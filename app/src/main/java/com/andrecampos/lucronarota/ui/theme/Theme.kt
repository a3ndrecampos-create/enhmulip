package com.andrecampos.lucronarota.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColors = darkColorScheme(
    primary = Emerald,
    onPrimary = Color.White,
    secondary = AzulParticular,
    background = BackgroundDark,
    surface = SurfaceDark,
    surfaceVariant = SurfaceDarkAlt,
    onBackground = TextPrimaryDark,
    onSurface = TextPrimaryDark,
    onSurfaceVariant = TextSecondaryDark,
    error = LucroNegativo
)

private val LightColors = lightColorScheme(
    primary = EmeraldDark,
    onPrimary = Color.White,
    secondary = AzulParticular,
    background = BackgroundLight,
    surface = SurfaceLight,
    surfaceVariant = SurfaceLightAlt,
    onBackground = TextPrimaryLight,
    onSurface = TextPrimaryLight,
    onSurfaceVariant = TextSecondaryLight,
    error = LucroNegativo
)

@Composable
fun LucroNaRotaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
