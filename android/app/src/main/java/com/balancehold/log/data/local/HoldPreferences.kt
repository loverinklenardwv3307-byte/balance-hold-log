package com.balancehold.log.data.local

import android.content.Context
import android.content.SharedPreferences
import com.balancehold.log.core.config.GameConfig

class HoldPreferences(context: Context) {

    private val preferences: SharedPreferences =
        context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE)

    fun attemptsRaw(): String = preferences.getString(KEY_ATTEMPTS, "").orEmpty()

    fun storeAttempts(raw: String) {
        preferences.edit().putString(KEY_ATTEMPTS, raw).apply()
    }

    fun milestonesRaw(): String = preferences.getString(KEY_MILESTONES, "").orEmpty()

    fun storeMilestones(raw: String) {
        preferences.edit().putString(KEY_MILESTONES, raw).apply()
    }

    fun seeded(): Boolean = preferences.getBoolean(KEY_SEEDED, false)

    fun markSeeded() {
        preferences.edit().putBoolean(KEY_SEEDED, true).apply()
    }

    fun countdownSeconds(): Int =
        preferences.getInt(KEY_COUNTDOWN, GameConfig.DEFAULT_COUNTDOWN_SECONDS)

    fun soundsEnabled(): Boolean = preferences.getBoolean(KEY_SOUNDS, true)

    fun darkTheme(): Boolean = preferences.getBoolean(KEY_DARK, false)

    fun storeSettings(countdownSeconds: Int, soundsEnabled: Boolean, darkTheme: Boolean) {
        preferences.edit()
            .putInt(KEY_COUNTDOWN, countdownSeconds)
            .putBoolean(KEY_SOUNDS, soundsEnabled)
            .putBoolean(KEY_DARK, darkTheme)
            .apply()
    }

    fun storeExport(content: String) {
        preferences.edit().putString(KEY_EXPORT, content).apply()
    }

    private companion object {
        const val STORE_NAME = "balance_hold_log_store"
        const val KEY_ATTEMPTS = "attempts"
        const val KEY_MILESTONES = "milestones"
        const val KEY_SEEDED = "seeded"
        const val KEY_COUNTDOWN = "countdown_seconds"
        const val KEY_SOUNDS = "sounds_enabled"
        const val KEY_DARK = "dark_theme"
        const val KEY_EXPORT = "export_log"
    }
}
