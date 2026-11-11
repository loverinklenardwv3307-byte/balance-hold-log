package com.balancehold.log.core.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.balancehold.log.core.theme.LocalAppPalette

@Composable
fun HorizonLine(
    steady: Boolean,
    lineWidth: Dp,
    amplitude: Float,
    lineColor: Color,
    modifier: Modifier = Modifier
) {
    val palette = LocalAppPalette.current
    val transition = rememberInfiniteTransition(label = "horizon")
    val tilt by transition.animateFloat(
        initialValue = -amplitude,
        targetValue = amplitude,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "horizonTilt"
    )
    val breath by transition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "horizonBreath"
    )
    val settle by animateFloatAsState(
        targetValue = if (steady) 0f else 1f,
        animationSpec = tween(durationMillis = 900),
        label = "horizonSettle"
    )

    Box(
        modifier = modifier
            .width(lineWidth)
            .height(18.dp)
            .graphicsLayer { rotationZ = tilt * settle },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(lineColor)
        )
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(palette.accent)
                .graphicsLayer { alpha = breath }
        )
    }
}
