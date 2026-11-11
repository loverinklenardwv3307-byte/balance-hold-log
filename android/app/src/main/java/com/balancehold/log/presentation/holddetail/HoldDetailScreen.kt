package com.balancehold.log.presentation.holddetail

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.balancehold.log.R
import com.balancehold.log.core.di.ScreenArgs
import com.balancehold.log.core.di.ServiceLocator
import com.balancehold.log.core.theme.LocalAppPalette
import com.balancehold.log.core.ui.AppBackground
import com.balancehold.log.core.ui.MilestoneRow
import com.balancehold.log.core.ui.PrimaryButton
import com.balancehold.log.core.ui.SecondaryButton
import com.balancehold.log.core.ui.WeeklyChart
import com.balancehold.log.core.ui.formatClock
import com.balancehold.log.core.ui.holdNameRes
import com.balancehold.log.domain.model.Difficulty

@Composable
fun HoldDetailScreen(
    holdId: String,
    onBack: () -> Unit,
    onStartHold: (String) -> Unit
) {
    val holdDetailViewModel: HoldDetailViewModel = viewModel(
        key = "detail-$holdId",
        factory = ServiceLocator.factory(ScreenArgs(holdId = holdId))
    )
    val state by holdDetailViewModel.state.collectAsStateWithLifecycle()

    HoldDetailContent(
        state = state,
        onBack = onBack,
        onRaise = holdDetailViewModel::raiseTarget,
        onLower = holdDetailViewModel::lowerTarget,
        onStart = { onStartHold(holdId) }
    )
}

@Composable
private fun HoldDetailContent(
    state: HoldDetailUiState,
    onBack: () -> Unit,
    onRaise: () -> Unit,
    onLower: () -> Unit,
    onStart: () -> Unit
) {
    val palette = LocalAppPalette.current
    val milestoneCleared = state.milestonesMs.any { it <= state.bestMs }

    AppBackground(gradient = listOf(palette.pageTop, palette.pageMid, palette.pageBottom)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 20.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack, modifier = Modifier.size(48.dp)) {
                    Icon(
                        painter = painterResource(R.drawable.ic_back),
                        contentDescription = stringResource(R.string.cd_back),
                        tint = palette.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = stringResource(holdNameRes(state.kind)),
                    style = MaterialTheme.typography.titleLarge,
                    color = palette.primary
                )
                Spacer(modifier = Modifier.weight(1f))
                if (milestoneCleared) {
                    Image(
                        painter = painterResource(R.drawable.sprite_5),
                        contentDescription = stringResource(R.string.cd_milestone_badge),
                        modifier = Modifier.size(30.dp),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    Spacer(modifier = Modifier.size(30.dp))
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(168.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = palette.surface),
                border = BorderStroke(1.dp, palette.border),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(R.string.weekly_title),
                        style = MaterialTheme.typography.labelSmall,
                        color = palette.textSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    WeeklyChart(
                        bestValues = state.weekBest,
                        averageValues = state.weekAverage,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                state.milestonesMs.forEach { target ->
                    MilestoneRow(
                        targetText = stringResource(R.string.milestone_target, formatClock(target)),
                        progress = if (target <= 0L) 0f else state.bestMs.toFloat() / target,
                        cleared = state.bestMs >= target,
                        isNext = target == state.nextMilestoneMs
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SecondaryButton(
                    text = stringResource(R.string.lower_target),
                    onClick = onLower,
                    modifier = Modifier.weight(1f)
                )
                SecondaryButton(
                    text = stringResource(R.string.raise_target),
                    onClick = onRaise,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (state.attempts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.no_attempts),
                        style = MaterialTheme.typography.labelLarge,
                        color = palette.textSecondary
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(items = state.attempts, key = { it.id }) { row ->
                        AttemptLine(row = row)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            PrimaryButton(
                text = stringResource(R.string.start_hold),
                onClick = onStart,
                iconRes = R.drawable.ic_play,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(18.dp))
        }
    }
}

@Composable
private fun AttemptLine(row: AttemptRow) {
    val palette = LocalAppPalette.current
    val dotColor = when (row.difficulty) {
        Difficulty.EASY -> palette.success
        Difficulty.STEADY -> palette.accent
        Difficulty.HARD -> palette.primary
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(palette.surface)
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = formatClock(row.durationMs),
            style = MaterialTheme.typography.titleMedium,
            color = palette.textPrimary
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = if (row.daysAgo == 0) {
                stringResource(R.string.today)
            } else {
                stringResource(R.string.days_ago, row.daysAgo)
            },
            style = MaterialTheme.typography.labelSmall,
            color = palette.textSecondary
        )
        if (row.sideLabel != null) {
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = stringResource(row.sideLabel),
                style = MaterialTheme.typography.labelSmall,
                color = palette.textSecondary
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = stringResource(
                when (row.difficulty) {
                    Difficulty.EASY -> R.string.diff_easy
                    Difficulty.STEADY -> R.string.diff_steady
                    Difficulty.HARD -> R.string.diff_hard
                }
            ),
            style = MaterialTheme.typography.labelSmall,
            color = palette.textSecondary
        )
        Spacer(modifier = Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(dotColor)
        )
    }
}
