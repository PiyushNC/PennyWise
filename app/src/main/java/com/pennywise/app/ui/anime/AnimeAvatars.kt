package com.pennywise.app.ui.anime

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pennywise.app.ui.theme.AnimeMidnightBg
import com.pennywise.app.ui.theme.ElectricViolet
import com.pennywise.app.ui.theme.NeonCyan
import com.pennywise.app.ui.theme.SakuraPink
import com.pennywise.app.ui.theme.SakuraPinkLight
import com.pennywise.app.ui.theme.SoftLavender

data class AnimeAvatarInfo(
    val id: String,
    val name: String,
    val title: String,
    val primaryColor: Color,
)

val AvailableAnimeAvatars = listOf(
    AnimeAvatarInfo("moon", "Penny", "Sakura Guardian", SakuraPink),
    AnimeAvatarInfo("star", "Ren", "Cyber Samurai", NeonCyan),
    AnimeAvatarInfo("leaf", "Yuki", "Kitsune Spirit", Color(0xFFFF9E00)),
    AnimeAvatarInfo("astral", "Sora", "Astral Traveler", ElectricViolet),
    AnimeAvatarInfo("chibi", "Chibi Penny", "Pocket Mascot", SakuraPinkLight),
    AnimeAvatarInfo("kaito", "Kaito", "Night Wanderer", Color(0xFF3A86EF)),
)

