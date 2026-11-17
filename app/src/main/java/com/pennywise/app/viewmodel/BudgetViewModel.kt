package com.pennywise.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.pennywise.app.data.repository.BudgetRepository
import com.pennywise.app.data.repository.TransactionRepository
import com.pennywise.app.domain.calculation.FinancialCalculator
import com.pennywise.app.domain.model.Budget
import com.pennywise.app.domain.model.TransactionType
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class BudgetSummary(
    val budget: Budget,
    val spent: Double,
) {
    val remaining: Double get() = budget.amount - spent
    val utilization: Float get() = FinancialCalculator.budgetUtilization(spent, budget.amount)
}

data class BudgetUiState(
    val monthStart: Long = currentMonthStart(),
    val monthLabel: String = currentMonthLabel(),
    val overallBudget: BudgetSummary? = null,
    val categoryBudgets: List<BudgetSummary> = emptyList(),
    val isLoading: Boolean = true,
) {
    val hasBudgets: Boolean get() = overallBudget != null || categoryBudgets.isNotEmpty()
}

class BudgetViewModel(
    private val budgetRepository: BudgetRepository,
    transactionRepository: TransactionRepository,
) : ViewModel() {
    private val monthStart = MutableStateFlow(currentMonthStart())

    val uiState: StateFlow<BudgetUiState> = combine(
        budgetRepository.observeForMonth(monthStart.value),
        transactionRepository.transactions,
    ) { budgets, transactions ->
        val summary = FinancialCalculator.summarize(
            month = FinancialCalculator.monthFromStart(monthStart.value),
            transactions = transactions,
        )

        val summaries = budgets.map { budget ->
            BudgetSummary(
                budget = budget,
                spent = if (budget.category == null) {
                    summary.totalExpenses
                } else {
                    summary.categoryExpenses
                        .firstOrNull { it.category == budget.category }
                        ?.amount
                        ?: 0.0
                },
            )
        }
        BudgetUiState(
            monthStart = monthStart.value,
            monthLabel = monthLabel(monthStart.value),
            overallBudget = summaries.firstOrNull { it.budget.category == null },
            categoryBudgets = summaries.filter { it.budget.category != null },
            isLoading = false,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), BudgetUiState())

    fun save(
        budget: Budget,
        onSaved: () -> Unit = {},
        onDuplicate: () -> Unit = {},
    ) {
        viewModelScope.launch {
            if (budgetRepository.existsForMonthAndCategory(budget.monthStart, budget.category, budget.id)) {
                onDuplicate()
            } else {
                budgetRepository.save(budget)
                onSaved()
            }
        }
    }

    fun delete(summary: BudgetSummary) {
        viewModelScope.launch { budgetRepository.delete(summary.budget) }
    }

    suspend fun getBudget(id: Long): Budget? = budgetRepository.getById(id)

    class Factory(
        private val budgetRepository: BudgetRepository,
        private val transactionRepository: TransactionRepository,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(BudgetViewModel::class.java)) {
                return BudgetViewModel(budgetRepository, transactionRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
        }
    }
}

fun currentMonthStart(): Long =
    LocalDate.now()
        .withDayOfMonth(1)
        .atStartOfDay(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli()

fun nextMonthStart(monthStart: Long): Long =
    java.time.Instant.ofEpochMilli(monthStart)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
        .plusMonths(1)
        .atStartOfDay(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli()

private fun monthLabel(monthStart: Long): String =
    DateTimeFormatter.ofPattern("MMMM yyyy")
        .format(java.time.Instant.ofEpochMilli(monthStart).atZone(ZoneId.systemDefault()))

private fun currentMonthLabel(): String = monthLabel(currentMonthStart())
