package com.pennywise.app.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Anime Sakura Nights Color Palette
val AnimeMidnightBg = Color(0xFF0F0C1B)
val AnimeIndigoSurface = Color(0xFF1B162E)
val AnimeCardDark = Color(0xFF241C3F)
val AnimeCardDarkTranslucent = Color(0xD922193E)

val SakuraPink = Color(0xFFFF758F)
val SakuraPinkLight = Color(0xFFFFB5A7)
val SakuraPinkGlow = Color(0xFFFF8FA3)
val ElectricViolet = Color(0xFF7B2CBF)
val NeonViolet = Color(0xFF9D4EDD)
val SoftLavender = Color(0xFFE0AAFF)
val PaleLavender = Color(0xFFF3E8FF)
val NeonCyan = Color(0xFF4CC9F0)

val AnimeEmerald = Color(0xFF38B000)
val AnimeMint = Color(0xFF70E000)
val AnimeCoral = Color(0xFFFF4D6D)
val AnimeAmber = Color(0xFFFFB703)

// Light Theme - Sakura Daytime
val SakuraDayBg = Color(0xFFFFF0F5)
val SakuraDayCard = Color(0xFFFFFFFF)
val SakuraDayCardVariant = Color(0xFFF7EEFF)
val SakuraDayTextPrimary = Color(0xFF2E1065)
val SakuraDayTextSecondary = Color(0xFF6B5B95)

// Gradients
val SakuraHeroGradient = Brush.linearGradient(
    colors = listOf(
        Color(0xFF2B1055),
        Color(0xFF7B2CBF),
        Color(0xFFFF758F)
    )
)

val NeonCardGradient = Brush.linearGradient(
    colors = listOf(
        Color(0xFF2D1B4E),
        Color(0xFF1B162E)
    )
)

val SakuraProgressGradient = Brush.horizontalGradient(
    colors = listOf(
        Color(0xFF7B2CBF),
        Color(0xFFFF758F),
        Color(0xFFFFB5A7)
    )
)

val CyanNeonGradient = Brush.horizontalGradient(
    colors = listOf(
        Color(0xFF4CC9F0),
        Color(0xFF7B2CBF)
    )
)
