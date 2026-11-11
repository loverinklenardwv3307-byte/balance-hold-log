package com.balancehold.log.presentation.holddetail

import com.balancehold.log.domain.model.Difficulty
import com.balancehold.log.domain.model.HoldKind

data class AttemptRow(
    val id: String,
    val durationMs: Long,
    val daysAgo: Int,
    val difficulty: Difficulty,
    val sideLabel: Int?
)

data class HoldDetailUiState(
    val loading: Boolean = true,
    val kind: HoldKind = HoldKind.PLANK,
    val bestMs: Long = 0L,
    val milestonesMs: List<Long> = emptyList(),
    val nextMilestoneMs: Long = 0L,
    val weekBest: List<Long> = emptyList(),
    val weekAverage: List<Long> = emptyList(),
    val attempts: List<AttemptRow> = emptyList()
)
