package com.balancehold.log.presentation.game

import com.balancehold.log.core.config.GameConfig
import com.balancehold.log.domain.model.HoldKind
import com.balancehold.log.domain.model.HoldSide

enum class GamePhase {
    READY,
    COUNTDOWN,
    RUNNING,
    PAUSED,
    FINISHED
}

data class GameUiState(
    val kind: HoldKind = HoldKind.PLANK,
    val hasSides: Boolean = false,
    val phase: GamePhase = GamePhase.READY,
    val elapsedMs: Long = 0L,
    val countdownValue: Int = 0,
    val countdownSeconds: Int = GameConfig.DEFAULT_COUNTDOWN_SECONDS,
    val personalBestMs: Long = 0L,
    val side: HoldSide = HoldSide.NONE,
    val isNewBest: Boolean = false,
    val newBestFlash: Int = 0,
    val pulseTick: Int = 0,
    val beatTick: Int = 0,
    val feedbackEnabled: Boolean = true
)

sealed interface GameEvent {
    data class Finished(
        val holdId: String,
        val durationMs: Long,
        val isNewBest: Boolean,
        val timestamp: Long
    ) : GameEvent
}
