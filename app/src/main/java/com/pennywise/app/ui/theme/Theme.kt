package com.pennywise.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val LightColors = lightColorScheme(
    primary = SakuraPink,
    onPrimary = Color.White,
    primaryContainer = SoftLavender,
    onPrimaryContainer = SakuraDayTextPrimary,
    secondary = ElectricViolet,
    onSecondary = Color.White,
    tertiary = NeonCyan,
    onTertiary = Color.White,
    background = SakuraDayBg,
    onBackground = SakuraDayTextPrimary,
    surface = SakuraDayCard,
    onSurface = SakuraDayTextPrimary,
    surfaceVariant = SakuraDayCardVariant,
    onSurfaceVariant = SakuraDayTextSecondary,
    outline = Color(0xFFE2CEF7),
    error = AnimeCoral,
    onError = Color.White,
)

private val DarkColors = darkColorScheme(
    primary = SakuraPink,
    onPrimary = AnimeMidnightBg,
    primaryContainer = Color(0xFF321D54),
    onPrimaryContainer = SoftLavender,
    secondary = SoftLavender,
    onSecondary = AnimeMidnightBg,
    tertiary = NeonCyan,
    onTertiary = AnimeMidnightBg,
    background = AnimeMidnightBg,
    onBackground = Color.White,
    surface = AnimeIndigoSurface,
    onSurface = Color.White,
    surfaceVariant = AnimeCardDark,
    onSurfaceVariant = SoftLavender.copy(alpha = 0.8f),
    outline = Color(0xFF4A3770),
    error = AnimeCoral,
    onError = Color.White,
)

private val AnimeTypography = androidx.compose.material3.Typography(
    displaySmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 32.sp,
        lineHeight = 38.sp,
        letterSpacing = (-0.5).sp,
    ),
    headlineSmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 30.sp,
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 26.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 22.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontSize = 16.sp,
        lineHeight = 24.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontSize = 14.sp,
        lineHeight = 20.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.2.sp,
    ),
)

@Composable
fun PennyWiseTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = AnimeTypography,
        shapes = androidx.compose.material3.Shapes(
            extraSmall = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
            small = androidx.compose.foundation.shape.RoundedCornerShape(14.dp),
            medium = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
            large = androidx.compose.foundation.shape.RoundedCornerShape(28.dp),
            extraLarge = androidx.compose.foundation.shape.RoundedCornerShape(36.dp),
        ),
        content = content,
    )
}
