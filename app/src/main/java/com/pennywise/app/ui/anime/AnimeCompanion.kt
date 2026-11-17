package com.pennywise.app.ui.anime

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pennywise.app.ui.theme.ElectricViolet
import com.pennywise.app.ui.theme.NeonCyan
import com.pennywise.app.ui.theme.SakuraPink
import com.pennywise.app.ui.theme.SakuraPinkLight
import com.pennywise.app.ui.theme.SoftLavender

enum class CompanionEmotion {
    JOY,         // Spending well under budget / high income
    ENCOURAGING, // Normal healthy budget
    WARNING,     // Approaching budget limit
    ALERT,       // Over budget
    PROUD,       // Great savings / goal reached
}

@Composable
fun PennyCompanionPortrait(
    modifier: Modifier = Modifier,
    size: Dp = 84.dp,
    emotion: CompanionEmotion = CompanionEmotion.ENCOURAGING,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "companion_float")
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "float_offset",
    )

    Box(
        modifier = modifier
            .size(size)
            .padding(top = (floatOffset.coerceAtLeast(0f)).dp),
        contentAlignment = Alignment.Center,
    ) {
        // Glowing Background Circle
        Canvas(modifier = Modifier.size(size)) {
            val center = Offset(this.size.width / 2f, this.size.height / 2f)
            val radius = this.size.width / 2f - 4f

            // Aura glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        SakuraPink.copy(alpha = 0.6f),
                        ElectricViolet.copy(alpha = 0.3f),
                        Color.Transparent,
                    ),
                    center = center,
                    radius = radius * 1.3f,
                ),
                radius = radius * 1.2f,
            )

            // Main disc
            drawCircle(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF381F54),
                        Color(0xFF1F1538),
                    ),
                ),
                radius = radius,
                center = center,
            )

            // Neon ring
            drawCircle(
                brush = Brush.sweepGradient(
                    colors = listOf(SakuraPink, ElectricViolet, NeonCyan, SakuraPink),
                    center = center,
                ),
                radius = radius,
                center = center,
                style = Stroke(width = 4f),
            )

            // Drawing Penny Anime Companion Character
            val w = this.size.width
            val h = this.size.height

            // Hair back (twin tails)
            val leftTail = Path().apply {
                moveTo(w * 0.25f, h * 0.45f)
                cubicTo(w * 0.05f, h * 0.45f, w * 0.02f, h * 0.75f, w * 0.15f, h * 0.88f)
                cubicTo(w * 0.25f, h * 0.95f, w * 0.3f, h * 0.75f, w * 0.32f, h * 0.58f)
            }
            val rightTail = Path().apply {
                moveTo(w * 0.75f, h * 0.45f)
                cubicTo(w * 0.95f, h * 0.45f, w * 0.98f, h * 0.75f, w * 0.85f, h * 0.88f)
                cubicTo(w * 0.75f, h * 0.95f, w * 0.7f, h * 0.75f, w * 0.68f, h * 0.58f)
            }
            drawPath(leftTail, color = Color(0xFFFF5C8A))
            drawPath(rightTail, color = Color(0xFFFF5C8A))

            // Neck & Shoulders
            drawRect(
                color = Color(0xFFFFDFC4),
                topLeft = Offset(w * 0.44f, h * 0.65f),
                size = Size(w * 0.12f, h * 0.15f),
            )
            // Sailor Collar Ribbon
            val ribbonPath = Path().apply {
                moveTo(w * 0.38f, h * 0.72f)
                lineTo(w * 0.50f, h * 0.88f)
                lineTo(w * 0.62f, h * 0.72f)
                lineTo(w * 0.50f, h * 0.78f)
                close()
            }
            drawPath(ribbonPath, color = SakuraPink)
            drawCircle(color = ElectricViolet, radius = w * 0.04f, center = Offset(w * 0.5f, h * 0.76f))

            // Face Shape
            val facePath = Path().apply {
                moveTo(w * 0.32f, h * 0.38f)
                lineTo(w * 0.68f, h * 0.38f)
                cubicTo(w * 0.68f, h * 0.62f, w * 0.58f, h * 0.74f, w * 0.5f, h * 0.76f)
                cubicTo(w * 0.42f, h * 0.74f, w * 0.32f, h * 0.62f, w * 0.32f, h * 0.38f)
            }
            drawPath(facePath, color = Color(0xFFFFE5D9))

            // Bangs (Hair Front)
            val bangs = Path().apply {
                moveTo(w * 0.3f, h * 0.38f)
                cubicTo(w * 0.35f, h * 0.22f, w * 0.65f, h * 0.22f, w * 0.7f, h * 0.38f)
                lineTo(w * 0.62f, h * 0.44f)
                lineTo(w * 0.55f, h * 0.36f)
                lineTo(w * 0.50f, h * 0.46f)
                lineTo(w * 0.45f, h * 0.36f)
                lineTo(w * 0.38f, h * 0.44f)
                close()
            }
            drawPath(bangs, color = Color(0xFFFF5C8A))

            // Anime Eyes
            val eyeColor = when (emotion) {
                CompanionEmotion.ALERT, CompanionEmotion.WARNING -> Color(0xFFFF4D6D)
                else -> ElectricViolet
            }

            // Left Eye
            drawRoundRect(
                color = eyeColor,
                topLeft = Offset(w * 0.38f, h * 0.48f),
                size = Size(w * 0.08f, h * 0.12f),
                cornerRadius = CornerRadius(8f, 8f),
            )
            drawCircle(color = Color.White, radius = w * 0.02f, center = Offset(w * 0.4f, h * 0.51f))

            // Right Eye
            drawRoundRect(
                color = eyeColor,
                topLeft = Offset(w * 0.54f, h * 0.48f),
                size = Size(w * 0.08f, h * 0.12f),
                cornerRadius = CornerRadius(8f, 8f),
            )
            drawCircle(color = Color.White, radius = w * 0.02f, center = Offset(w * 0.56f, h * 0.51f))

            // Cheeks (Blush)
            drawCircle(color = SakuraPink.copy(alpha = 0.5f), radius = w * 0.04f, center = Offset(w * 0.36f, h * 0.59f))
            drawCircle(color = SakuraPink.copy(alpha = 0.5f), radius = w * 0.04f, center = Offset(w * 0.64f, h * 0.59f))

            // Mouth based on Emotion
            when (emotion) {
                CompanionEmotion.JOY, CompanionEmotion.PROUD -> {
                    val smile = Path().apply {
                        moveTo(w * 0.45f, h * 0.63f)
                        quadraticTo(w * 0.50f, h * 0.68f, w * 0.55f, h * 0.63f)
                    }
                    drawPath(smile, color = Color(0xFFD62828), style = Stroke(width = 3f))
                }
                CompanionEmotion.WARNING -> {
                    drawLine(color = Color(0xFF8D99AE), start = Offset(w * 0.46f, h * 0.65f), end = Offset(w * 0.54f, h * 0.65f), strokeWidth = 3f)
                }
                CompanionEmotion.ALERT -> {
                    val mouthOpen = Path().apply {
                        moveTo(w * 0.46f, h * 0.63f)
                        quadraticTo(w * 0.50f, h * 0.69f, w * 0.54f, h * 0.63f)
                        close()
                    }
                    drawPath(mouthOpen, color = Color(0xFFD62828))
                }
                CompanionEmotion.ENCOURAGING -> {
                    val gentleSmile = Path().apply {
                        moveTo(w * 0.46f, h * 0.64f)
                        quadraticTo(w * 0.50f, h * 0.67f, w * 0.54f, h * 0.64f)
                    }
                    drawPath(gentleSmile, color = Color(0xFFD62828), style = Stroke(width = 3f))
                }
            }

            // Sakura Hairpin
            drawCircle(color = SakuraPinkLight, radius = w * 0.04f, center = Offset(w * 0.33f, h * 0.32f))
            drawCircle(color = Color.White, radius = w * 0.015f, center = Offset(w * 0.33f, h * 0.32f))
        }
    }
}

@Composable
fun AnimeSpeechBubbleCard(
    message: String,
    title: String = "PENNY'S ADVICE",
    emotion: CompanionEmotion = CompanionEmotion.ENCOURAGING,
    modifier: Modifier = Modifier,
) {
    val cardBg = if (MaterialTheme.colorScheme.background == com.pennywise.app.ui.theme.AnimeMidnightBg) {
        Brush.linearGradient(
            colors = listOf(
                Color(0xFF2B1B48),
                Color(0xFF1E1436),
            ),
        )
    } else {
        Brush.linearGradient(
            colors = listOf(
                Color(0xFFFFF5F8),
                Color(0xFFF7E8FF),
            ),
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(cardBg)
            .border(
                width = 1.5.dp,
                brush = Brush.horizontalGradient(
                    listOf(
                        SakuraPink.copy(alpha = 0.8f),
                        ElectricViolet.copy(alpha = 0.6f),
                    ),
                ),
                shape = RoundedCornerShape(24.dp),
            )
            .padding(18.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            PennyCompanionPortrait(size = 72.dp, emotion = emotion)

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(SakuraPink.copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 2.dp),
                    ) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = SakuraPink,
                            letterSpacing = 1.sp,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 20.sp,
                )
            }
        }
    }
}
