package com.pennywise.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.pennywise.app.domain.model.Budget
import com.pennywise.app.ui.components.PennyWisePrimaryButton
import com.pennywise.app.ui.theme.PennyWiseSpacing
import com.pennywise.app.ui.theme.PennyWiseTheme
import com.pennywise.app.viewmodel.BudgetViewModel
import com.pennywise.app.viewmodel.currentMonthStart

private val budgetCategories = listOf("Food", "Home", "Transport", "Shopping", "Entertainment", "Health", "Other")

@Composable
fun AddEditBudgetScreen(
    onBack: () -> Unit,
    onSaved: () -> Unit,
    viewModel: BudgetViewModel,
    budgetId: Long? = null,
) {
    var existing by remember { mutableStateOf<Budget?>(null) }
    var duplicateMessage by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(budgetId) {
        existing = budgetId?.let { viewModel.getBudget(it) }
    }
    AddEditBudgetContent(
        existing = existing,
        onBack = onBack,
        duplicateMessage = duplicateMessage,
        onDuplicate = { duplicateMessage = it },
        onSave = { budget ->
            viewModel.save(
                budget = budget,
                onSaved = onSaved,
                onDuplicate = { duplicateMessage = "A budget already exists for this category this month." },
            )
        },
    )
}

@Composable
private fun AddEditBudgetContent(
    existing: Budget?,
    onBack: () -> Unit,
    onSave: (Budget) -> Unit,
    duplicateMessage: String? = null,
    onDuplicate: (String) -> Unit = {},
) {
    var amount by remember(existing) { mutableStateOf(existing?.amount?.toString().orEmpty()) }
    var category by remember(existing) { mutableStateOf(existing?.category) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .imePadding()
            .padding(PennyWiseSpacing.screen),
        verticalArrangement = Arrangement.spacedBy(PennyWiseSpacing.standard),
    ) {
        Text(
            text = if (existing == null) "Create budget" else "Edit budget",
            style = MaterialTheme.typography.headlineSmall,
        )
        Text(
            text = if (category == null) {
                "Set a limit for all expenses this month."
            } else {
                "Set a limit for ${category.orEmpty()} this month."
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it; errorMessage = null },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Budget amount (₹)") },
            singleLine = true,
            isError = errorMessage != null,
        )
        Text("Budget type", style = MaterialTheme.typography.titleMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(PennyWiseSpacing.small)) {
            FilterChip(
                selected = category == null,
                onClick = { category = null },
                label = { Text("Overall monthly") },
            )
            FilterChip(
                selected = category != null,
                onClick = { if (category == null) category = budgetCategories.first() },
                label = { Text("Category") },
            )
        }
        if (category != null) {
            Text("Category", style = MaterialTheme.typography.titleMedium)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(PennyWiseSpacing.small),
            ) {
                budgetCategories.forEach { option ->
                    FilterChip(
                        selected = category == option,
                        onClick = { category = option },
                        label = { Text(option) },
                    )
                }
            }
        }
        errorMessage?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
        }
        duplicateMessage?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(PennyWiseSpacing.small),
        ) {
            TextButton(onClick = onBack, modifier = Modifier.weight(1f)) {
                Text("Cancel")
            }
            PennyWisePrimaryButton(
                text = "Save budget",
                onClick = {
                    val parsedAmount = amount.toDoubleOrNull()
                    errorMessage = when {
                        parsedAmount == null || parsedAmount <= 0 ->
                            "Budget amount must be greater than zero."
                        else -> null
                    }
                    if (errorMessage == null) {
                        onSave(
                            Budget(
                                id = existing?.id ?: 0L,
                                monthStart = existing?.monthStart ?: currentMonthStart(),
                                category = category,
                                amount = parsedAmount!!,
                            ),
                        )
                    }
                },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AddEditBudgetPreview() {
    PennyWiseTheme {
        AddEditBudgetContent(existing = null, onBack = {}, onSave = {})
    }
}
