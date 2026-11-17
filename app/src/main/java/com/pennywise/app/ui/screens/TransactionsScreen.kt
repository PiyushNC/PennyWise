package com.pennywise.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pennywise.app.domain.model.Transaction
import com.pennywise.app.domain.model.TransactionType
import com.pennywise.app.ui.anime.AnimeEmptyStateIllustration
import com.pennywise.app.ui.components.PennyWiseCard
import com.pennywise.app.ui.components.TransactionListItem
import com.pennywise.app.ui.theme.AnimeAtmosphericBackground
import com.pennywise.app.ui.theme.AnimeCoral
import com.pennywise.app.ui.theme.ElectricViolet
import com.pennywise.app.ui.theme.PennyWiseSpacing
import com.pennywise.app.ui.theme.SakuraPink
import com.pennywise.app.viewmodel.TransactionFilter
import com.pennywise.app.viewmodel.TransactionSort
import com.pennywise.app.viewmodel.TransactionsUiState
import com.pennywise.app.viewmodel.TransactionsViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
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
            title = { Text("Delete transaction?", fontWeight = FontWeight.Bold) },
            text = { Text("This transaction will be permanently removed from your log.") },
            confirmButton = {
                TextButton(onClick = {
                    onDelete(transaction)
                    pendingDelete = null
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Transaction deleted")
                    }
                }) { Text("Delete", color = AnimeCoral, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { pendingDelete = null }) { Text("Cancel") }
            },
            containerColor = MaterialTheme.colorScheme.surface,
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddTransaction,
                containerColor = SakuraPink,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.border(2.dp, Brush.horizontalGradient(listOf(SakuraPink, ElectricViolet)), CircleShape),
            ) {
                Icon(Icons.Outlined.Add, contentDescription = "Add transaction", modifier = Modifier.size(28.dp))
            }
        },
        containerColor = Color.Transparent,
    ) { paddingValues ->
        AnimeAtmosphericBackground(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = PennyWiseSpacing.screen),
                verticalArrangement = Arrangement.spacedBy(PennyWiseSpacing.standard),
            ) {
                item {
                    Column(
                        modifier = Modifier.padding(top = PennyWiseSpacing.large),
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                    ) {
                        Text(
                            text = "LOG BOOK",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = SakuraPink,
                            letterSpacing = 1.2.sp,
                        )
                        Text(
                            text = "Transactions Log",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.ExtraBold,
                        )
                        Text(
                            text = "Review and manage your anime financial activity.",
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
                        leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null, tint = SakuraPink) },
                        placeholder = { Text("Search transactions...") },
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SakuraPink,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                        ),
                    )
                }

                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(PennyWiseSpacing.small)) {
                        TransactionFilter.entries.forEach { filter ->
                            val isSelected = uiState.filter == filter
                            FilterChip(
                                selected = isSelected,
                                onClick = { onFilterChange(filter) },
                                label = { Text(filter.name.lowercase().replaceFirstChar(Char::uppercase)) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = SakuraPink.copy(alpha = 0.25f),
                                    selectedLabelColor = SakuraPink,
                                ),
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
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = ElectricViolet.copy(alpha = 0.25f),
                                    selectedLabelColor = ElectricViolet,
                                ),
                            )
                            DropdownMenu(
                                expanded = categoryMenuExpanded,
                                onDismissRequest = { categoryMenuExpanded = false },
                            ) {
                                DropdownMenuItem(
                                    text = { Text("All Categories") },
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
                            AnimeEmptyStateIllustration(
                                title = if (uiState.query.isBlank() && uiState.category == null && uiState.filter == TransactionFilter.ALL) {
                                    "No Transactions Logged Yet!"
                                } else {
                                    "No Matching Transactions"
                                },
                                subtitle = "Tap the glowing '+' button to log your income or expenses.",
                            )
                        }
                    }
                } else {
                    item {
                        PennyWiseCard(modifier = Modifier.fillMaxWidth()) {
                            uiState.transactions.forEachIndexed { index, transaction ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth(),
                                ) {
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
                                        Icon(
                                            imageVector = Icons.Outlined.DeleteOutline,
                                            contentDescription = "Delete transaction",
                                            tint = AnimeCoral.copy(alpha = 0.8f),
                                        )
                                    }
                                }
                                if (index < uiState.transactions.lastIndex) {
                                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
                                }
                            }
                        }
                    }
                    item { Column(modifier = Modifier.padding(bottom = 80.dp)) {} }
                }
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
