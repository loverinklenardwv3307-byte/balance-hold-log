package com.balancehold.log.core.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class AppPalette(
    val name: String,
    val pageTop: Color,
    val pageMid: Color,
    val pageBottom: Color,
    val surface: Color,
    val border: Color,
    val primary: Color,
    val primaryDeep: Color,
    val accent: Color,
    val accentDark: Color,
    val accentTint: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textOnAccent: Color,
    val success: Color,
    val glow: Color,
    val imageOverlay: Float
)

val StonePalette = AppPalette(
    name = PresetName,
    pageTop = StoneLight,
    pageMid = StoneBase,
    pageBottom = StoneDeep,
    surface = StoneLight,
    border = StoneBorder,
    primary = SlatePrimary,
    primaryDeep = SlateDeep,
    accent = Tangerine,
    accentDark = TangerineDark,
    accentTint = TangerineTint,
    textPrimary = TextPrimary,
    textSecondary = TextSecondary,
    textOnAccent = TextOnAccent,
    success = SageSuccess,
    glow = StoneGlow,
    imageOverlay = 0.94f
)

val NightPalette = AppPalette(
    name = PresetName,
    pageTop = NightPageTop,
    pageMid = NightPageMid,
    pageBottom = NightPageBottom,
    surface = NightSurface,
    border = NightBorder,
    primary = NightTextPrimary,
    primaryDeep = SlateDeep,
    accent = Tangerine,
    accentDark = TangerineDark,
    accentTint = Color(0xFF4A3524),
    textPrimary = NightTextPrimary,
    textSecondary = NightTextSecondary,
    textOnAccent = TextOnAccent,
    success = SageSuccess,
    glow = Color(0xFF3A5160),
    imageOverlay = 0.9f
)

val LocalAppPalette = staticCompositionLocalOf { StonePalette }

private val StoneScheme = lightColorScheme(
    primary = SlatePrimary,
    onPrimary = TextOnAccent,
    primaryContainer = StoneDeep,
    onPrimaryContainer = TextPrimary,
    secondary = StoneBorder,
    onSecondary = TextPrimary,
    tertiary = Tangerine,
    onTertiary = TextOnAccent,
    background = StoneBase,
    onBackground = TextPrimary,
    surface = StoneLight,
    onSurface = TextPrimary,
    surfaceVariant = StoneDeep,
    onSurfaceVariant = TextSecondary,
    outline = StoneBorder,
    outlineVariant = StoneDeep
)

private val NightScheme = darkColorScheme(
    primary = Tangerine,
    onPrimary = TextOnAccent,
    primaryContainer = NightSurface,
    onPrimaryContainer = NightTextPrimary,
    secondary = NightBorder,
    onSecondary = NightTextPrimary,
    tertiary = Tangerine,
    onTertiary = TextOnAccent,
    background = NightPageMid,
    onBackground = NightTextPrimary,
    surface = NightSurface,
    onSurface = NightTextPrimary,
    surfaceVariant = NightPageBottom,
    onSurfaceVariant = NightTextSecondary,
    outline = NightBorder,
    outlineVariant = NightPageBottom
)

@Composable
fun AppTheme(darkTheme: Boolean = false, content: @Composable () -> Unit) {
    val palette = if (darkTheme) NightPalette else StonePalette
    CompositionLocalProvider(LocalAppPalette provides palette) {
        MaterialTheme(
            colorScheme = if (darkTheme) NightScheme else StoneScheme,
            typography = AppTypography,
            content = content
        )
    }
}
