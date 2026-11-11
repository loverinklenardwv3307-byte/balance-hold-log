package com.balancehold.log.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.balancehold.log.domain.model.AppSettings
import com.balancehold.log.domain.repository.HoldRepository
import com.balancehold.log.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val holdRepository: HoldRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsUiState())
    val state: StateFlow<SettingsUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            settingsRepository.settings.collect { settings ->
                _state.update {
                    it.copy(
                        countdownSeconds = settings.countdownSeconds,
                        soundsEnabled = settings.soundsEnabled,
                        darkTheme = settings.darkTheme
                    )
                }
            }
        }
    }

    fun setCountdown(seconds: Int) {
        commit(_state.value.copy(countdownSeconds = seconds))
    }

    fun setSounds(enabled: Boolean) {
        commit(_state.value.copy(soundsEnabled = enabled))
    }

    fun setDarkTheme(enabled: Boolean) {
        commit(_state.value.copy(darkTheme = enabled))
    }

    fun exportLog() {
        val attempts = holdRepository.attempts.value
        val report = attempts.joinToString("\n") { attempt ->
            listOf(
                attempt.holdId,
                attempt.durationMs.toString(),
                attempt.timestamp.toString(),
                attempt.side.name,
                attempt.difficulty.name,
                attempt.note
            ).joinToString(" ")
        }
        settingsRepository.storeExport(report)
        _state.update { it.copy(exportedCount = attempts.size) }
    }

    private fun commit(next: SettingsUiState) {
        _state.value = next
        settingsRepository.update(
            AppSettings(
                countdownSeconds = next.countdownSeconds,
                soundsEnabled = next.soundsEnabled,
                darkTheme = next.darkTheme
            )
        )
    }
}
