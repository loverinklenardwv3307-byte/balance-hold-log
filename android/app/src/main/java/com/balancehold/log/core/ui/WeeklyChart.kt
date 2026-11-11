package com.balancehold.log.core.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.dp
import com.balancehold.log.core.theme.LocalAppPalette

@Composable
fun WeeklyChart(
    bestValues: List<Long>,
    averageValues: List<Long>,
    modifier: Modifier = Modifier
) {
    val palette = LocalAppPalette.current
    val reveal = remember(bestValues) { Animatable(0f) }
    LaunchedEffect(bestValues) {
        reveal.animateTo(1f, tween(durationMillis = 450, easing = FastOutSlowInEasing))
    }
    val peak = bestValues.maxOrNull() ?: 0L
    val scaleMax = if (peak <= 0L) 1L else peak
    val peakIndex = bestValues.indexOfFirst { it == peak && it > 0L }

    Canvas(modifier = modifier) {
        val count = bestValues.size
        if (count == 0) return@Canvas
        val gap = 14.dp.toPx()
        val baseline = size.height - 18.dp.toPx()
        val available = size.width - gap * (count - 1)
        val column = (available / count).coerceAtMost(34.dp.toPx())
        val total = column * count + gap * (count - 1)
        val startX = (size.width - total) / 2f
        val usableHeight = baseline - 6.dp.toPx()
        val corner = CornerRadius(6.dp.toPx(), 6.dp.toPx())

        drawLine(
            color = palette.border,
            start = Offset(startX, baseline),
            end = Offset(startX + total, baseline),
            strokeWidth = 1.dp.toPx()
        )

        for (index in 0 until count) {
            val stagger = ((reveal.value - index * 0.06f) / 0.7f).coerceIn(0f, 1f)
            val left = startX + index * (column + gap)
            val averageHeight =
                usableHeight * (averageValues.getOrElse(index) { 0L }.toFloat() / scaleMax) * stagger
            val bestHeight = usableHeight * (bestValues[index].toFloat() / scaleMax) * stagger

            if (averageHeight > 1f) {
                drawRoundRect(
                    color = palette.border,
                    topLeft = Offset(left, baseline - averageHeight),
                    size = Size(column, averageHeight),
                    cornerRadius = corner
                )
            }
            if (bestHeight > 1f) {
                val innerWidth = column * 0.56f
                drawRoundRect(
                    color = palette.primary,
                    topLeft = Offset(left + (column - innerWidth) / 2f, baseline - bestHeight),
                    size = Size(innerWidth, bestHeight),
                    cornerRadius = corner
                )
                if (index == peakIndex) {
                    drawRoundRect(
                        color = palette.accent,
                        topLeft = Offset(left + (column - innerWidth) / 2f, baseline - bestHeight),
                        size = Size(innerWidth, 4.dp.toPx()),
                        cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
                    )
                }
            }
        }
    }
}
