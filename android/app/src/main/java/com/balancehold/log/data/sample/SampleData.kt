package com.balancehold.log.data.sample

import com.balancehold.log.domain.model.Attempt
import com.balancehold.log.domain.model.Difficulty
import com.balancehold.log.domain.model.Hold
import com.balancehold.log.domain.model.HoldKind
import com.balancehold.log.domain.model.HoldSide

object SampleData {

    const val HOLD_PLANK = "plank"
    const val HOLD_ONE_LEG = "one_leg_stand"
    const val HOLD_WALL_SIT = "wall_sit"
    const val HOLD_TREE_POSE = "tree_pose"
    const val HOLD_SIDE_PLANK = "side_plank"

    private const val HOUR = 3_600_000L
    private const val DAY = 24L * HOUR

    fun holds(): List<Hold> = listOf(
        Hold(HOLD_PLANK, HoldKind.PLANK, false, listOf(30_000L, 60_000L, 120_000L)),
        Hold(HOLD_ONE_LEG, HoldKind.ONE_LEG_STAND, true, listOf(30_000L, 60_000L, 90_000L)),
        Hold(HOLD_WALL_SIT, HoldKind.WALL_SIT, false, listOf(45_000L, 90_000L, 180_000L)),
        Hold(HOLD_TREE_POSE, HoldKind.TREE_POSE, true, listOf(30_000L, 60_000L, 120_000L)),
        Hold(HOLD_SIDE_PLANK, HoldKind.SIDE_PLANK, true, listOf(20_000L, 45_000L, 90_000L))
    )

    fun seedAttempts(now: Long): List<Attempt> = listOf(
        Attempt("seed_1", HOLD_WALL_SIT, 51_400L, now - 3L * HOUR, HoldSide.NONE, Difficulty.STEADY, "steady breathing"),
        Attempt("seed_2", HOLD_PLANK, 42_800L, now - 2L * DAY, HoldSide.NONE, Difficulty.STEADY, ""),
        Attempt("seed_3", HOLD_ONE_LEG, 22_300L, now - 3L * DAY, HoldSide.LEFT, Difficulty.HARD, "shaky at the end"),
        Attempt("seed_4", HOLD_WALL_SIT, 64_100L, now - 8L * DAY, HoldSide.NONE, Difficulty.HARD, ""),
        Attempt("seed_5", HOLD_PLANK, 58_600L, now - 9L * DAY, HoldSide.NONE, Difficulty.HARD, ""),
        Attempt("seed_6", HOLD_ONE_LEG, 31_200L, now - 10L * DAY, HoldSide.RIGHT, Difficulty.STEADY, ""),
        Attempt("seed_7", HOLD_PLANK, 74_900L, now - 16L * DAY, HoldSide.NONE, Difficulty.STEADY, ""),
        Attempt("seed_8", HOLD_WALL_SIT, 45_500L, now - 20L * DAY, HoldSide.NONE, Difficulty.EASY, ""),
        Attempt("seed_9", HOLD_ONE_LEG, 27_700L, now - 23L * DAY, HoldSide.LEFT, Difficulty.EASY, "")
    )
}
