package com.balancehold.log.domain.usecase

import com.balancehold.log.domain.model.Attempt
import com.balancehold.log.domain.model.SaveOutcome
import com.balancehold.log.domain.repository.HoldRepository

class SaveAttemptUseCase(private val holdRepository: HoldRepository) {

    operator fun invoke(attempt: Attempt): SaveOutcome {
        holdRepository.addAttempt(attempt)
        val forHold = holdRepository.attempts.value.filter { it.holdId == attempt.holdId }
        return SaveOutcome(
            bestMs = forHold.maxOfOrNull { it.durationMs } ?: 0L,
            attemptCount = forHold.size
        )
    }
}
