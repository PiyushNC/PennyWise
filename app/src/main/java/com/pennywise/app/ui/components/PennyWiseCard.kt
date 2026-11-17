package com.pennywise.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.pennywise.app.ui.theme.AnimeCardDark
import com.pennywise.app.ui.theme.AnimeMidnightBg
import com.pennywise.app.ui.theme.ElectricViolet
import com.pennywise.app.ui.theme.SakuraPink

@Composable
fun PennyWiseCard(
    modifier: Modifier = Modifier,
    glowBorder: Boolean = true,
    content: @Composable ColumnScope.() -> Unit,
) {
    val isDark = MaterialTheme.colorScheme.background == AnimeMidnightBg

    val cardBg = if (isDark) {
        Brush.linearGradient(
            colors = listOf(
                Color(0xFF261D45),
                Color(0xFF1B1533),
            ),
        )
    } else {
        Brush.linearGradient(
            colors = listOf(
                Color(0xFFFFFFFF),
                Color(0xFFFCF5FF),
            ),
        )
    }

    val borderModifier = if (glowBorder) {
        Modifier.border(
            width = 1.dp,
            brush = Brush.horizontalGradient(
                colors = listOf(
                    SakuraPink.copy(alpha = if (isDark) 0.5f else 0.4f),
                    ElectricViolet.copy(alpha = if (isDark) 0.4f else 0.3f),
                ),
            ),
            shape = MaterialTheme.shapes.medium,
        )
    } else Modifier

    Card(
        modifier = modifier.then(borderModifier),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        androidx.compose.foundation.layout.Column(
            modifier = Modifier
                .background(cardBg)
                .padding(20.dp),
            content = content,
        )
    }
}
