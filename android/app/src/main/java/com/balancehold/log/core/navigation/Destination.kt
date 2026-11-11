package com.balancehold.log.core.navigation

sealed interface Screen {
    data object Splash : Screen
    data object Menu : Screen
    data class Game(val holdId: String) : Screen
    data class GameOver(
        val holdId: String,
        val durationMs: Long,
        val isNewBest: Boolean,
        val timestamp: Long
    ) : Screen
    data class HoldDetail(val holdId: String) : Screen
    data object Settings : Screen
}
