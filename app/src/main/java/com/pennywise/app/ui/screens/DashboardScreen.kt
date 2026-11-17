package com.pennywise.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pennywise.app.ui.components.AmountDisplay
import com.pennywise.app.ui.components.BudgetProgress
import com.pennywise.app.ui.components.PennyWiseCard
import com.pennywise.app.ui.components.PennyWisePrimaryButton
import com.pennywise.app.ui.components.TransactionListItem
import com.pennywise.app.ui.theme.PennyWiseSpacing
import com.pennywise.app.ui.theme.PennyWiseTheme
import com.pennywise.app.viewmodel.CategorySpending
import com.pennywise.app.viewmodel.DashboardUiState
import com.pennywise.app.viewmodel.DashboardViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun DashboardScreen(
    onAddTransaction: () -> Unit = {},
    viewModel: DashboardViewModel,
) {
    val uiState by viewModel.uiState.collectAsState()
    DashboardContent(
        uiState = uiState,
        onAddTransaction = onAddTransaction,
    )
}

@Composable
private fun DashboardContent(
    uiState: DashboardUiState,
    onAddTransaction: () -> Unit,
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddTransaction,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ) {
                Icon(Icons.Outlined.Add, contentDescription = "Add transaction")
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = PennyWiseSpacing.screen),
            verticalArrangement = Arrangement.spacedBy(PennyWiseSpacing.section),
        ) {
            item {
                DashboardHeader(
                    greeting = uiState.greeting,
                    date = uiState.currentDate,
                )
            }
            if (!uiState.hasFinancialData) {
                item {
                    EmptyDashboardState(onAddTransaction = onAddTransaction)
                }
            } else {
                item {
                    BalanceSummaryCard(uiState = uiState)
                }
                item {
                    SpendingProgressCard(uiState = uiState)
                }
                item {
                    IncomeExpenseCard(uiState = uiState)
                }
                item {
                    CategorySpendingCard(categories = uiState.categorySpending)
                }
                item {
                    Text(
                        text = "Recent transactions",
                        style = MaterialTheme.typography.titleLarge,
                    )
                }
                if (uiState.recentTransactions.isEmpty()) {
                    item {
                        Text(
                            text = "Your latest five transactions will appear here.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                } else {
                    item {
                        PennyWiseCard(modifier = Modifier.fillMaxWidth()) {
                            uiState.recentTransactions.take(5).forEachIndexed { index, transaction ->
                                TransactionListItem(
                                    title = transaction.title,
                                    category = transaction.category,
                                    amount = signedCurrency(transaction.amount, transaction.isIncome),
                                    date = transaction.date,
                                    isIncome = transaction.isIncome,
                                )
                                if (index < uiState.recentTransactions.take(5).lastIndex) {
                                    HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                                }
                            }
                        }
                    }
                }
                item { Spacer(modifier = Modifier.height(72.dp)) }
            }
        }
    }
}

