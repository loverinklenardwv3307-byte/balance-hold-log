package com.balancehold.log.core.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.balancehold.log.R
import com.balancehold.log.core.theme.LocalAppPalette

@Composable
fun ChipRow(
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    chipHeight: Dp = 48.dp
) {
    val palette = LocalAppPalette.current
    val selectedState = stringResource(R.string.state_selected)
    val idleState = stringResource(R.string.state_not_selected)
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        options.forEachIndexed { index, option ->
            val active = index == selectedIndex
            Surface(
                onClick = { onSelect(index) },
                modifier = Modifier
                    .weight(1f)
                    .semantics { stateDescription = if (active) selectedState else idleState },
                shape = RoundedCornerShape(22.dp),
                color = if (active) palette.accentTint else palette.pageBottom,
                border = BorderStroke(
                    width = if (active) 2.dp else 1.dp,
                    color = if (active) palette.accent else palette.border
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(chipHeight),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = option,
                        style = MaterialTheme.typography.labelLarge,
                        color = if (active) palette.accent else palette.textSecondary,
                        maxLines = 1
                    )
                }
            }
        }
    }
}