@Composable
fun AnimeAvatarView(
    avatarId: String,
    modifier: Modifier = Modifier,
    size: Dp = 64.dp,
    isSelected: Boolean = false,
    onClick: (() -> Unit)? = null,
) {
    val avatarInfo = AvailableAnimeAvatars.find { it.id == avatarId } ?: AvailableAnimeAvatars[0]

    val clickableModifier = if (onClick != null) {
        Modifier.clickable { onClick() }
    } else Modifier

    val borderBrush = if (isSelected) {
        Brush.sweepGradient(listOf(SakuraPink, ElectricViolet, NeonCyan, SakuraPink))
    } else {
        Brush.linearGradient(listOf(avatarInfo.primaryColor.copy(alpha = 0.5f), ElectricViolet.copy(alpha = 0.3f)))
    }

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .border(
                width = if (isSelected) 3.dp else 1.5.dp,
                brush = borderBrush,
                shape = CircleShape,
            )
            .then(clickableModifier),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val w = this.size.width
            val h = this.size.height
            val center = Offset(w / 2f, h / 2f)

            // Background gradient
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        avatarInfo.primaryColor.copy(alpha = 0.8f),
                        Color(0xFF1B1233),
                    ),
                    center = center,
                    radius = w * 0.7f,
                ),
                radius = w / 2f,
            )

            // Render Character Silhouette/Portrait depending on ID
            when (avatarId) {
                "star" -> {
                    // Cyber Samurai Ren (Mask + Visor)
                    drawRect(color = Color(0xFF111124), topLeft = Offset(w * 0.3f, h * 0.3f), size = Size(w * 0.4f, h * 0.5f))
                    drawRoundRect(
                        color = NeonCyan,
                        topLeft = Offset(w * 0.28f, h * 0.42f),
                        size = Size(w * 0.44f, h * 0.12f),
                        cornerRadius = CornerRadius(6f, 6f),
                    )
                    drawLine(color = Color.White, start = Offset(w * 0.3f, h * 0.48f), end = Offset(w * 0.7f, h * 0.48f), strokeWidth = 3f)
                }
                "leaf" -> {
                    // Kitsune Spirit Yuki (Fox Ears + Mask)
                    val leftEar = Path().apply {
                        moveTo(w * 0.3f, h * 0.4f)
                        lineTo(w * 0.2f, h * 0.15f)
                        lineTo(w * 0.45f, h * 0.3f)
                        close()
                    }
                    val rightEar = Path().apply {
                        moveTo(w * 0.7f, h * 0.4f)
                        lineTo(w * 0.8f, h * 0.15f)
                        lineTo(w * 0.55f, h * 0.3f)
                        close()
                    }
                    drawPath(leftEar, color = Color(0xFFFF9E00))
                    drawPath(rightEar, color = Color(0xFFFF9E00))
                    drawCircle(color = Color.White, radius = w * 0.25f, center = Offset(w * 0.5f, h * 0.55f))
                    drawCircle(color = SakuraPink, radius = w * 0.05f, center = Offset(w * 0.5f, h * 0.6f))
                }
                "astral" -> {
                    // Astral Traveler Sora (Star Aura + Hood)
                    val hood = Path().apply {
                        moveTo(w * 0.2f, h * 0.7f)
                        cubicTo(w * 0.2f, h * 0.2f, w * 0.8f, h * 0.2f, w * 0.8f, h * 0.7f)
                        close()
                    }
                    drawPath(hood, color = ElectricViolet)
                    drawCircle(color = Color.White, radius = w * 0.04f, center = Offset(w * 0.42f, h * 0.48f))
                    drawCircle(color = Color.White, radius = w * 0.04f, center = Offset(w * 0.58f, h * 0.48f))
                }
                "chibi" -> {
                    // Chibi Penny (Giant Eyes + Big Bow)
                    drawCircle(color = Color(0xFFFFE5D9), radius = w * 0.28f, center = Offset(w * 0.5f, h * 0.5f))
                    val bow = Path().apply {
                        moveTo(w * 0.5f, h * 0.25f)
                        lineTo(w * 0.3f, h * 0.18f)
                        lineTo(w * 0.3f, h * 0.32f)
                        lineTo(w * 0.5f, h * 0.25f)
                        lineTo(w * 0.7f, h * 0.18f)
                        lineTo(w * 0.7f, h * 0.32f)
                        close()
                    }
                    drawPath(bow, color = SakuraPink)
                    drawCircle(color = ElectricViolet, radius = w * 0.07f, center = Offset(w * 0.4f, h * 0.52f))
                    drawCircle(color = ElectricViolet, radius = w * 0.07f, center = Offset(w * 0.6f, h * 0.52f))
                }
                "kaito" -> {
                    // Night Wanderer Kaito (Anime Glasses + Scarf)
                    drawCircle(color = Color(0xFFFFE5D9), radius = w * 0.25f, center = Offset(w * 0.5f, h * 0.45f))
                    // Hair
                    val hair = Path().apply {
                        moveTo(w * 0.25f, h * 0.45f)
                        cubicTo(w * 0.3f, h * 0.18f, w * 0.7f, h * 0.18f, w * 0.75f, h * 0.45f)
                        lineTo(w * 0.6f, h * 0.35f)
                        lineTo(w * 0.5f, h * 0.42f)
                        lineTo(w * 0.4f, h * 0.35f)
                        close()
                    }
                    drawPath(hair, color = Color(0xFF1E293B))
                    // Glasses
                    drawCircle(color = Color.Transparent, radius = w * 0.08f, center = Offset(w * 0.4f, h * 0.48f), style = Stroke(3f))
                    drawCircle(color = Color.Transparent, radius = w * 0.08f, center = Offset(w * 0.6f, h * 0.48f), style = Stroke(3f))
                    drawLine(color = Color.White, start = Offset(w * 0.48f, h * 0.48f), end = Offset(w * 0.52f, h * 0.48f), strokeWidth = 3f)
                }
                else -> {
                    // Default Sakura Penny (Standard Companion)
                    drawCircle(color = Color(0xFFFFE5D9), radius = w * 0.25f, center = Offset(w * 0.5f, h * 0.48f))
                    val hair = Path().apply {
                        moveTo(w * 0.25f, h * 0.48f)
                        cubicTo(w * 0.3f, h * 0.2f, w * 0.7f, h * 0.2f, w * 0.75f, h * 0.48f)
                        lineTo(w * 0.65f, h * 0.38f)
                        lineTo(w * 0.5f, h * 0.44f)
                        lineTo(w * 0.35f, h * 0.38f)
                        close()
                    }
                    drawPath(hair, color = SakuraPink)
                    drawCircle(color = ElectricViolet, radius = w * 0.05f, center = Offset(w * 0.4f, h * 0.5f))
                    drawCircle(color = ElectricViolet, radius = w * 0.05f, center = Offset(w * 0.6f, h * 0.5f))
                }
            }
        }
    }
}
