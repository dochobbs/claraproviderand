package com.clara.provider.ui.theme

import androidx.compose.foundation.isSystemInDarkMode
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Define your colors
private val LightBlue = Color(0xFF1976D2)
private val DarkBlue = Color(0xFF1565C0)
private val AccentColor = Color(0xFFFF6F00)

private val LightColorScheme = lightColorScheme(
    primary = LightBlue,
    secondary = AccentColor,
    tertiary = Color(0xFF6200EE),
    background = Color(0xFFFAFAFA),
    surface = Color(0xFFFFFFFF),
    error = Color(0xFFB00020)
)

private val DarkColorScheme = darkColorScheme(
    primary = LightBlue,
    secondary = AccentColor,
    tertiary = Color(0xFF6200EE),
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    error = Color(0xFFCF6679)
)

@Composable
fun ClaraProviderAppTheme(
    darkTheme: Boolean = isSystemInDarkMode(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
