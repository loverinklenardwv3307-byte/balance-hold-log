package com.balancehold.log.presentation.holddetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.balancehold.log.R
import com.balancehold.log.core.config.GameConfig
import com.balancehold.log.domain.model.Attempt
import com.balancehold.log.domain.model.Hold
import com.balancehold.log.domain.model.HoldKind
import com.balancehold.log.domain.model.HoldSide
import com.balancehold.log.domain.repository.HoldRepository
import com.balancehold.log.domain.usecase.GetNextMilestoneUseCase
import com.balancehold.log.domain.usecase.GetWeeklyStatsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class HoldDetailViewModel(
    private val holdRepository: HoldRepository,
    private val weeklyStats: GetWeeklyStatsUseCase,
    private val nextMilestone: GetNextMilestoneUseCase,
    private val holdId: String
) : ViewModel() {

    private val _state = MutableStateFlow(HoldDetailUiState())
    val state: StateFlow<HoldDetailUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            combine(holdRepository.holds, holdRepository.attempts) { holds, attempts ->
                build(holds.firstOrNull { it.id == holdId }, attempts.filter { it.holdId == holdId })
            }.collect { snapshot ->
                _state.value = snapshot
            }
        }
    }

    fun raiseTarget() {
        shiftTarget(GameConfig.MILESTONE_STEP_MS)
    }

    fun lowerTarget() {
        shiftTarget(-GameConfig.MILESTONE_STEP_MS)
    }

    private fun shiftTarget(deltaMs: Long) {
        val current = _state.value
        if (current.milestonesMs.isEmpty()) return
        val target = current.nextMilestoneMs
        val updated = current.milestonesMs.map { value ->
            if (value == target) {
                (value + deltaMs).coerceIn(GameConfig.MILESTONE_MIN_MS, GameConfig.MILESTONE_MAX_MS)
            } else {
                value
            }
        }
        holdRepository.updateMilestones(holdId, updated)
    }

    private fun build(hold: Hold?, attempts: List<Attempt>): HoldDetailUiState {
        val now = System.currentTimeMillis()
        val best = attempts.maxOfOrNull { it.durationMs } ?: 0L
        val milestones = hold?.milestonesMs.orEmpty().sorted()
        val weeks = weeklyStats(attempts, now, GameConfig.CHART_WEEKS)
        return HoldDetailUiState(
            loading = false,
            kind = hold?.kind ?: HoldKind.PLANK,
            bestMs = best,
            milestonesMs = milestones,
            nextMilestoneMs = nextMilestone(milestones, best),
            weekBest = weeks.map { it.bestMs },
            weekAverage = weeks.map { it.averageMs },
            attempts = attempts.map { attempt ->
                AttemptRow(
                    id = attempt.id,
                    durationMs = attempt.durationMs,
                    daysAgo = ((now - attempt.timestamp) / 86_400_000L).toInt().coerceAtLeast(0),
                    difficulty = attempt.difficulty,
                    sideLabel = when (attempt.side) {
                        HoldSide.LEFT -> R.string.side_left
                        HoldSide.RIGHT -> R.string.side_right
                        HoldSide.NONE -> null
                    }
                )
            }
        )
    }
}
