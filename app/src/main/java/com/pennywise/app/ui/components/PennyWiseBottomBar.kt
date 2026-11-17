package com.pennywise.app.ui.components

import androidx.compose.foundation.border
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pennywise.app.ui.navigation.PennyWiseDestination
import com.pennywise.app.ui.theme.AnimeMidnightBg
import com.pennywise.app.ui.theme.ElectricViolet
import com.pennywise.app.ui.theme.SakuraPink

@Composable
fun PennyWiseBottomBar(
    destinations: List<PennyWiseDestination>,
    currentRoute: String?,
    onDestinationSelected: (PennyWiseDestination) -> Unit,
) {
    val isDark = MaterialTheme.colorScheme.background == AnimeMidnightBg

    val navBg = if (isDark) Color(0xFF16102A) else Color(0xFFFFF0F6)

    NavigationBar(
        modifier = Modifier.border(
            width = 1.dp,
            brush = Brush.horizontalGradient(
                listOf(
                    SakuraPink.copy(alpha = 0.4f),
                    ElectricViolet.copy(alpha = 0.4f),
                ),
            ),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        ),
        containerColor = navBg,
        tonalElevation = 8.dp,
    ) {
        destinations.forEach { destination ->
            val isSelected = currentRoute == destination.route
            NavigationBarItem(
                selected = isSelected,
                onClick = { onDestinationSelected(destination) },
                icon = {
                    Icon(
                        imageVector = destination.icon,
                        contentDescription = destination.label,
                    )
                },
                label = {
                    Text(
                        text = destination.label,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        letterSpacing = 0.3.sp,
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = SakuraPink,
                    selectedTextColor = SakuraPink,
                    indicatorColor = SakuraPink.copy(alpha = 0.2f),
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
            )
        }
    }
}
