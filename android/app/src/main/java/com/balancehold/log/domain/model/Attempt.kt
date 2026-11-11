package com.balancehold.log.domain.model

data class Attempt(
    val id: String,
    val holdId: String,
    val durationMs: Long,
    val timestamp: Long,
    val side: HoldSide,
    val difficulty: Difficulty,
    val note: String
)
