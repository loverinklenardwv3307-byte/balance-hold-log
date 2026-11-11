package com.balancehold.log.data.repository

import com.balancehold.log.data.local.HoldPreferences
import com.balancehold.log.domain.model.AppSettings
import com.balancehold.log.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsRepositoryImpl(private val preferences: HoldPreferences) : SettingsRepository {

    private val _settings = MutableStateFlow(
        AppSettings(
            countdownSeconds = preferences.countdownSeconds(),
            soundsEnabled = preferences.soundsEnabled(),
            darkTheme = preferences.darkTheme()
        )
    )
    override val settings: StateFlow<AppSettings> = _settings.asStateFlow()

    override fun update(settings: AppSettings) {
        _settings.value = settings
        preferences.storeSettings(
            countdownSeconds = settings.countdownSeconds,
            soundsEnabled = settings.soundsEnabled,
            darkTheme = settings.darkTheme
        )
    }

    override fun storeExport(content: String) {
        preferences.storeExport(content)
    }
}
