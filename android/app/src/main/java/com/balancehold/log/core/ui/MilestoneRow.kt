package com.balancehold.log.core.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import com.balancehold.log.R
import com.balancehold.log.core.theme.LocalAppPalette

@Composable
fun MilestoneRow(
    targetText: String,
    progress: Float,
    cleared: Boolean,
    isNext: Boolean,
    modifier: Modifier = Modifier
) {
    val palette = LocalAppPalette.current
    val clearedState = stringResource(R.string.state_cleared)
    val openState = stringResource(R.string.state_open)
    val animated by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 450, easing = FastOutSlowInEasing),
        label = "milestoneProgress"
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .semantics { stateDescription = if (cleared) clearedState else openState },
        shape = RoundedCornerShape(16.dp),
        color = palette.surface,
        border = BorderStroke(
            width = if (isNext) 2.dp else 1.dp,
            color = if (isNext) palette.accent else palette.border
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = targetText,
                style = MaterialTheme.typography.labelLarge,
                color = palette.textPrimary
            )
            Spacer(modifier = Modifier.width(14.dp))
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(palette.pageBottom)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animated)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(3.dp))
                        .background(if (cleared) palette.success else palette.accent)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            if (cleared) {
                Icon(
                    painter = painterResource(R.drawable.ic_check),
                    contentDescription = null,
                    tint = palette.success,
                    modifier = Modifier.size(20.dp)
                )
            } else {
                Spacer(modifier = Modifier.size(20.dp))
            }
        }
    }
}
