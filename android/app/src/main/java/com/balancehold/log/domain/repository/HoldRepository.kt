package com.balancehold.log.domain.repository

import com.balancehold.log.domain.model.Attempt
import com.balancehold.log.domain.model.Hold
import kotlinx.coroutines.flow.StateFlow

interface HoldRepository {
    val holds: StateFlow<List<Hold>>
    val attempts: StateFlow<List<Attempt>>
    fun addAttempt(attempt: Attempt)
    fun updateMilestones(holdId: String, milestonesMs: List<Long>)
}
