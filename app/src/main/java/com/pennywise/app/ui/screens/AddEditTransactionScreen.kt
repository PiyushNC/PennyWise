package com.pennywise.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pennywise.app.domain.model.Transaction
import com.pennywise.app.domain.model.TransactionType
import com.pennywise.app.ui.components.PennyWiseCard
import com.pennywise.app.ui.components.PennyWiseOutlinedButton
import com.pennywise.app.ui.components.PennyWisePrimaryButton
import com.pennywise.app.ui.theme.PennyWiseSpacing
import com.pennywise.app.ui.theme.PennyWiseTheme
import com.pennywise.app.viewmodel.TransactionsViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val categories = listOf("Food", "Home", "Shopping", "Transport", "Health", "Salary", "Other")
private val paymentMethods = listOf("Cash", "UPI", "Card", "Bank transfer", "Other")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTransactionScreen(
    onBack: () -> Unit,
    onSaved: () -> Unit,
    viewModel: TransactionsViewModel,
    transactionId: Long? = null,
) {
    var existing by remember { mutableStateOf<Transaction?>(null) }
    LaunchedEffect(transactionId) {
        existing = transactionId?.let { viewModel.getTransaction(it) }
    }
    AddEditTransactionContent(
        existing = existing,
        onBack = onBack,
        onSave = { viewModel.save(it); onSaved() },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddEditTransactionContent(
    existing: Transaction?,
    onBack: () -> Unit,
    onSave: (Transaction) -> Unit,
) {
    var title by remember(existing) { mutableStateOf(existing?.title.orEmpty()) }
    var amount by remember(existing) { mutableStateOf(existing?.amount?.toString().orEmpty()) }
    var type by remember(existing) { mutableStateOf(existing?.type ?: TransactionType.EXPENSE) }
    var category by remember(existing) { mutableStateOf(existing?.category.orEmpty()) }
    var paymentMethod by remember(existing) { mutableStateOf(existing?.paymentMethod.orEmpty()) }
    var note by remember(existing) { mutableStateOf(existing?.note.orEmpty()) }
    var date by remember(existing) { mutableStateOf(existing?.date ?: System.currentTimeMillis()) }
    var showDatePicker by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = date)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    date = datePickerState.selectedDateMillis ?: date
                    showDatePicker = false
                }) { Text("Done") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .imePadding()
            .padding(PennyWiseSpacing.screen),
        verticalArrangement = Arrangement.spacedBy(PennyWiseSpacing.standard),
    ) {
        Text(
            text = if (existing == null) "Add transaction" else "Edit transaction",
            style = MaterialTheme.typography.headlineSmall,
        )
        OutlinedTextField(
            value = title,
            onValueChange = { title = it; errorMessage = null },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Title") },
            singleLine = true,
            isError = errorMessage?.contains("title", ignoreCase = true) == true,
        )
        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it; errorMessage = null },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Amount (₹)") },
            singleLine = true,
            isError = errorMessage?.contains("amount", ignoreCase = true) == true,
        )
        Text("Type", style = MaterialTheme.typography.titleMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(PennyWiseSpacing.small)) {
            TransactionType.entries.forEach { option ->
                FilterChip(
                    selected = type == option,
                    onClick = { type = option },
                    label = { Text(option.name.lowercase().replaceFirstChar(Char::uppercase)) },
                )
            }
        }
        Text("Category", style = MaterialTheme.typography.titleMedium)
        SelectionChips(categories, category) { category = it }
        Text("Payment method", style = MaterialTheme.typography.titleMedium)
        SelectionChips(paymentMethods, paymentMethod) { paymentMethod = it }
        PennyWiseOutlinedButton(
            text = "Date: ${formatDate(date)}",
            onClick = { showDatePicker = true },
            modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = note,
            onValueChange = { note = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Note (optional)") },
            minLines = 3,
        )
        errorMessage?.let {
            Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(PennyWiseSpacing.small),
        ) {
            TextButton(onClick = onBack, modifier = Modifier.weight(1f)) { Text("Cancel") }
            PennyWisePrimaryButton(
                text = "Save transaction",
                onClick = {
                    val parsedAmount = amount.toDoubleOrNull()
                    errorMessage = when {
                        title.isBlank() -> "Title cannot be empty."
                        parsedAmount == null || parsedAmount <= 0 -> "Amount must be greater than zero."
                        category.isBlank() -> "Please select a category."
                        paymentMethod.isBlank() -> "Please select a payment method."
                        else -> null
                    }
                    if (errorMessage == null) {
                        onSave(
                            Transaction(
                                id = existing?.id ?: 0L,
                                title = title.trim(),
                                amount = parsedAmount!!,
                                type = type,
                                category = category,
                                note = note.trim(),
                                date = date,
                                paymentMethod = paymentMethod,
                            ),
                        )
                    }
                },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun SelectionChips(
    options: List<String>,
    selected: String,
    onSelected: (String) -> Unit,
) {
    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(PennyWiseSpacing.small),
    ) {
        options.forEach { option ->
            FilterChip(
                selected = selected == option,
                onClick = { onSelected(option) },
                label = { Text(option) },
            )
        }
    }
}

private fun formatDate(millis: Long): String =
    SimpleDateFormat("d MMM yyyy", Locale.getDefault()).format(Date(millis))

@Preview(showBackground = true)
@Composable
private fun AddTransactionPreview() {
    PennyWiseTheme {
        AddEditTransactionContent(
            existing = null,
            onBack = {},
            onSave = {},
        )
    }
}
