package com.pennywise.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocalCafe
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.Work
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun CategoryIcon(
    category: String,
    modifier: Modifier = Modifier,
) {
    val icon = when (category.lowercase()) {
        "home", "rent" -> Icons.Outlined.Home
        "transport", "car" -> Icons.Outlined.DirectionsCar
        "food", "coffee" -> Icons.Outlined.LocalCafe
        "shopping" -> Icons.Outlined.ShoppingBag
        "fitness" -> Icons.Outlined.FitnessCenter
        "salary", "work" -> Icons.Outlined.Work
        else -> Icons.Outlined.Category
    }
    val tint = when (category.lowercase()) {
        "food", "coffee" -> Color(0xFFE59B54)
        "shopping" -> Color(0xFF8A78D7)
        "transport", "car" -> Color(0xFF4D9BD1)
        "home", "rent" -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.secondary
    }

    Box(
        modifier = modifier
            .size(44.dp)
            .background(tint.copy(alpha = 0.14f), CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = category,
            tint = tint,
            modifier = Modifier.size(22.dp),
        )
    }
}
