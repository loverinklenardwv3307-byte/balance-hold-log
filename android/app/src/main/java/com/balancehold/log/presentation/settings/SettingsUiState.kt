package com.balancehold.log.presentation.settings

data class SettingsUiState(
    val countdownSeconds: Int = 3,
    val soundsEnabled: Boolean = true,
    val darkTheme: Boolean = false,
    val exportedCount: Int = -1
)
