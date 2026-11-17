package com.pennywise.app.ui.anime

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pennywise.app.ui.theme.ElectricViolet
import com.pennywise.app.ui.theme.NeonCyan
import com.pennywise.app.ui.theme.SakuraPink
import com.pennywise.app.ui.theme.SakuraPinkLight
import com.pennywise.app.ui.theme.SoftLavender

@Composable
fun AnimeEmptyStateIllustration(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier.size(160.dp),
            contentAlignment = Alignment.Center,
        ) {
            Canvas(modifier = Modifier.size(160.dp)) {
                val w = size.width
                val h = size.height
                val center = Offset(w / 2f, h / 2f)

                // Background glowing moon
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            SakuraPink.copy(alpha = 0.4f),
                            ElectricViolet.copy(alpha = 0.2f),
                            Color.Transparent,
                        ),
                        center = center,
                        radius = w * 0.5f,
                    ),
                    radius = w * 0.45f,
                )

                drawCircle(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFFFB5A7).copy(alpha = 0.3f),
                            Color(0xFF7B2CBF).copy(alpha = 0.2f),
                        ),
                    ),
                    radius = w * 0.35f,
                    center = center,
                )

                // Cherry Blossom Branch
                val branch = Path().apply {
                    moveTo(w * 0.1f, h * 0.2f)
                    cubicTo(w * 0.3f, h * 0.15f, w * 0.6f, h * 0.35f, w * 0.9f, h * 0.25f)
                }
                drawPath(branch, color = Color(0xFF4A3770), style = Stroke(width = 8f))

                // Blossoms on Branch
                drawCircle(color = SakuraPink, radius = 12f, center = Offset(w * 0.35f, h * 0.18f))
                drawCircle(color = SakuraPinkLight, radius = 10f, center = Offset(w * 0.55f, h * 0.28f))
                drawCircle(color = SakuraPink, radius = 14f, center = Offset(w * 0.75f, h * 0.24f))

                // Empty Treasure Scroll / Card
                val scroll = Path().apply {
                    moveTo(w * 0.3f, h * 0.5f)
                    lineTo(w * 0.7f, h * 0.5f)
                    lineTo(w * 0.75f, h * 0.8f)
                    lineTo(w * 0.25f, h * 0.8f)
                    close()
                }
                drawPath(
                    scroll,
                    brush = Brush.linearGradient(
                        listOf(ElectricViolet.copy(alpha = 0.6f), Color(0xFF241C3F)),
                    ),
                )
                drawPath(
                    scroll,
                    color = SakuraPink,
                    style = Stroke(width = 3f),
                )

                // Sparkle / Glowing Star
                drawCircle(color = Color.White, radius = 5f, center = Offset(w * 0.5f, h * 0.65f))
                drawCircle(color = NeonCyan, radius = 8f, center = Offset(w * 0.65f, h * 0.58f))
            }

            // Companion Avatar overlay
            PennyCompanionPortrait(size = 56.dp, emotion = CompanionEmotion.ENCOURAGING)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp,
        )
    }
}
