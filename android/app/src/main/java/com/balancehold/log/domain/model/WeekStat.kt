package com.balancehold.log.domain.model

data class WeekStat(
    val weeksAgo: Int,
    val bestMs: Long,
    val averageMs: Long
)
