package com.balancehold.log.presentation.gameover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.balancehold.log.core.config.GameConfig
import com.balancehold.log.domain.model.Attempt
import com.balancehold.log.domain.model.Difficulty
import com.balancehold.log.domain.model.HoldKind
import com.balancehold.log.domain.model.HoldSide
import com.balancehold.log.domain.repository.HoldRepository
import com.balancehold.log.domain.usecase.SaveAttemptUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GameOverViewModel(
    holdRepository: HoldRepository,
    private val saveAttemptUseCase: SaveAttemptUseCase,
    private val holdId: String,
    durationMs: Long,
    isNewBest: Boolean,
    private val timestamp: Long
) : ViewModel() {

    private val _state: MutableStateFlow<GameOverUiState>
    val state: StateFlow<GameOverUiState>

    private val _events = Channel<GameOverEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        val hold = holdRepository.holds.value.firstOrNull { it.id == holdId }
        val previous = holdRepository.attempts.value.filter { it.holdId == holdId }
        _state = MutableStateFlow(
            GameOverUiState(
                kind = hold?.kind ?: HoldKind.PLANK,
                hasSides = hold?.hasSides == true,
                durationMs = durationMs,
                isNewBest = isNewBest,
                side = if (hold?.hasSides == true) HoldSide.LEFT else HoldSide.NONE,
                bestMs = previous.maxOfOrNull { it.durationMs } ?: 0L,
                attemptCount = previous.size
            )
        )
        state = _state.asStateFlow()
    }

    fun selectSide(side: HoldSide) {
        _state.update { it.copy(side = side) }
    }

    fun selectDifficulty(difficulty: Difficulty) {
        _state.update { it.copy(difficulty = difficulty) }
    }

    fun updateNote(value: String) {
        _state.update { it.copy(note = value.take(GameConfig.NOTE_MAX_LENGTH)) }
    }

    fun keepAndLeave() {
        persist()
        viewModelScope.launch { _events.send(GameOverEvent.ToMenu) }
    }

    fun keepAndRepeat() {
        persist()
        viewModelScope.launch { _events.send(GameOverEvent.Repeat(holdId)) }
    }

    fun discard() {
        viewModelScope.launch { _events.send(GameOverEvent.ToMenu) }
    }

    private fun persist() {
        val current = _state.value
        if (current.stored) return
        val outcome = saveAttemptUseCase(
            Attempt(
                id = "attempt_$timestamp",
                holdId = holdId,
                durationMs = current.durationMs,
                timestamp = timestamp,
                side = current.side,
                difficulty = current.difficulty,
                note = current.note
            )
        )
        _state.update {
            it.copy(stored = true, bestMs = outcome.bestMs, attemptCount = outcome.attemptCount)
        }
    }
}
