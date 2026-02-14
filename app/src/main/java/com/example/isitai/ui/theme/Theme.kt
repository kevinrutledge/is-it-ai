package com.example.isitai.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

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
    outline = BadgeBorder
)

@Composable
fun IsItAITheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
