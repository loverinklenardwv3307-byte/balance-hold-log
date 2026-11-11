package com.balancehold.log.presentation.game

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.balancehold.log.R
import com.balancehold.log.core.di.ScreenArgs
import com.balancehold.log.core.di.ServiceLocator
import com.balancehold.log.core.theme.LocalAppPalette
import com.balancehold.log.core.ui.AppBackground
import com.balancehold.log.core.ui.ChipRow
import com.balancehold.log.core.ui.HorizonLine
import com.balancehold.log.core.ui.StopwatchDisplay
import com.balancehold.log.core.ui.holdNameRes
import com.balancehold.log.domain.model.HoldSide

@Composable
fun GameScreen(
    holdId: String,
    onBack: () -> Unit,
    onFinished: (String, Long, Boolean, Long) -> Unit
) {
    val gameViewModel: GameViewModel = viewModel(
        key = "game-$holdId",
        factory = ServiceLocator.factory(ScreenArgs(holdId = holdId))
    )
    val state by gameViewModel.state.collectAsStateWithLifecycle()

    DisposableEffect(holdId) {
        gameViewModel.reset()
        onDispose { gameViewModel.pause() }
    }

    LaunchedEffect(Unit) {
        gameViewModel.events.collect { event ->
            when (event) {
                is GameEvent.Finished ->
                    onFinished(event.holdId, event.durationMs, event.isNewBest, event.timestamp)
            }
        }
    }

    GameContent(
        state = state,
        onTap = gameViewModel::onScreenTap,
        onTogglePause = gameViewModel::togglePause,
        onFinish = gameViewModel::finish,
        onSelectSide = gameViewModel::selectSide,
        onBack = onBack
    )
}

