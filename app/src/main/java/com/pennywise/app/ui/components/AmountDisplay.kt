package com.pennywise.app.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun AmountDisplay(
    amount: String,
    modifier: Modifier = Modifier,
    label: String? = null,
    positive: Boolean? = null,
    prominent: Boolean = false,
) {
    Column(modifier = modifier) {
        label?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Row {
            Text(
                text = amount,
                style = if (prominent) {
                    MaterialTheme.typography.displaySmall
                } else {
                    MaterialTheme.typography.titleMedium
                },
                fontWeight = if (prominent) FontWeight.Bold else FontWeight.SemiBold,
                color = when (positive) {
                    true -> MaterialTheme.colorScheme.primary
                    false -> MaterialTheme.colorScheme.error
                    null -> MaterialTheme.colorScheme.onSurface
                },
            )
            if (positive != null) {
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (positive) "Income" else "Expense",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (positive) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.error
                    },
                )
            }
        }
    }
}
