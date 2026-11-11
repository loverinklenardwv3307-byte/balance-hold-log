package com.balancehold.log.presentation.menu

import com.balancehold.log.domain.model.HoldKind

data class HoldRow(
    val id: String,
    val kind: HoldKind,
    val bestMs: Long,
    val lastMs: Long,
    val leadsToday: Boolean
)

data class MenuUiState(
    val loading: Boolean = true,
    val holds: List<HoldRow> = emptyList(),
    val selectedHoldId: String = "",
    val totalAttempts: Int = 0,
    val bestTodayMs: Long = 0L
)
