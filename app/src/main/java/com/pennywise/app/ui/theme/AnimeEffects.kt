package com.pennywise.app.ui.theme

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.sin
import kotlin.random.Random

private data class Petal(
    val xRatio: Float,
    val initialYRatio: Float,
    val speed: Float,
    val scale: Float,
    val rotationSpeed: Float,
    val swayFrequency: Float,
)

@Composable
fun SakuraPetalsOverlay(
    modifier: Modifier = Modifier,
    petalCount: Int = 18,
) {
    val transition = rememberInfiniteTransition(label = "sakura_falling")
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 14000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "sakura_progress",
    )

    val petals = remember {
        List(petalCount) {
            Petal(
                xRatio = Random.nextFloat(),
                initialYRatio = Random.nextFloat(),
                speed = 0.5f + Random.nextFloat() * 0.8f,
                scale = 6f + Random.nextFloat() * 10f,
                rotationSpeed = (Random.nextFloat() - 0.5f) * 360f,
                swayFrequency = 2f + Random.nextFloat() * 3f,
            )
        }
    }

    val petalColor = SakuraPink.copy(alpha = 0.35f)
    val petalHighlight = SakuraPinkLight.copy(alpha = 0.5f)

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        petals.forEach { petal ->
            val rawY = (petal.initialYRatio + progress * petal.speed) % 1.2f - 0.1f
            val y = rawY * height
            val sway = sin((progress * 2 * Math.PI * petal.swayFrequency).toFloat()) * 30f
            val x = (petal.xRatio * width + sway) % width

            val rotation = progress * petal.rotationSpeed

            rotate(degrees = rotation, pivot = Offset(x, y)) {
                val path = Path().apply {
                    moveTo(x, y - petal.scale)
                    cubicTo(
                        x + petal.scale * 0.8f, y - petal.scale * 0.5f,
                        x + petal.scale * 0.6f, y + petal.scale * 0.8f,
                        x, y + petal.scale,
                    )
                    cubicTo(
                        x - petal.scale * 0.6f, y + petal.scale * 0.8f,
                        x - petal.scale * 0.8f, y - petal.scale * 0.5f,
                        x, y - petal.scale,
                    )
                    close()
                }
                drawPath(path = path, color = petalColor)
                drawCircle(
                    color = petalHighlight,
                    radius = petal.scale * 0.25f,
                    center = Offset(x, y - petal.scale * 0.2f),
                )
            }
        }
    }
}

@Composable
fun AnimeAtmosphericBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val bgColors = if (MaterialTheme.colorScheme.background == AnimeMidnightBg) {
        listOf(
            AnimeMidnightBg,
            Color(0xFF16102B),
            Color(0xFF1E1538),
            AnimeMidnightBg,
        )
    } else {
        listOf(
            SakuraDayBg,
            Color(0xFFF9ECFF),
            Color(0xFFFFF4F8),
            SakuraDayBg,
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(bgColors)),
    ) {
        SakuraPetalsOverlay(modifier = Modifier.fillMaxSize())
        content()
    }
}

fun Modifier.glowingNeonBorder(
    shape: Shape,
    borderWidth: Dp = 1.5.dp,
    glowColor: Color = SakuraPink,
): Modifier = this
    .border(
        width = borderWidth,
        brush = Brush.horizontalGradient(
            colors = listOf(
                glowColor.copy(alpha = 0.8f),
                ElectricViolet.copy(alpha = 0.9f),
                NeonCyan.copy(alpha = 0.7f),
                glowColor.copy(alpha = 0.8f),
            ),
        ),
        shape = shape,
    )
