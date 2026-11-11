package com.balancehold.log.domain.model

data class AppSettings(
    val countdownSeconds: Int,
    val soundsEnabled: Boolean,
    val darkTheme: Boolean
)
