package com.balancehold.log.presentation.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.balancehold.log.domain.model.Attempt
import com.balancehold.log.domain.model.Hold
import com.balancehold.log.domain.repository.HoldRepository
import java.util.Calendar
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MenuViewModel(holdRepository: HoldRepository) : ViewModel() {

    private val _state = MutableStateFlow(MenuUiState())
    val state: StateFlow<MenuUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            combine(holdRepository.holds, holdRepository.attempts) { holds, attempts ->
                build(holds, attempts)
            }.collect { snapshot ->
                _state.update { current ->
                    snapshot.copy(
                        selectedHoldId = current.selectedHoldId.takeIf { chosen ->
                            snapshot.holds.any { it.id == chosen }
                        } ?: snapshot.holds.firstOrNull()?.id.orEmpty()
                    )
                }
            }
        }
    }

    fun select(holdId: String) {
        _state.update { it.copy(selectedHoldId = holdId) }
    }

    private fun build(holds: List<Hold>, attempts: List<Attempt>): MenuUiState {
        val now = System.currentTimeMillis()
        val dayStart = startOfDay(now)
        val todayAttempts = attempts.filter { it.timestamp >= dayStart }
        val leader = todayAttempts.maxByOrNull { it.durationMs }
        val rows = holds.map { hold ->
            val forHold = attempts.filter { it.holdId == hold.id }
            HoldRow(
                id = hold.id,
                kind = hold.kind,
                bestMs = forHold.maxOfOrNull { it.durationMs } ?: 0L,
                lastMs = forHold.maxByOrNull { it.timestamp }?.durationMs ?: 0L,
                leadsToday = leader != null && leader.holdId == hold.id
            )
        }
        return MenuUiState(
            loading = false,
            holds = rows,
            selectedHoldId = rows.firstOrNull()?.id.orEmpty(),
            totalAttempts = attempts.size,
            bestTodayMs = todayAttempts.maxOfOrNull { it.durationMs } ?: 0L
        )
    }

    private fun startOfDay(now: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = now
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
}
