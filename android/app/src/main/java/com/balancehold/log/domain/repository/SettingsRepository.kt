package com.balancehold.log.domain.repository

import com.balancehold.log.domain.model.AppSettings
import kotlinx.coroutines.flow.StateFlow

interface SettingsRepository {
    val settings: StateFlow<AppSettings>
    fun update(settings: AppSettings)
    fun storeExport(content: String)
}
