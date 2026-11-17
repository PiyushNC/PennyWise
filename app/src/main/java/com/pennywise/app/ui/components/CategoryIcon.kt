package com.pennywise.app.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pennywise.app.ui.anime.AnimeCategoryBadge

@Composable
fun CategoryIcon(
    category: String,
    modifier: Modifier = Modifier,
    size: Dp = 44.dp,
) {
    AnimeCategoryBadge(
        category = category,
        modifier = modifier,
        size = size,
    )
}
