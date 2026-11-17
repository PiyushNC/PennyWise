package com.pennywise.app.ui.anime

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pennywise.app.ui.theme.ElectricViolet
import com.pennywise.app.ui.theme.NeonCyan
import com.pennywise.app.ui.theme.SakuraPink
import com.pennywise.app.ui.theme.SoftLavender

@Composable
fun AnimeCategoryBadge(
    category: String,
    modifier: Modifier = Modifier,
    size: Dp = 44.dp,
) {
    val categoryClean = category.lowercase()
    val (badgeBgColors, iconColor) = when {
        categoryClean.contains("food") || categoryClean.contains("dining") ->
            listOf(Color(0xFFFF758F), Color(0xFFFFB5A7)) to Color.White
        categoryClean.contains("shop") || categoryClean.contains("fashion") ->
            listOf(Color(0xFF9D4EDD), Color(0xFFE0AAFF)) to Color.White
        categoryClean.contains("transport") || categoryClean.contains("travel") ->
            listOf(Color(0xFF4CC9F0), Color(0xFF4895EF)) to Color.White
        categoryClean.contains("bill") || categoryClean.contains("utility") ->
            listOf(Color(0xFFFFB703), Color(0xFFFB8500)) to Color.White
        categoryClean.contains("entertain") || categoryClean.contains("game") ->
            listOf(Color(0xFF7B2CBF), Color(0xFF5A189A)) to Color.White
        categoryClean.contains("health") || categoryClean.contains("medical") ->
            listOf(Color(0xFFFF4D6D), Color(0xFFC77DFF)) to Color.White
        categoryClean.contains("salary") || categoryClean.contains("income") ->
            listOf(Color(0xFF38B000), Color(0xFF70E000)) to Color.White
        categoryClean.contains("invest") ->
            listOf(Color(0xFF00B4D8), Color(0xFF90E0EF)) to Color.White
        else ->
            listOf(SakuraPink, ElectricViolet) to Color.White
    }

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(Brush.linearGradient(badgeBgColors))
            .border(1.dp, Color.White.copy(alpha = 0.6f), CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.size(size * 0.65f)) {
            val w = this.size.width
            val h = this.size.height

            when {
                categoryClean.contains("food") || categoryClean.contains("dining") -> {
                    // Anime Ramen Bowl
                    val bowl = Path().apply {
                        moveTo(w * 0.15f, h * 0.45f)
                        lineTo(w * 0.85f, h * 0.45f)
                        cubicTo(w * 0.85f, h * 0.85f, w * 0.15f, h * 0.85f, w * 0.15f, h * 0.45f)
                    }
                    drawPath(bowl, color = Color.White)
                    // Chopsticks
                    drawLine(color = Color(0xFFFFF0F5), start = Offset(w * 0.2f, h * 0.15f), end = Offset(w * 0.75f, h * 0.4f), strokeWidth = 3f)
                    drawLine(color = Color(0xFFFFF0F5), start = Offset(w * 0.25f, h * 0.1f), end = Offset(w * 0.8f, h * 0.35f), strokeWidth = 3f)
                }
                categoryClean.contains("shop") || categoryClean.contains("fashion") -> {
                    // Shopping Bag with Anime Ribbon
                    drawRoundRect(
                        color = Color.White,
                        topLeft = Offset(w * 0.2f, h * 0.35f),
                        size = Size(w * 0.6f, h * 0.55f),
                        cornerRadius = CornerRadius(6f, 6f),
                    )
                    drawCircle(color = Color.Transparent, radius = w * 0.15f, center = Offset(w * 0.5f, h * 0.35f), style = Stroke(4f))
                    drawCircle(color = SakuraPink, radius = w * 0.08f, center = Offset(w * 0.5f, h * 0.55f))
                }
                categoryClean.contains("transport") || categoryClean.contains("travel") -> {
                    // Speed Metro/Scooter Badge
                    drawCircle(color = Color.White, radius = w * 0.18f, center = Offset(w * 0.35f, h * 0.65f))
                    drawCircle(color = Color.White, radius = w * 0.18f, center = Offset(w * 0.65f, h * 0.65f))
                    val body = Path().apply {
                        moveTo(w * 0.2f, h * 0.55f)
                        lineTo(w * 0.8f, h * 0.45f)
                        lineTo(w * 0.7f, h * 0.3f)
                        lineTo(w * 0.3f, h * 0.3f)
                        close()
                    }
                    drawPath(body, color = Color.White)
                }
                categoryClean.contains("bill") || categoryClean.contains("utility") -> {
                    // Energy Zap / Bolt
                    val zap = Path().apply {
                        moveTo(w * 0.55f, h * 0.05f)
                        lineTo(w * 0.2f, h * 0.55f)
                        lineTo(w * 0.5f, h * 0.55f)
                        lineTo(w * 0.45f, h * 0.95f)
                        lineTo(w * 0.8f, h * 0.45f)
                        lineTo(w * 0.5f, h * 0.45f)
                        close()
                    }
                    drawPath(zap, color = Color.White)
                }
                categoryClean.contains("entertain") || categoryClean.contains("game") -> {
                    // Anime Game Controller
                    drawRoundRect(
                        color = Color.White,
                        topLeft = Offset(w * 0.15f, h * 0.3f),
                        size = Size(w * 0.7f, h * 0.45f),
                        cornerRadius = CornerRadius(12f, 12f),
                    )
                    drawCircle(color = ElectricViolet, radius = w * 0.05f, center = Offset(w * 0.3f, h * 0.52f))
                    drawCircle(color = SakuraPink, radius = w * 0.05f, center = Offset(w * 0.7f, h * 0.46f))
                    drawCircle(color = NeonCyan, radius = w * 0.05f, center = Offset(w * 0.63f, h * 0.56f))
                }
                categoryClean.contains("health") || categoryClean.contains("medical") -> {
                    // Potion Bottle / Heart
                    val heart = Path().apply {
                        moveTo(w * 0.5f, h * 0.75f)
                        cubicTo(w * 0.2f, h * 0.5f, w * 0.15f, h * 0.25f, w * 0.35f, h * 0.2f)
                        cubicTo(w * 0.45f, h * 0.2f, w * 0.5f, h * 0.3f, w * 0.5f, h * 0.3f)
                        cubicTo(w * 0.5f, h * 0.3f, w * 0.55f, h * 0.2f, w * 0.65f, h * 0.2f)
                        cubicTo(w * 0.85f, h * 0.25f, w * 0.8f, h * 0.5f, w * 0.5f, h * 0.75f)
                        close()
                    }
                    drawPath(heart, color = Color.White)
                }
                categoryClean.contains("salary") || categoryClean.contains("income") -> {
                    // Glowing Coin/Chest
                    drawCircle(color = Color.White, radius = w * 0.32f, center = Offset(w * 0.5f, h * 0.5f))
                    drawCircle(color = Color(0xFFFFB703), radius = w * 0.22f, center = Offset(w * 0.5f, h * 0.5f))
                    drawLine(color = Color.White, start = Offset(w * 0.5f, h * 0.35f), end = Offset(w * 0.5f, h * 0.65f), strokeWidth = 4f)
                }
                categoryClean.contains("invest") -> {
                    // Crystal Gem
                    val gem = Path().apply {
                        moveTo(w * 0.5f, h * 0.1f)
                        lineTo(w * 0.85f, h * 0.38f)
                        lineTo(w * 0.5f, h * 0.9f)
                        lineTo(w * 0.15f, h * 0.38f)
                        close()
                    }
                    drawPath(gem, color = Color.White)
                }
                else -> {
                    // Sakura Star Badge
                    val star = Path().apply {
                        moveTo(w * 0.5f, h * 0.1f)
                        lineTo(w * 0.62f, h * 0.35f)
                        lineTo(w * 0.9f, h * 0.38f)
                        lineTo(w * 0.7f, h * 0.58f)
                        lineTo(w * 0.76f, h * 0.88f)
                        lineTo(w * 0.5f, h * 0.72f)
                        lineTo(w * 0.24f, h * 0.88f)
                        lineTo(w * 0.3f, h * 0.58f)
                        lineTo(w * 0.1f, h * 0.38f)
                        lineTo(w * 0.38f, h * 0.35f)
                        close()
                    }
                    drawPath(star, color = Color.White)
                }
            }
        }
    }
}
