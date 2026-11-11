package com.balancehold.log.domain.usecase

import com.balancehold.log.domain.model.Attempt
import com.balancehold.log.domain.model.WeekStat

class GetWeeklyStatsUseCase {

    operator fun invoke(attempts: List<Attempt>, now: Long, weeks: Int): List<WeekStat> {
        val span = 7L * 24L * 60L * 60L * 1000L
        return (0 until weeks).map { index ->
            val from = now - span * (index + 1)
            val until = now - span * index
            val slice = attempts.filter { it.timestamp >= from && it.timestamp < until }
            WeekStat(
                weeksAgo = index,
                bestMs = slice.maxOfOrNull { it.durationMs } ?: 0L,
                averageMs = if (slice.isEmpty()) 0L else slice.sumOf { it.durationMs } / slice.size
            )
        }.reversed()
    }
}
