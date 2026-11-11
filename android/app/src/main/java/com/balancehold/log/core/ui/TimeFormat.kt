package com.balancehold.log.core.ui

import java.util.Locale

fun formatClock(durationMs: Long): String {
    val total = if (durationMs < 0L) 0L else durationMs
    val minutes = total / 60_000L
    val seconds = (total / 1_000L) % 60L
    return String.format(Locale.US, "%02d:%02d", minutes, seconds)
}

fun formatTenths(durationMs: Long): String {
    val total = if (durationMs < 0L) 0L else durationMs
    return String.format(Locale.US, ".%01d", (total / 100L) % 10L)
}

fun formatFull(durationMs: Long): String = formatClock(durationMs) + formatTenths(durationMs)
