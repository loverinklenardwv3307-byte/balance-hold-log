package com.balancehold.log.domain.usecase

class GetNextMilestoneUseCase {

    operator fun invoke(milestonesMs: List<Long>, bestMs: Long): Long {
        val sorted = milestonesMs.sorted()
        return sorted.firstOrNull { it > bestMs } ?: (sorted.lastOrNull() ?: 0L)
    }
}
