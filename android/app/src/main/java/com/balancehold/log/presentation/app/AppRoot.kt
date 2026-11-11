package com.balancehold.log.presentation.app

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.balancehold.log.core.di.ServiceLocator
import com.balancehold.log.core.navigation.Screen
import com.balancehold.log.core.theme.AppTheme
import com.balancehold.log.core.theme.LocalAppPalette
import com.balancehold.log.presentation.game.GameScreen
import com.balancehold.log.presentation.gameover.GameOverScreen
import com.balancehold.log.presentation.holddetail.HoldDetailScreen
import com.balancehold.log.presentation.menu.MenuScreen
import com.balancehold.log.presentation.settings.SettingsScreen
import com.balancehold.log.presentation.splash.SplashScreen

@Composable
fun AppRoot() {
    val appViewModel: AppViewModel = viewModel(factory = ServiceLocator.factory())
    val screen by appViewModel.screen.collectAsStateWithLifecycle()
    val canGoBack by appViewModel.canGoBack.collectAsStateWithLifecycle()
    val darkTheme by appViewModel.darkTheme.collectAsStateWithLifecycle()

    AppTheme(darkTheme = darkTheme) {
        val palette = LocalAppPalette.current
        BackHandler(enabled = canGoBack) { appViewModel.back() }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(palette.pageMid)
        ) {
            AnimatedContent(
                targetState = screen,
                transitionSpec = {
                    fadeIn(tween(durationMillis = 260)) togetherWith
                        fadeOut(tween(durationMillis = 200))
                },
                label = "screenTransition"
            ) { current ->
                when (current) {
                    Screen.Splash -> SplashScreen(
                        onFinished = { appViewModel.toMenu() }
                    )

                    Screen.Menu -> MenuScreen(
                        onStartHold = { holdId -> appViewModel.navigate(Screen.Game(holdId)) },
                        onOpenHold = { holdId -> appViewModel.navigate(Screen.HoldDetail(holdId)) },
                        onOpenSettings = { appViewModel.navigate(Screen.Settings) }
                    )

                    is Screen.Game -> GameScreen(
                        holdId = current.holdId,
                        onBack = { appViewModel.back() },
                        onFinished = { holdId, durationMs, isNewBest, timestamp ->
                            appViewModel.replace(
                                Screen.GameOver(holdId, durationMs, isNewBest, timestamp)
                            )
                        }
                    )

                    is Screen.GameOver -> GameOverScreen(
                        holdId = current.holdId,
                        durationMs = current.durationMs,
                        isNewBest = current.isNewBest,
                        timestamp = current.timestamp,
                        onMenu = { appViewModel.toMenu() },
                        onRepeat = { holdId -> appViewModel.replace(Screen.Game(holdId)) }
                    )

                    is Screen.HoldDetail -> HoldDetailScreen(
                        holdId = current.holdId,
                        onBack = { appViewModel.back() },
                        onStartHold = { holdId -> appViewModel.navigate(Screen.Game(holdId)) }
                    )

                    Screen.Settings -> SettingsScreen(
                        onBack = { appViewModel.back() }
                    )
                }
            }
        }
    }
}
