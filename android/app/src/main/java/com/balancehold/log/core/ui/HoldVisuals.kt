package com.balancehold.log.core.ui

import com.balancehold.log.R
import com.balancehold.log.domain.model.Difficulty
import com.balancehold.log.domain.model.HoldKind

fun holdNameRes(kind: HoldKind): Int = when (kind) {
    HoldKind.PLANK -> R.string.hold_plank
    HoldKind.ONE_LEG_STAND -> R.string.hold_one_leg_stand
    HoldKind.WALL_SIT -> R.string.hold_wall_sit
    HoldKind.TREE_POSE -> R.string.hold_tree_pose
    HoldKind.SIDE_PLANK -> R.string.hold_side_plank
}

fun holdSpriteRes(kind: HoldKind): Int = when (kind) {
    HoldKind.PLANK -> R.drawable.sprite_1
    HoldKind.ONE_LEG_STAND -> R.drawable.sprite_2
    HoldKind.WALL_SIT -> R.drawable.sprite_3
    HoldKind.TREE_POSE -> R.drawable.sprite_4
    HoldKind.SIDE_PLANK -> R.drawable.sprite_1
}

fun difficultyLabelRes(difficulty: Difficulty): Int = when (difficulty) {
    Difficulty.EASY -> R.string.diff_easy
    Difficulty.STEADY -> R.string.diff_steady
    Difficulty.HARD -> R.string.diff_hard
}
