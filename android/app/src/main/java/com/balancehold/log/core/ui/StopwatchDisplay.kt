package com.balancehold.log.core.ui

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.balancehold.log.core.theme.LocalAppPalette
import kotlin.math.min

@Composable
fun StopwatchDisplay(
    elapsedMs: Long,
    dimmed: Boolean,
    pulse: Float,
    modifier: Modifier = Modifier
) {
    val palette = LocalAppPalette.current
    BoxWithConstraints(modifier = modifier, contentAlignment = Alignment.Center) {
        val numeral = min(120f, maxWidth.value / 4.6f)
        val alpha = if (dimmed) 0.55f else 1f
        Row(
            modifier = Modifier.graphicsLayer {
                scaleX = pulse
                scaleY = pulse
            },
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = formatClock(elapsedMs),
                fontSize = numeral.sp,
                lineHeight = (numeral * 1.06f).sp,
                fontWeight = FontWeight.Light,
                letterSpacing = (-2).sp,
                textAlign = TextAlign.Center,
                maxLines = 1,
                color = palette.primary.copy(alpha = alpha)
            )
            Text(
                text = formatTenths(elapsedMs),
                fontSize = (numeral / 3f).sp,
                lineHeight = (numeral / 2.4f).sp,
                fontWeight = FontWeight.Light,
                maxLines = 1,
                color = palette.accent.copy(alpha = alpha)
            )
        }
    }
}