@Composable
private fun DashboardHeader(
    greeting: String,
    date: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = PennyWiseSpacing.large),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(PennyWiseSpacing.compact)) {
            Text(
                text = greeting,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text("Your financial overview", style = MaterialTheme.typography.headlineSmall)
            Text(
                text = date,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun BalanceSummaryCard(uiState: DashboardUiState) {
    PennyWiseCard(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
        ) {
            AmountDisplay(
                amount = uiState.monthlyBudget?.let(::formatCurrency) ?: "—",
                label = "Total monthly budget",
                prominent = true,
            )
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Outlined.AccountBalanceWallet,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = PennyWiseSpacing.large),
            horizontalArrangement = Arrangement.spacedBy(PennyWiseSpacing.standard),
        ) {
            SummaryMetric(
                label = "Expenses",
                value = formatCurrency(uiState.monthlyExpenses),
                modifier = Modifier.weight(1f),
            )
            SummaryMetric(
                label = "Remaining",
                value = uiState.remainingBudget?.let(::formatCurrency) ?: "—",
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun SummaryMetric(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun SpendingProgressCard(uiState: DashboardUiState) {
    PennyWiseCard(modifier = Modifier.fillMaxWidth()) {
        BudgetProgress(
            label = "Monthly spending",
            spent = formatCurrency(uiState.monthlyExpenses),
            limit = uiState.monthlyBudget?.let(::formatCurrency) ?: "—",
            progress = uiState.spendingProgress,
        )
        Text(
            text = "${(uiState.spendingProgress * 100).toInt()}% of your monthly budget used",
            modifier = Modifier.padding(top = PennyWiseSpacing.standard),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun IncomeExpenseCard(uiState: DashboardUiState) {
    PennyWiseCard(modifier = Modifier.fillMaxWidth()) {
        Text("Income vs expenses", style = MaterialTheme.typography.titleLarge)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = PennyWiseSpacing.large),
            horizontalArrangement = Arrangement.spacedBy(PennyWiseSpacing.standard),
        ) {
            IncomeExpenseMetric(
                label = "Income",
                value = formatCurrency(uiState.monthlyIncome),
                positive = true,
                modifier = Modifier.weight(1f),
            )
            IncomeExpenseMetric(
                label = "Expenses",
                value = formatCurrency(uiState.monthlyExpenses),
                positive = false,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun IncomeExpenseMetric(
    label: String,
    value: String,
    positive: Boolean,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = if (positive) Icons.Outlined.ArrowUpward else Icons.Outlined.ArrowDownward,
            contentDescription = null,
            tint = if (positive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(
                    if (positive) MaterialTheme.colorScheme.primaryContainer
                    else MaterialTheme.colorScheme.errorContainer,
                )
                .padding(7.dp),
        )
        Column(modifier = Modifier.padding(start = PennyWiseSpacing.small)) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun CategorySpendingCard(categories: List<CategorySpending>) {
    PennyWiseCard(modifier = Modifier.fillMaxWidth()) {
        Text("Where your money goes", style = MaterialTheme.typography.titleLarge)
        if (categories.isEmpty()) {
            Text(
                "Category insights will appear after you add expenses.",
                modifier = Modifier.padding(top = PennyWiseSpacing.compact),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        } else {
            categories.take(4).forEach { category ->
                CategorySpendingRow(category = category)
            }
        }
    }
}

@Composable
private fun CategorySpendingRow(category: CategorySpending) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = PennyWiseSpacing.standard),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(Color(category.colorHex)),
        )
        Text(
            text = category.name,
            modifier = Modifier
                .weight(1f)
                .padding(start = PennyWiseSpacing.small),
            style = MaterialTheme.typography.bodyLarge,
        )
        Text(
            text = formatCurrency(category.amount),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun EmptyDashboardState(onAddTransaction: () -> Unit) {
    PennyWiseCard(modifier = Modifier.fillMaxWidth()) {
        Text("Your dashboard is ready", style = MaterialTheme.typography.titleLarge)
        Text(
            "Add your first transaction or set a monthly budget to see your financial picture here.",
            modifier = Modifier.padding(top = PennyWiseSpacing.compact),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        PennyWisePrimaryButton(
            text = "Add transaction",
            onClick = onAddTransaction,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = PennyWiseSpacing.large),
        )
    }
}

private fun formatCurrency(amount: Double): String =
    NumberFormat.getCurrencyInstance(Locale("en", "IN")).apply {
        maximumFractionDigits = 0
        minimumFractionDigits = 0
    }.format(amount)

private fun signedCurrency(amount: Double, isIncome: Boolean): String =
    "${if (isIncome) "+" else "−"}${formatCurrency(amount)}"

@Preview(showBackground = true)
@Composable
private fun DashboardScreenPreview() {
    PennyWiseTheme {
        DashboardContent(
            uiState = DashboardViewModel.previewState,
            onAddTransaction = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EmptyDashboardScreenPreview() {
    PennyWiseTheme {
        DashboardContent(
            uiState = DashboardUiState(),
            onAddTransaction = {},
        )
    }
}