@Composable
private fun GameContent(
    state: GameUiState,
    onTap: () -> Unit,
    onTogglePause: () -> Unit,
    onFinish: () -> Unit,
    onSelectSide: (HoldSide) -> Unit,
    onBack: () -> Unit
) {
    val palette = LocalAppPalette.current
    val haptic = LocalHapticFeedback.current
    val tapZoneLabel = stringResource(R.string.cd_tap_zone)
    val phaseLabel = stringResource(
        when (state.phase) {
            GamePhase.READY -> R.string.phase_ready
            GamePhase.COUNTDOWN -> R.string.phase_countdown
            GamePhase.RUNNING -> R.string.holding
            GamePhase.PAUSED -> R.string.phase_paused
            GamePhase.FINISHED -> R.string.phase_finished
        }
    )

    val pulse = remember { Animatable(1f) }
    val beat = remember { Animatable(1f) }
    val wash = remember { Animatable(0f) }
    val glow by animateFloatAsState(
        targetValue = if (state.phase == GamePhase.RUNNING) 1f else 0f,
        animationSpec = tween(durationMillis = 600),
        label = "gameGlow"
    )

    LaunchedEffect(state.pulseTick) {
        if (state.pulseTick > 0) {
            if (state.feedbackEnabled) {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            }
            pulse.animateTo(1.035f, tween(durationMillis = 180))
            pulse.animateTo(1f, tween(durationMillis = 180))
        }
    }

    LaunchedEffect(state.beatTick) {
        if (state.beatTick > 0) {
            if (state.feedbackEnabled) {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            }
            beat.snapTo(1f)
            beat.animateTo(0.7f, tween(durationMillis = 760))
        }
    }

    LaunchedEffect(state.newBestFlash) {
        if (state.newBestFlash > 0) {
            wash.animateTo(0.45f, tween(durationMillis = 250))
            wash.animateTo(0f, tween(durationMillis = 250))
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onTap() }
            .semantics {
                contentDescription = tapZoneLabel
                stateDescription = phaseLabel
            }
    ) {
        AppBackground(
            gradient = listOf(
                palette.glow.copy(alpha = palette.imageOverlay * (0.85f + glow * 0.15f)),
                palette.pageMid.copy(alpha = palette.imageOverlay),
                palette.pageBottom.copy(alpha = 0.97f)
            ),
            useArtwork = true
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .safeDrawingPadding()
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onBack, modifier = Modifier.size(48.dp)) {
                            Icon(
                                painter = painterResource(R.drawable.ic_back),
                                contentDescription = stringResource(R.string.cd_back),
                                tint = palette.textSecondary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = stringResource(holdNameRes(state.kind)),
                            style = MaterialTheme.typography.labelLarge,
                            color = palette.textSecondary
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        IconButton(onClick = onTogglePause, modifier = Modifier.size(48.dp)) {
                            Image(
                                painter = painterResource(R.drawable.icon_pause),
                                contentDescription = stringResource(R.string.cd_pause),
                                modifier = Modifier.size(26.dp),
                                contentScale = ContentScale.Fit
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(horizontal = 20.dp, vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            if (state.phase == GamePhase.COUNTDOWN) {
                                Text(
                                    text = state.countdownValue.toString(),
                                    fontSize = 120.sp,
                                    lineHeight = 128.sp,
                                    fontWeight = FontWeight.Light,
                                    color = palette.accent,
                                    modifier = Modifier.graphicsLayer {
                                        scaleX = beat.value
                                        scaleY = beat.value
                                    }
                                )
                            } else {
                                StopwatchDisplay(
                                    elapsedMs = state.elapsedMs,
                                    dimmed = state.phase == GamePhase.PAUSED,
                                    pulse = pulse.value,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .semantics { liveRegion = LiveRegionMode.Polite }
                                )
                            }
                            Spacer(modifier = Modifier.height(26.dp))
                            HorizonLine(
                                steady = state.phase == GamePhase.RUNNING,
                                lineWidth = 240.dp,
                                amplitude = 4f,
                                lineColor = palette.primary.copy(alpha = 0.35f)
                            )
                            if (state.isNewBest) {
                                Spacer(modifier = Modifier.height(22.dp))
                                Text(
                                    text = stringResource(R.string.new_best),
                                    style = MaterialTheme.typography.labelLarge,
                                    color = palette.accent,
                                    modifier = Modifier.semantics {
                                        liveRegion = LiveRegionMode.Polite
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(132.dp))
                }

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(start = 24.dp, end = 24.dp, bottom = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (state.hasSides) {
                        Box(modifier = Modifier.width(220.dp)) {
                            ChipRow(
                                options = listOf(
                                    stringResource(R.string.side_left),
                                    stringResource(R.string.side_right)
                                ),
                                selectedIndex = if (state.side == HoldSide.RIGHT) 1 else 0,
                                onSelect = { index ->
                                    onSelectSide(
                                        if (index == 1) HoldSide.RIGHT else HoldSide.LEFT
                                    )
                                },
                                chipHeight = 44.dp
                            )
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                    }
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 64.dp),
                        shape = RoundedCornerShape(24.dp),
                        color = palette.surface.copy(alpha = 0.94f),
                        border = BorderStroke(1.dp, palette.border),
                        shadowElevation = 6.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 20.dp, end = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = stringResource(
                                    when (state.phase) {
                                        GamePhase.READY -> R.string.tap_to_start
                                        GamePhase.COUNTDOWN -> R.string.get_into_position
                                        GamePhase.RUNNING -> R.string.tap_to_stop
                                        GamePhase.PAUSED -> R.string.paused_hint
                                        GamePhase.FINISHED -> R.string.holding
                                    }
                                ),
                                style = MaterialTheme.typography.labelLarge,
                                color = palette.textSecondary,
                                maxLines = 1,
                                modifier = Modifier.weight(1f)
                            )
                            TextButton(
                                onClick = onFinish,
                                modifier = Modifier.heightIn(min = 48.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.finish),
                                    style = MaterialTheme.typography.labelLarge,
                                    color = palette.accent
                                )
                            }
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(palette.accent.copy(alpha = wash.value))
            )
        }
    }
}
