package com.pennywise.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pennywise.app.ui.components.BudgetProgress
import com.pennywise.app.ui.components.PennyWiseCard
import com.pennywise.app.ui.theme.PennyWiseSpacing
import com.pennywise.app.ui.theme.PennyWiseTheme
import com.pennywise.app.viewmodel.BudgetSummary
import com.pennywise.app.viewmodel.BudgetUiState
import com.pennywise.app.viewmodel.BudgetViewModel
import java.text.NumberFormat
import java.util.Locale
import kotlinx.coroutines.launch

@Composable
fun BudgetsScreen(
    onAddBudget: () -> Unit,
    onEditBudget: (Long) -> Unit,
    viewModel: BudgetViewModel,
) {
    val uiState by viewModel.uiState.collectAsState()
    BudgetsContent(
        uiState = uiState,
        onAddBudget = onAddBudget,
        onEditBudget = onEditBudget,
        onDeleteBudget = viewModel::delete,
    )
}

@Composable
private fun BudgetsContent(
    uiState: BudgetUiState,
    onAddBudget: () -> Unit,
    onEditBudget: (Long) -> Unit,
    onDeleteBudget: (BudgetSummary) -> Unit,
) {
    var pendingDelete by remember { mutableStateOf<BudgetSummary?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    pendingDelete?.let { summary ->
        AlertDialog(
            onDismissRequest = { pendingDelete = null },
            title = { Text("Delete budget?") },
            text = { Text("This budget will be permanently removed.") },
            confirmButton = {
                TextButton(onClick = {
                    onDeleteBudget(summary)
                    pendingDelete = null
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Budget deleted")
                    }
                }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingDelete = null }) { Text("Cancel") }
            },
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddBudget,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ) {
                Icon(Icons.Outlined.Add, contentDescription = "Add budget")
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = PennyWiseSpacing.screen),
            verticalArrangement = Arrangement.spacedBy(PennyWiseSpacing.standard),
        ) {
            item {
                Column(
                    modifier = Modifier.padding(top = PennyWiseSpacing.large),
                    verticalArrangement = Arrangement.spacedBy(PennyWiseSpacing.compact),
                ) {
                    Text("Budgets", style = MaterialTheme.typography.headlineSmall)
                    Text(
                        "Track your spending plan for ${uiState.monthLabel}.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            if (!uiState.hasBudgets) {
                item {
                    PennyWiseCard(modifier = Modifier.fillMaxWidth()) {
                        Text("No budgets yet", style = MaterialTheme.typography.titleLarge)
                        Text(
                            "Set an overall monthly budget or create a category budget to start.",
                            modifier = Modifier.padding(top = PennyWiseSpacing.compact),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            } else {
                uiState.overallBudget?.let { summary ->
                    item {
                        BudgetSummaryCard(
                            title = "Monthly budget",
                            summary = summary,
                            onEdit = { onEditBudget(summary.budget.id) },
                            onDelete = null,
                        )
                    }
                }
                if (uiState.categoryBudgets.isNotEmpty()) {
                    item {
                        Text("Category budgets", style = MaterialTheme.typography.titleLarge)
                    }
                    items(uiState.categoryBudgets, key = { it.budget.id }) { summary ->
                        BudgetSummaryCard(
                            title = summary.budget.category ?: "Category",
                            summary = summary,
                            onEdit = { onEditBudget(summary.budget.id) },
                            onDelete = { pendingDelete = summary },
                        )
                    }
                }
            }
            item { Column(modifier = Modifier.padding(bottom = 80.dp)) {} }
        }
    }
}

@Composable
private fun BudgetSummaryCard(
    title: String,
    summary: BudgetSummary,
    onEdit: () -> Unit,
    onDelete: (() -> Unit)?,
) {
    PennyWiseCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(title, style = MaterialTheme.typography.titleLarge)
            Row {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Outlined.Edit, contentDescription = "Edit $title budget")
                }
                onDelete?.let {
                    IconButton(onClick = it) {
                        Icon(Icons.Outlined.DeleteOutline, contentDescription = "Delete $title budget")
                    }
                }
            }
        }
        BudgetProgress(
            label = "Used",
            spent = formatCurrency(summary.spent),
            limit = formatCurrency(summary.budget.amount),
            progress = summary.utilization,
            color = budgetStatusColor(summary),
            modifier = Modifier.padding(top = PennyWiseSpacing.standard),
        )
        Text(
            text = if (summary.remaining >= 0) {
                "${formatCurrency(summary.remaining)} remaining"
            } else {
                "${formatCurrency(-summary.remaining)} over budget"
            },
            modifier = Modifier.padding(top = PennyWiseSpacing.small),
            style = MaterialTheme.typography.bodyMedium,
            color = budgetStatusColor(summary),
        )
    }
}

private fun budgetStatusColor(summary: BudgetSummary) =
    when {
        summary.remaining < 0 -> androidx.compose.ui.graphics.Color(0xFFD64545)
        summary.utilization >= 0.8f -> androidx.compose.ui.graphics.Color(0xFFC88719)
        else -> androidx.compose.ui.graphics.Color(0xFF1F9D72)
    }

private fun formatCurrency(amount: Double): String =
    NumberFormat.getCurrencyInstance(Locale("en", "IN")).apply {
        maximumFractionDigits = 0
        minimumFractionDigits = 0
    }.format(amount)

@Preview(showBackground = true)
@Composable
private fun BudgetsScreenPreview() {
    PennyWiseTheme {
        BudgetsContent(
            uiState = BudgetUiState(),
            onAddBudget = {},
            onEditBudget = {},
            onDeleteBudget = {},
        )
    }
}
