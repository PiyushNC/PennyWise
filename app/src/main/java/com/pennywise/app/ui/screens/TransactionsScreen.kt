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
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import com.pennywise.app.PennyWiseApplication
import com.pennywise.app.domain.model.Transaction
import com.pennywise.app.domain.model.TransactionType
import com.pennywise.app.ui.components.PennyWiseCard
import com.pennywise.app.ui.components.TransactionListItem
import com.pennywise.app.ui.theme.PennyWiseSpacing
import com.pennywise.app.ui.theme.PennyWiseTheme
import com.pennywise.app.viewmodel.TransactionFilter
import com.pennywise.app.viewmodel.TransactionSort
import com.pennywise.app.viewmodel.TransactionsUiState
import com.pennywise.app.viewmodel.TransactionsViewModel
import java.text.NumberFormat
import java.util.Locale
import java.text.SimpleDateFormat
import java.util.Date
import kotlinx.coroutines.launch

@Composable
fun TransactionsScreen(
    onAddTransaction: () -> Unit,
    onEditTransaction: (Long) -> Unit,
    viewModel: TransactionsViewModel,
) {
    val uiState by viewModel.uiState.collectAsState()
    TransactionsContent(
        uiState = uiState,
        onQueryChange = viewModel::setQuery,
        onFilterChange = viewModel::setFilter,
        onCategoryChange = viewModel::setCategory,
        onSortChange = viewModel::setSort,
        onDelete = viewModel::delete,
        onEdit = { onEditTransaction(it.id) },
        onAddTransaction = onAddTransaction,
    )
}

@Composable
private fun TransactionsContent(
    uiState: TransactionsUiState,
    onQueryChange: (String) -> Unit,
    onFilterChange: (TransactionFilter) -> Unit,
    onCategoryChange: (String?) -> Unit,
    onSortChange: (TransactionSort) -> Unit,
    onDelete: (Transaction) -> Unit,
    onEdit: (Transaction) -> Unit,
    onAddTransaction: () -> Unit,
) {
    var categoryMenuExpanded by remember { mutableStateOf(false) }
    var sortMenuExpanded by remember { mutableStateOf(false) }
    var pendingDelete by remember { mutableStateOf<Transaction?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    pendingDelete?.let { transaction ->
        AlertDialog(
            onDismissRequest = { pendingDelete = null },
            title = { Text("Delete transaction?") },
            text = { Text("This transaction will be permanently removed.") },
            confirmButton = {
                TextButton(onClick = {
                    onDelete(transaction)
                    pendingDelete = null
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Transaction deleted")
                    }
                }) { Text("Delete", color = MaterialTheme.colorScheme.error) }
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
            verticalArrangement = Arrangement.spacedBy(PennyWiseSpacing.standard),
        ) {
            item {
                Column(
                    modifier = Modifier.padding(top = PennyWiseSpacing.large),
                    verticalArrangement = Arrangement.spacedBy(PennyWiseSpacing.compact),
                ) {
                    Text("Transactions", style = MaterialTheme.typography.headlineSmall)
                    Text(
                        "Review and manage your financial activity.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            item {
                OutlinedTextField(
                    value = uiState.query,
                    onValueChange = onQueryChange,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
                    placeholder = { Text("Search by title") },
                    shape = MaterialTheme.shapes.small,
                )
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(PennyWiseSpacing.small)) {
                    TransactionFilter.entries.forEach { filter ->
                        FilterChip(
                            selected = uiState.filter == filter,
                            onClick = { onFilterChange(filter) },
                            label = { Text(filter.name.lowercase().replaceFirstChar(Char::uppercase)) },
                        )
                    }
                }
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(PennyWiseSpacing.small)) {
                    Column {
                        FilterChip(
                            selected = uiState.category != null,
                            onClick = { categoryMenuExpanded = true },
                            label = { Text(uiState.category ?: "Category") },
                        )
                        DropdownMenu(
                            expanded = categoryMenuExpanded,
                            onDismissRequest = { categoryMenuExpanded = false },
                        ) {
                            DropdownMenuItem(
                                text = { Text("All categories") },
                                onClick = {
                                    onCategoryChange(null)
                                    categoryMenuExpanded = false
                                },
                            )
                            uiState.categories.forEach { category ->
                                DropdownMenuItem(
                                    text = { Text(category) },
                                    onClick = {
                                        onCategoryChange(category)
                                        categoryMenuExpanded = false
                                    },
                                )
                            }
                        }
                    }
                    Column {
                        FilterChip(
                            selected = true,
                            onClick = { sortMenuExpanded = true },
                            label = { Text("Sort: ${uiState.sort.name.lowercase().replaceFirstChar(Char::uppercase)}") },
                        )
                        DropdownMenu(
                            expanded = sortMenuExpanded,
                            onDismissRequest = { sortMenuExpanded = false },
                        ) {
                            DropdownMenuItem(
                                text = { Text("Date") },
                                onClick = {
                                    onSortChange(TransactionSort.DATE)
                                    sortMenuExpanded = false
                                },
                            )
                            DropdownMenuItem(
                                text = { Text("Amount") },
                                onClick = {
                                    onSortChange(TransactionSort.AMOUNT)
                                    sortMenuExpanded = false
                                },
                            )
                        }
                    }
                }
            }
            if (uiState.transactions.isEmpty()) {
                item {
                    PennyWiseCard(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            if (uiState.query.isBlank() && uiState.category == null && uiState.filter == TransactionFilter.ALL) {
                                "No transactions yet"
                            } else {
                                "No matching transactions"
                            },
                            style = MaterialTheme.typography.titleMedium,
                        )
                        Text(
                            "Add an income or expense to start building your history.",
                            modifier = Modifier.padding(top = PennyWiseSpacing.compact),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            } else {
                item {
                    PennyWiseCard(modifier = Modifier.fillMaxWidth()) {
                        uiState.transactions.forEachIndexed { index, transaction ->
                            Row {
                                TransactionListItem(
                                    title = transaction.title,
                                    category = transaction.category,
                                    amount = signedCurrency(transaction),
                                    date = formatDate(transaction.date),
                                    isIncome = transaction.type == TransactionType.INCOME,
                                    onClick = { onEdit(transaction) },
                                    modifier = Modifier.weight(1f),
                                )
                                IconButton(onClick = { pendingDelete = transaction }) {
                                    Icon(Icons.Outlined.DeleteOutline, contentDescription = "Delete transaction")
                                }
                            }
                        }
                    }
                }
                item { Column(modifier = Modifier.padding(bottom = 80.dp)) {} }
            }
        }
    }
}

private fun formatDate(millis: Long): String =
    SimpleDateFormat("d MMM yyyy", Locale.getDefault()).format(Date(millis))

private fun signedCurrency(transaction: Transaction): String {
    val format = NumberFormat.getCurrencyInstance(Locale("en", "IN")).apply {
        maximumFractionDigits = 0
        minimumFractionDigits = 0
    }
    return "${if (transaction.type == TransactionType.INCOME) "+" else "−"}${format.format(transaction.amount)}"
}

@Preview(showBackground = true)
@Composable
private fun TransactionsScreenPreview() {
    PennyWiseTheme {
        TransactionsContent(
            uiState = TransactionsUiState(),
            onQueryChange = {},
            onFilterChange = {},
            onCategoryChange = {},
            onSortChange = {},
            onDelete = {},
            onEdit = {},
            onAddTransaction = {},
        )
    }
}
