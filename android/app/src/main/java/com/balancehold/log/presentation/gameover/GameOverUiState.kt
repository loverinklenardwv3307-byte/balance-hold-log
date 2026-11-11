package com.balancehold.log.presentation.gameover

import com.balancehold.log.domain.model.Difficulty
import com.balancehold.log.domain.model.HoldKind
import com.balancehold.log.domain.model.HoldSide

data class GameOverUiState(
    val kind: HoldKind = HoldKind.PLANK,
    val hasSides: Boolean = false,
    val durationMs: Long = 0L,
    val isNewBest: Boolean = false,
    val side: HoldSide = HoldSide.NONE,
    val difficulty: Difficulty = Difficulty.STEADY,
    val note: String = "",
    val bestMs: Long = 0L,
    val attemptCount: Int = 0,
    val stored: Boolean = false
)

sealed interface GameOverEvent {
    data object ToMenu : GameOverEvent
    data class Repeat(val holdId: String) : GameOverEvent
}
