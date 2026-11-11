package com.balancehold.log.presentation.gameover

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
import com.balancehold.log.core.ui.GlassCard
import com.balancehold.log.core.ui.PrimaryButton
import com.balancehold.log.core.ui.SecondaryButton
import com.balancehold.log.core.ui.StatCard
import com.balancehold.log.core.ui.formatClock
import com.balancehold.log.core.ui.formatFull
import com.balancehold.log.core.ui.holdNameRes
import com.balancehold.log.core.ui.holdSpriteRes
import com.balancehold.log.domain.model.Difficulty
import com.balancehold.log.domain.model.HoldSide

@Composable
fun GameOverScreen(
    holdId: String,
    durationMs: Long,
    isNewBest: Boolean,
    timestamp: Long,
    onMenu: () -> Unit,
    onRepeat: (String) -> Unit
) {
    val gameOverViewModel: GameOverViewModel = viewModel(
        key = "result-$timestamp",
        factory = ServiceLocator.factory(
            ScreenArgs(
                holdId = holdId,
                durationMs = durationMs,
                isNewBest = isNewBest,
                timestamp = timestamp
            )
        )
    )
    val state by gameOverViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        gameOverViewModel.events.collect { event ->
            when (event) {
                GameOverEvent.ToMenu -> onMenu()
                is GameOverEvent.Repeat -> onRepeat(event.holdId)
            }
        }
    }

    GameOverContent(
        state = state,
        onSelectSide = gameOverViewModel::selectSide,
        onSelectDifficulty = gameOverViewModel::selectDifficulty,
        onNoteChange = gameOverViewModel::updateNote,
        onKeep = gameOverViewModel::keepAndLeave,
        onRepeatHold = gameOverViewModel::keepAndRepeat,
        onDiscard = gameOverViewModel::discard
    )
}

@Composable
private fun GameOverContent(
    state: GameOverUiState,
    onSelectSide: (HoldSide) -> Unit,
    onSelectDifficulty: (Difficulty) -> Unit,
    onNoteChange: (String) -> Unit,
    onKeep: () -> Unit,
    onRepeatHold: () -> Unit,
    onDiscard: () -> Unit
) {
    val palette = LocalAppPalette.current
    val counter = remember { Animatable(0f) }
    val cardScale = remember { Animatable(0.92f) }

    LaunchedEffect(state.durationMs) {
        counter.animateTo(1f, tween(durationMillis = 450, easing = FastOutSlowInEasing))
    }
    LaunchedEffect(Unit) {
        cardScale.animateTo(1f, tween(durationMillis = 260, easing = FastOutSlowInEasing))
    }

    val shownMs = (state.durationMs * counter.value).toLong()

    AppBackground(
        gradient = listOf(palette.glow, palette.pageMid, palette.pageBottom)
    ) {
        if (state.isNewBest) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = 40.dp)
                    .size(280.dp)
                    .background(
                        Brush.radialGradient(
                            listOf(palette.accent.copy(alpha = 0.16f), Color.Transparent)
                        )
                    )
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(
                        if (state.isNewBest) R.string.new_best else R.string.hold_complete
                    ),
                    style = MaterialTheme.typography.headlineMedium,
                    color = if (state.isNewBest) palette.accent else palette.primary
                )
                if (state.isNewBest) {
                    Spacer(modifier = Modifier.width(10.dp))
                    Image(
                        painter = painterResource(R.drawable.sprite_5),
                        contentDescription = stringResource(R.string.cd_milestone_badge),
                        modifier = Modifier.size(38.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        scaleX = cardScale.value
                        scaleY = cardScale.value
                    }
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = formatFull(shownMs),
                            fontSize = 52.sp,
                            lineHeight = 58.sp,
                            fontWeight = FontWeight.Light,
                            color = palette.primary,
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = stringResource(holdNameRes(state.kind)),
                            style = MaterialTheme.typography.labelLarge,
                            color = palette.textSecondary
                        )
                    }
                    Image(
                        painter = painterResource(holdSpriteRes(state.kind)),
                        contentDescription = stringResource(holdNameRes(state.kind)),
                        modifier = Modifier
                            .padding(start = 12.dp)
                            .size(64.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            if (state.hasSides) {
                ChipRow(
                    options = listOf(
                        stringResource(R.string.side_left),
                        stringResource(R.string.side_right)
                    ),
                    selectedIndex = if (state.side == HoldSide.RIGHT) 1 else 0,
                    onSelect = { index ->
                        onSelectSide(if (index == 1) HoldSide.RIGHT else HoldSide.LEFT)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    chipHeight = 44.dp
                )
            }

            ChipRow(
                options = listOf(
                    stringResource(R.string.diff_easy),
                    stringResource(R.string.diff_steady),
                    stringResource(R.string.diff_hard)
                ),
                selectedIndex = Difficulty.entries.indexOf(state.difficulty),
                onSelect = { index -> onSelectDifficulty(Difficulty.entries[index]) },
                modifier = Modifier.fillMaxWidth(),
                chipHeight = 52.dp
            )

            OutlinedTextField(
                value = state.note,
                onValueChange = onNoteChange,
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                label = {
                    Text(
                        text = stringResource(R.string.note_label),
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = palette.accent,
                    unfocusedBorderColor = palette.border,
                    focusedContainerColor = palette.surface,
                    unfocusedContainerColor = palette.surface,
                    focusedTextColor = palette.textPrimary,
                    unfocusedTextColor = palette.textPrimary,
                    cursorColor = palette.accent,
                    focusedLabelColor = palette.accent,
                    unfocusedLabelColor = palette.textSecondary
                )
            )

            if (state.bestMs > 0L && state.attemptCount > 0) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        label = stringResource(R.string.stat_best),
                        value = formatClock(state.bestMs),
                        accent = palette.primary,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        label = stringResource(R.string.stat_attempts),
                        value = state.attemptCount.toString(),
                        accent = palette.accent,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Text(
                text = stringResource(R.string.saved_locally),
                style = MaterialTheme.typography.labelSmall,
                color = palette.textSecondary
            )

            PrimaryButton(
                text = stringResource(R.string.save_attempt),
                onClick = onKeep,
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SecondaryButton(
                    text = stringResource(R.string.hold_again),
                    onClick = onRepeatHold,
                    modifier = Modifier.weight(1f)
                )
                SecondaryButton(
                    text = stringResource(R.string.menu),
                    onClick = onDiscard,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
