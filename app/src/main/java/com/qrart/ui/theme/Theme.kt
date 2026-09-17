package com.qrart.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF9DB7FF),
    secondary = Color(0xFF8FD8CE),
    tertiary = Color(0xFFC9B6FF),
    surface = SurfaceDark,
    onSurface = CardLight
)

private val LightColorScheme = lightColorScheme(
    primary = DUABlue,
    secondary = DUATeal,
    tertiary = DUAAccent,
    surface = DUACanvas,
    onSurface = DUAInk,
    background = DUACanvas,
    onBackground = DUAInk,
    surfaceVariant = DUASurface
)

@Composable
fun QRArtTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.surface.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
