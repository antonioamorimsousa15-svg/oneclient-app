package com.shoyoomc.aflecklauncher.ui.theme

import com.shoyoomc.aflecklauncher.ui.screens.main.AfCutShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import com.shoyoomc.aflecklauncher.ui.screens.main.AfThemeState

/**
 * Esquema de cores do Afleck: todas as telas (Versões, Download, Contas, Ajustes...)
 * passam a seguir o mesmo tema escolhido na aba Aparência.
 */
@Composable
fun afColorScheme(): ColorScheme {
    val accent = AfThemeState.accent
    val accent2 = AfThemeState.accent2
    val border = AfThemeState.border
    val base = AfThemeState.base
    val glass = AfThemeState.panelOpacity / 100f
    val navy = lerp(base, Color.Black, 0.55f).copy(alpha = 1f)
    val bg = lerp(base, Color.Black, 0.88f).copy(alpha = 1f)
    return darkColorScheme(
        primary = accent,
        onPrimary = navy,
        primaryContainer = lerp(base, accent, 0.28f).copy(alpha = 1f),
        onPrimaryContainer = Color.White,
        inversePrimary = accent2,
        secondary = accent2,
        onSecondary = navy,
        secondaryContainer = lerp(base, accent2, 0.22f).copy(alpha = 1f),
        onSecondaryContainer = Color.White,
        tertiary = border,
        onTertiary = navy,
        tertiaryContainer = lerp(base, border, 0.25f).copy(alpha = 1f),
        onTertiaryContainer = Color.White,
        background = bg,
        onBackground = Color.White,
        surface = bg,
        onSurface = Color.White,
        surfaceVariant = lerp(base, Color.Black, 0.5f).copy(alpha = 1f),
        onSurfaceVariant = AfThemeState.current.dim,
        surfaceTint = accent,
        inverseSurface = Color.White,
        inverseOnSurface = navy,
        outline = border,
        outlineVariant = lerp(base, border, 0.4f).copy(alpha = 1f),
        surfaceDim = lerp(base, Color.Black, 0.6f).copy(alpha = 1f),
        surfaceBright = lerp(base, Color.White, 0.15f).copy(alpha = 1f),
        surfaceContainerLowest = lerp(base, Color.Black, 0.5f).copy(alpha = glass),
        surfaceContainerLow = lerp(base, Color.Black, 0.25f).copy(alpha = glass),
        surfaceContainer = base.copy(alpha = glass),
        surfaceContainerHigh = lerp(base, Color.Black, 0.6f).copy(alpha = 1f),
        surfaceContainerHighest = lerp(base, Color.Black, 0.45f).copy(alpha = 1f),
    )
}

/** Formato retangular com bordas bem arredondadas, em todo o app. */
private fun afCorner(r: androidx.compose.ui.unit.Dp) = androidx.compose.foundation.shape.RoundedCornerShape(r)

fun afShapes() = Shapes(
    extraSmall = afCorner(2.dp),
    small = afCorner(3.dp),
    medium = afCorner(3.dp),
    large = afCorner(4.dp),
    extraLarge = afCorner(4.dp)
)
