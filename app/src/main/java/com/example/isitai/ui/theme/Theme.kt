package com.example.isitai.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = ButtonFill,
    onPrimary = ButtonText,
    background = PrimarySurface,
    surface = PrimarySurface,
    onBackground = PrimaryText,
    onSurface = PrimaryText,
    onSurfaceVariant = SecondaryText,
    surfaceVariant = PlaceholderLight,
    secondaryContainer = BadgeBackground,
    outline = BadgeBorder,
    error = Color(0xFF7E2639),
    surfaceContainer = Color(0xFFE0D0BA)
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkButtonFill,
    onPrimary = DarkButtonText,
    background = DarkPrimarySurface,
    surface = DarkPrimarySurface,
    onBackground = DarkPrimaryText,
    onSurface = DarkPrimaryText,
    onSurfaceVariant = DarkSecondaryText,
    surfaceVariant = DarkPlaceholderLight,
    secondaryContainer = DarkBadgeBackground,
    outline = DarkBadgeBorder,
    error = Color(0xFF994639),
    surfaceContainer = Color(0xFF303030)
)

@Composable
fun IsItAITheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
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
