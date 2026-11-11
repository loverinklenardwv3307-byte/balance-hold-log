package com.balancehold.log.presentation.menu

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.balancehold.log.R
import com.balancehold.log.core.di.ServiceLocator
import com.balancehold.log.core.theme.LocalAppPalette
import com.balancehold.log.core.ui.AppBackground
import com.balancehold.log.core.ui.HoldCard
import com.balancehold.log.core.ui.PrimaryButton
import com.balancehold.log.core.ui.StatCard
import com.balancehold.log.core.ui.formatClock
import com.balancehold.log.core.ui.holdNameRes
import com.balancehold.log.core.ui.holdSpriteRes

@Composable
fun MenuScreen(
    onStartHold: (String) -> Unit,
    onOpenHold: (String) -> Unit,
    onOpenSettings: () -> Unit
) {
    val menuViewModel: MenuViewModel = viewModel(factory = ServiceLocator.factory())
    val state by menuViewModel.state.collectAsStateWithLifecycle()
    MenuContent(
        state = state,
        onSelect = menuViewModel::select,
        onStartHold = onStartHold,
        onOpenHold = onOpenHold,
        onOpenSettings = onOpenSettings
    )
}

@Composable
private fun MenuContent(
    state: MenuUiState,
    onSelect: (String) -> Unit,
    onStartHold: (String) -> Unit,
    onOpenHold: (String) -> Unit,
    onOpenSettings: () -> Unit
) {
    val palette = LocalAppPalette.current
    val heroState = remember { MutableTransitionState(false) }
    heroState.targetState = true
    AppBackground(
        gradient = listOf(palette.pageTop, palette.pageMid, palette.pageBottom)
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 90.dp, y = (-70).dp)
                .size(300.dp)
                .background(
                    Brush.radialGradient(
                        listOf(palette.accent.copy(alpha = 0.14f), Color.Transparent)
                    )
                )
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 24.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(R.drawable.icon_home),
                    contentDescription = null,
                    modifier = Modifier.size(28.dp),
                    contentScale = ContentScale.Fit
                )
                Spacer(modifier = Modifier.weight(1f))
                IconButton(
                    onClick = onOpenSettings,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_tune),
                        contentDescription = stringResource(R.string.cd_settings),
                        tint = palette.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            AnimatedVisibility(
                visibleState = heroState,
                enter = fadeIn(tween(420)) + slideInVertically(
                    animationSpec = tween(durationMillis = 420, easing = FastOutSlowInEasing),
                    initialOffsetY = { 24 }
                )
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.menu_title_balance),
                        style = MaterialTheme.typography.displayLarge,
                        color = palette.primary
                    )
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = stringResource(R.string.menu_title_hold),
                            style = MaterialTheme.typography.displayLarge,
                            color = palette.primary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = stringResource(R.string.menu_title_log),
                            style = MaterialTheme.typography.displayLarge,
                            color = palette.accent
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .width(48.dp)
                            .height(3.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(palette.accent)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = stringResource(R.string.menu_tagline),
                        style = MaterialTheme.typography.labelMedium,
                        color = palette.textSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            if (!state.loading && state.totalAttempts > 0 && state.bestTodayMs > 0L) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics { liveRegion = LiveRegionMode.Polite },
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        label = stringResource(R.string.stat_total_holds),
                        value = state.totalAttempts.toString(),
                        accent = palette.primary,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        label = stringResource(R.string.stat_best_today),
                        value = formatClock(state.bestTodayMs),
                        accent = palette.accent,
                        modifier = Modifier.weight(1f)
                    )
                }
            } else {
                Text(
                    text = stringResource(R.string.menu_empty_hint),
                    style = MaterialTheme.typography.labelSmall,
                    color = palette.textSecondary,
                    modifier = Modifier.padding(vertical = 10.dp)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(bottom = 8.dp)
            ) {
                items(items = state.holds, key = { it.id }) { row ->
                    HoldCard(
                        nameRes = holdNameRes(row.kind),
                        spriteRes = holdSpriteRes(row.kind),
                        detail = if (row.bestMs > 0L) {
                            stringResource(
                                R.string.hold_card_stats,
                                formatClock(row.bestMs),
                                formatClock(row.lastMs)
                            )
                        } else {
                            stringResource(R.string.hold_card_fresh)
                        },
                        selected = row.id == state.selectedHoldId,
                        leading = row.leadsToday,
                        onSelect = { onSelect(row.id) },
                        onOpen = { onOpenHold(row.id) }
                    )
                }
            }

            Text(
                text = stringResource(R.string.menu_hint),
                style = MaterialTheme.typography.labelSmall,
                color = palette.textSecondary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            PrimaryButton(
                text = stringResource(R.string.start_hold),
                onClick = { onStartHold(state.selectedHoldId) },
                enabled = state.selectedHoldId.isNotEmpty(),
                iconRes = R.drawable.ic_play,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
