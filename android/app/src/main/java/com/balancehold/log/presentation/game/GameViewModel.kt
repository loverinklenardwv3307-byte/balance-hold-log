package com.balancehold.log.presentation.game

import android.os.SystemClock
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.balancehold.log.core.config.GameConfig
import com.balancehold.log.domain.model.HoldKind
import com.balancehold.log.domain.model.HoldSide
import com.balancehold.log.domain.repository.HoldRepository
import com.balancehold.log.domain.repository.SettingsRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class GameViewModel(
    private val holdRepository: HoldRepository,
    private val settingsRepository: SettingsRepository,
    private val holdId: String
) : ViewModel() {

    private val _state = MutableStateFlow(GameUiState())
    val state: StateFlow<GameUiState> = _state.asStateFlow()

    private val _events = Channel<GameEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private var tickJob: Job? = null
    private var countdownJob: Job? = null
    private var baseElapsedMs = 0L
    private var startMark = 0L

    init {
        reset()
    }

    fun reset() {
        tickJob?.cancel()
        countdownJob?.cancel()
        baseElapsedMs = 0L
        startMark = 0L
        val hold = holdRepository.holds.value.firstOrNull { it.id == holdId }
        val best = holdRepository.attempts.value
            .filter { it.holdId == holdId }
            .maxOfOrNull { it.durationMs } ?: 0L
        val settings = settingsRepository.settings.value
        _state.value = GameUiState(
            kind = hold?.kind ?: HoldKind.PLANK,
            hasSides = hold?.hasSides == true,
            phase = GamePhase.READY,
            countdownSeconds = settings.countdownSeconds,
            personalBestMs = best,
            side = if (hold?.hasSides == true) HoldSide.LEFT else HoldSide.NONE,
            feedbackEnabled = settings.soundsEnabled
        )
    }

    fun onScreenTap() {
        when (_state.value.phase) {
            GamePhase.READY -> beginSession()
            GamePhase.RUNNING -> finishRound()
            GamePhase.PAUSED -> resumeRound()
            GamePhase.COUNTDOWN -> Unit
            GamePhase.FINISHED -> Unit
        }
    }

    fun togglePause() {
        when (_state.value.phase) {
            GamePhase.RUNNING -> pause()
            GamePhase.PAUSED -> resumeRound()
            else -> Unit
        }
    }

    fun pause() {
        if (_state.value.phase != GamePhase.RUNNING) return
        tickJob?.cancel()
        baseElapsedMs = currentElapsed()
        _state.update { it.copy(phase = GamePhase.PAUSED, elapsedMs = baseElapsedMs) }
    }

    fun finish() {
        val phase = _state.value.phase
        if (phase == GamePhase.RUNNING || phase == GamePhase.PAUSED) {
            finishRound()
        }
    }

    fun selectSide(side: HoldSide) {
        _state.update { it.copy(side = side) }
    }

    private fun beginSession() {
        val seconds = _state.value.countdownSeconds
        if (seconds <= 0) {
            startWatch()
            return
        }
        _state.update {
            it.copy(
                phase = GamePhase.COUNTDOWN,
                countdownValue = seconds,
                beatTick = it.beatTick + 1
            )
        }
        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            var remaining = seconds
            while (remaining > 0) {
                delay(1000L)
                remaining -= 1
                if (remaining > 0) {
                    _state.update {
                        it.copy(countdownValue = remaining, beatTick = it.beatTick + 1)
                    }
                }
            }
            startWatch()
        }
    }

    private fun startWatch() {
        baseElapsedMs = 0L
        startMark = SystemClock.elapsedRealtime()
        _state.update { it.copy(phase = GamePhase.RUNNING, elapsedMs = 0L, countdownValue = 0) }
        launchTicker()
    }

    private fun resumeRound() {
        startMark = SystemClock.elapsedRealtime()
        _state.update { it.copy(phase = GamePhase.RUNNING) }
        launchTicker()
    }

    private fun currentElapsed(): Long =
        baseElapsedMs + (SystemClock.elapsedRealtime() - startMark)

    private fun launchTicker() {
        tickJob?.cancel()
        tickJob = viewModelScope.launch {
            while (isActive) {
                delay(GameConfig.TICK_MS)
                val elapsed = currentElapsed()
                _state.update { current ->
                    val crossed = current.personalBestMs > 0L &&
                        elapsed > current.personalBestMs &&
                        !current.isNewBest
                    current.copy(
                        elapsedMs = elapsed,
                        pulseTick = (elapsed / GameConfig.PULSE_INTERVAL_MS).toInt(),
                        isNewBest = current.isNewBest || crossed,
                        newBestFlash = if (crossed) current.newBestFlash + 1 else current.newBestFlash
                    )
                }
            }
        }
    }

    private fun finishRound() {
        tickJob?.cancel()
        countdownJob?.cancel()
        val current = _state.value
        val elapsed = if (current.phase == GamePhase.PAUSED) baseElapsedMs else currentElapsed()
        _state.update { it.copy(phase = GamePhase.FINISHED, elapsedMs = elapsed) }
        viewModelScope.launch {
            _events.send(
                GameEvent.Finished(
                    holdId = holdId,
                    durationMs = elapsed,
                    isNewBest = elapsed > current.personalBestMs,
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }

    override fun onCleared() {
        tickJob?.cancel()
        countdownJob?.cancel()
        super.onCleared()
    }
}
