package com.balancehold.log.data.repository

import com.balancehold.log.data.local.HoldPreferences
import com.balancehold.log.data.sample.SampleData
import com.balancehold.log.domain.model.Attempt
import com.balancehold.log.domain.model.Difficulty
import com.balancehold.log.domain.model.Hold
import com.balancehold.log.domain.model.HoldSide
import com.balancehold.log.domain.repository.HoldRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HoldRepositoryImpl(private val preferences: HoldPreferences) : HoldRepository {

    private val _holds = MutableStateFlow<List<Hold>>(emptyList())
    override val holds: StateFlow<List<Hold>> = _holds.asStateFlow()

    private val _attempts = MutableStateFlow<List<Attempt>>(emptyList())
    override val attempts: StateFlow<List<Attempt>> = _attempts.asStateFlow()

    init {
        if (!preferences.seeded()) {
            preferences.storeAttempts(
                encodeAttempts(SampleData.seedAttempts(System.currentTimeMillis()))
            )
            preferences.markSeeded()
        }
        val overrides = decodeMilestones(preferences.milestonesRaw())
        _holds.value = SampleData.holds().map { hold ->
            val custom = overrides[hold.id]
            if (custom == null) hold else hold.copy(milestonesMs = custom)
        }
        _attempts.value = decodeAttempts(preferences.attemptsRaw())
    }

    override fun addAttempt(attempt: Attempt) {
        val updated = (_attempts.value + attempt).sortedByDescending { it.timestamp }
        _attempts.value = updated
        preferences.storeAttempts(encodeAttempts(updated))
    }

    override fun updateMilestones(holdId: String, milestonesMs: List<Long>) {
        val sorted = milestonesMs.sorted()
        _holds.value = _holds.value.map { hold ->
            if (hold.id == holdId) hold.copy(milestonesMs = sorted) else hold
        }
        preferences.storeMilestones(
            encodeMilestones(_holds.value.associate { it.id to it.milestonesMs })
        )
    }

    private fun encodeAttempts(items: List<Attempt>): String = items.joinToString(ROW) { attempt ->
        listOf(
            attempt.id,
            attempt.holdId,
            attempt.durationMs.toString(),
            attempt.timestamp.toString(),
            attempt.side.name,
            attempt.difficulty.name,
            sanitize(attempt.note)
        ).joinToString(FIELD)
    }

    private fun decodeAttempts(raw: String): List<Attempt> {
        if (raw.isBlank()) return emptyList()
        return raw.split(ROW).mapNotNull { row ->
            val parts = row.split(FIELD)
            if (parts.size < 7) return@mapNotNull null
            val duration = parts[2].toLongOrNull() ?: return@mapNotNull null
            val stamp = parts[3].toLongOrNull() ?: return@mapNotNull null
            Attempt(
                id = parts[0],
                holdId = parts[1],
                durationMs = duration,
                timestamp = stamp,
                side = runCatching { HoldSide.valueOf(parts[4]) }.getOrDefault(HoldSide.NONE),
                difficulty = runCatching { Difficulty.valueOf(parts[5]) }
                    .getOrDefault(Difficulty.STEADY),
                note = parts[6]
            )
        }.sortedByDescending { it.timestamp }
    }

    private fun encodeMilestones(values: Map<String, List<Long>>): String =
        values.entries.joinToString(ROW) { entry ->
            entry.key + FIELD + entry.value.joinToString(VALUE)
        }

    private fun decodeMilestones(raw: String): Map<String, List<Long>> {
        if (raw.isBlank()) return emptyMap()
        return raw.split(ROW).mapNotNull { row ->
            val parts = row.split(FIELD)
            if (parts.size < 2) return@mapNotNull null
            val values = parts[1].split(VALUE).mapNotNull { it.toLongOrNull() }
            if (values.isEmpty()) null else parts[0] to values
        }.toMap()
    }

    private fun sanitize(note: String): String = note
        .replace(ROW, " ")
        .replace(FIELD, " ")
        .replace("\n", " ")
        .trim()

    private companion object {
        const val ROW = "\u001E"
        const val FIELD = "\u001F"
        const val VALUE = ","
    }
}
