package com.balancehold.log.core.di

data class ScreenArgs(
    val holdId: String = "",
    val durationMs: Long = 0L,
    val isNewBest: Boolean = false,
    val timestamp: Long = 0L
)
