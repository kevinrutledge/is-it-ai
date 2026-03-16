package com.example.isitai.ui.theme

import androidx.compose.ui.graphics.Color

// Light theme colors — "Paper & Tea"
val PrimarySurface = Color(0xFFFCFAF2)    // Shironeri (bleached silk)
val PrimaryText = Color(0xFF27221F)        // Sumi (ink black)
val SecondaryText = Color(0xFF6B5D52)      // Warm brown (tea-stained)
val PlaceholderLight = Color(0xFFD7C4BB)   // Haizakura (ashen cherry blossom)
val PlaceholderDark = Color(0xFFCBB5A8)    // Slightly deeper Haizakura
val ButtonFill = Color(0xFF27221F)         // Sumi (ink black)
val ButtonText = Color(0xFFFCFAF2)         // Shironeri (bleached silk)
val BadgeBackground = Color(0xFFEBE6D6)    // Japandi cream
val BadgeBorder = Color(0xFFB9A193)        // Shironezumi (warm gray)

// Dark theme colors — "Ink & Lacquer"
val DarkPrimarySurface = Color(0xFF1A1A1B) // Lacquer dark
val DarkPrimaryText = Color(0xFFE8DDD2)    // Desaturated warm white
val DarkSecondaryText = Color(0xFFBEB0A2)  // Lighter warm brown
val DarkPlaceholderLight = Color(0xFF4A4038) // Warm paper on lacquer
val DarkPlaceholderDark = Color(0xFF5A5048)  // Slightly lighter warm paper
val DarkButtonFill = Color(0xFFEDD5BF)     // Lighter warm parchment
val DarkButtonText = Color(0xFF27221F)     // Sumi (ink black)
val DarkBadgeBackground = Color(0xFF2C2420) // Dark warm
val DarkBadgeBorder = Color(0xFF554236)    // Kurotobi (dark brown)

// Accent colors
val StreakFlame = Color(0xFF985629)         // Kitsune (fox gold)
val DarkStreakFlame = Color(0xFF985629)     // Kitsune (fox gold, same both modes)

// Annotation colors — Japanese traditional, high contrast for image overlays
object AnnotationColors {
    val Anatomical = Color(0xFF005CAF) // Ruri (lapis lazuli)
    val Texture = Color(0xFFE94709)    // Shu-iro (vermillion)
    val Background = Color(0xFF8B81C3) // Fuji (wisteria)
    val Facial = Color(0xFFEFBB24)     // Ukon (turmeric)
    val Lighting = Color(0xFF24936E)   // Rokusho (verdigris)

    fun forType(type: String): Color = when (type) {
        "anatomical" -> Anatomical
        "texture" -> Texture
        "background" -> Background
        "facial" -> Facial
        "lighting" -> Lighting
        else -> Color.Gray
    }
}
