package com.balancehold.log.core.di

import android.content.Context
import com.balancehold.log.data.local.HoldPreferences
import com.balancehold.log.data.repository.HoldRepositoryImpl
import com.balancehold.log.data.repository.SettingsRepositoryImpl
import com.balancehold.log.domain.repository.HoldRepository
import com.balancehold.log.domain.repository.SettingsRepository

object ServiceLocator {

    private var holds: HoldRepository? = null
    private var preferences: SettingsRepository? = null

    fun init(context: Context) {
        if (holds != null && preferences != null) return
        val store = HoldPreferences(context.applicationContext)
        holds = HoldRepositoryImpl(store)
        preferences = SettingsRepositoryImpl(store)
    }

    fun factory(args: ScreenArgs = ScreenArgs()): AppViewModelFactory = AppViewModelFactory(
        holdRepository = requireNotNull(holds),
        settingsRepository = requireNotNull(preferences),
        args = args
    )
}
