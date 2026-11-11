package com.balancehold.log.domain.model

data class Hold(
    val id: String,
    val kind: HoldKind,
    val hasSides: Boolean,
    val milestonesMs: List<Long>
)
