package com.balancehold.log.presentation.settings

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.balancehold.log.R
import com.balancehold.log.core.di.ServiceLocator
import com.balancehold.log.core.theme.LocalAppPalette
import com.balancehold.log.core.ui.AppBackground
import com.balancehold.log.core.ui.ChipRow
import com.balancehold.log.core.ui.SecondaryButton

@Composable
fun SettingsScreen(onBack: () -> Unit) {
    val settingsViewModel: SettingsViewModel = viewModel(factory = ServiceLocator.factory())
    val state by settingsViewModel.state.collectAsStateWithLifecycle()

    SettingsContent(
        state = state,
        onBack = onBack,
        onCountdown = settingsViewModel::setCountdown,
        onSounds = settingsViewModel::setSounds,
        onTheme = settingsViewModel::setDarkTheme,
        onExport = settingsViewModel::exportLog
    )
}

@Composable
private fun SettingsContent(
    state: SettingsUiState,
    onBack: () -> Unit,
    onCountdown: (Int) -> Unit,
    onSounds: (Boolean) -> Unit,
    onTheme: (Boolean) -> Unit,
    onExport: () -> Unit
) {
    val palette = LocalAppPalette.current
    val countdownOptions = listOf(0, 3, 5)
    val onLabel = stringResource(R.string.state_on)
    val offLabel = stringResource(R.string.state_off)

    AppBackground(gradient = listOf(palette.pageTop, palette.pageMid, palette.pageBottom)) {
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
                IconButton(onClick = onBack, modifier = Modifier.size(48.dp)) {
                    Icon(
                        painter = painterResource(R.drawable.ic_back),
                        contentDescription = stringResource(R.string.cd_back),
                        tint = palette.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.settings_title),
                    style = MaterialTheme.typography.titleLarge,
                    color = palette.primary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            SettingRow(label = stringResource(R.string.countdown)) {
                Box(modifier = Modifier.width(190.dp)) {
                    ChipRow(
                        options = listOf(
                            stringResource(R.string.countdown_off),
                            stringResource(R.string.countdown_three),
                            stringResource(R.string.countdown_five)
                        ),
                        selectedIndex = countdownOptions.indexOf(state.countdownSeconds)
                            .coerceAtLeast(0),
                        onSelect = { index -> onCountdown(countdownOptions[index]) },
                        chipHeight = 44.dp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            SettingRow(label = stringResource(R.string.sounds)) {
                Switch(
                    checked = state.soundsEnabled,
                    onCheckedChange = onSounds,
                    modifier = Modifier.semantics {
                        stateDescription = if (state.soundsEnabled) onLabel else offLabel
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = palette.textOnAccent,
                        checkedTrackColor = palette.accent,
                        checkedBorderColor = palette.accent,
                        uncheckedThumbColor = palette.textSecondary,
                        uncheckedTrackColor = palette.pageBottom,
                        uncheckedBorderColor = palette.border
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            SettingRow(label = stringResource(R.string.theme)) {
                Box(modifier = Modifier.width(190.dp)) {
                    ChipRow(
                        options = listOf(
                            stringResource(R.string.theme_stone),
                            stringResource(R.string.theme_slate)
                        ),
                        selectedIndex = if (state.darkTheme) 1 else 0,
                        onSelect = { index -> onTheme(index == 1) },
                        chipHeight = 44.dp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            SettingRow(label = stringResource(R.string.local_backup)) {
                SecondaryButton(
                    text = stringResource(R.string.export_log),
                    onClick = onExport
                )
            }

            if (state.exportedCount >= 0) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(R.string.export_done, state.exportedCount),
                    style = MaterialTheme.typography.labelSmall,
                    color = palette.accent,
                    modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite }
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = stringResource(R.string.offline_note),
                style = MaterialTheme.typography.labelSmall,
                color = palette.textSecondary,
                modifier = Modifier.padding(bottom = 24.dp)
            )
        }
    }
}

@Composable
private fun SettingRow(label: String, control: @Composable () -> Unit) {
    val palette = LocalAppPalette.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(76.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = palette.surface),
        border = BorderStroke(1.dp, palette.border),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = palette.textPrimary
            )
            control()
        }
    }
}
