package com.balancehold.log.core.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.balancehold.log.domain.repository.HoldRepository
import com.balancehold.log.domain.repository.SettingsRepository
import com.balancehold.log.domain.usecase.GetNextMilestoneUseCase
import com.balancehold.log.domain.usecase.GetWeeklyStatsUseCase
import com.balancehold.log.domain.usecase.SaveAttemptUseCase
import com.balancehold.log.presentation.app.AppViewModel
import com.balancehold.log.presentation.game.GameViewModel
import com.balancehold.log.presentation.gameover.GameOverViewModel
import com.balancehold.log.presentation.holddetail.HoldDetailViewModel
import com.balancehold.log.presentation.menu.MenuViewModel
import com.balancehold.log.presentation.settings.SettingsViewModel
import com.balancehold.log.presentation.splash.SplashViewModel

class AppViewModelFactory(
    private val holdRepository: HoldRepository,
    private val settingsRepository: SettingsRepository,
    private val args: ScreenArgs
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val created: ViewModel = when {
            modelClass.isAssignableFrom(AppViewModel::class.java) ->
                AppViewModel(settingsRepository)

            modelClass.isAssignableFrom(SplashViewModel::class.java) ->
                SplashViewModel()

            modelClass.isAssignableFrom(MenuViewModel::class.java) ->
                MenuViewModel(holdRepository)

            modelClass.isAssignableFrom(GameViewModel::class.java) ->
                GameViewModel(holdRepository, settingsRepository, args.holdId)

            modelClass.isAssignableFrom(GameOverViewModel::class.java) ->
                GameOverViewModel(
                    holdRepository,
                    SaveAttemptUseCase(holdRepository),
                    args.holdId,
                    args.durationMs,
                    args.isNewBest,
                    args.timestamp
                )

            modelClass.isAssignableFrom(HoldDetailViewModel::class.java) ->
                HoldDetailViewModel(
                    holdRepository,
                    GetWeeklyStatsUseCase(),
                    GetNextMilestoneUseCase(),
                    args.holdId
                )

            modelClass.isAssignableFrom(SettingsViewModel::class.java) ->
                SettingsViewModel(settingsRepository, holdRepository)

            else -> throw IllegalArgumentException(modelClass.name)
        }
        return created as T
    }
}
