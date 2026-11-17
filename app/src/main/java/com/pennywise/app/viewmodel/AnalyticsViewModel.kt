package com.pennywise.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.pennywise.app.data.repository.TransactionRepository
import com.pennywise.app.domain.model.Transaction
import com.pennywise.app.domain.model.TransactionType
import com.pennywise.app.domain.calculation.FinancialCalculator
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class AnalyticsCategory(
    val name: String,
    val amount: Double,
    val percentage: Float,
    val color: Long,
)

data class AnalyticsPoint(
    val label: String,
    val amount: Double,
)

data class AnalyticsUiState(
    val selectedMonth: YearMonth = YearMonth.now(),
    val totalIncome: Double = 0.0,
    val totalExpenses: Double = 0.0,
    val categoryDistribution: List<AnalyticsCategory> = emptyList(),
    val weeklySpending: List<AnalyticsPoint> = emptyList(),
    val monthlySpending: List<AnalyticsPoint> = emptyList(),
    val highestSpendingCategory: AnalyticsCategory? = null,
    val averageDailySpending: Double = 0.0,
    val hasData: Boolean = false,
)

class AnalyticsViewModel(
    transactionRepository: TransactionRepository,
) : ViewModel() {
    private val selectedMonth = MutableStateFlow(YearMonth.now())

    val uiState: StateFlow<AnalyticsUiState> = combine(
        selectedMonth,
        transactionRepository.transactions,
    ) { month, transactions ->
        buildAnalyticsState(month, transactions)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        AnalyticsUiState(),
    )

    fun previousMonth() {
        selectedMonth.value = selectedMonth.value.minusMonths(1)
    }

    fun nextMonth() {
        selectedMonth.value = selectedMonth.value.plusMonths(1)
    }

    class Factory(
        private val transactionRepository: TransactionRepository,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AnalyticsViewModel::class.java)) {
                return AnalyticsViewModel(transactionRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
        }
    }
}

private fun buildAnalyticsState(
    month: YearMonth,
    transactions: List<Transaction>,
): AnalyticsUiState {
    val summary = FinancialCalculator.summarize(month, transactions)
    val totalExpenses = summary.totalExpenses
    val categoryDistribution = summary.categoryExpenses
        .map { category ->
            AnalyticsCategory(
                name = category.category,
                amount = category.amount,
                percentage = if (totalExpenses == 0.0) 0f else (category.amount / totalExpenses).toFloat(),
                color = analyticsCategoryColor(category.category),
            )
        }

    return AnalyticsUiState(
        selectedMonth = month,
        totalIncome = summary.totalIncome,
        totalExpenses = totalExpenses,
        categoryDistribution = categoryDistribution,
        weeklySpending = weeklyPoints(month, summary.transactions.filter { it.type == TransactionType.EXPENSE }),
        monthlySpending = monthlyPoints(month, transactions),
        highestSpendingCategory = categoryDistribution.firstOrNull(),
        averageDailySpending = summary.averageDailyExpense,
        hasData = summary.transactions.isNotEmpty(),
    )
}

private fun weeklyPoints(month: YearMonth, expenses: List<Transaction>): List<AnalyticsPoint> {
    val weeks = mutableMapOf<Int, Double>()
    (1..5).forEach { week -> weeks[week] = 0.0 }
    expenses.forEach { transaction ->
        val week = ((FinancialCalculator.transactionDate(transaction).dayOfMonth - 1) / 7 + 1)
            .coerceAtMost(5)
        weeks[week] = weeks.getValue(week) + transaction.amount
    }
    return weeks.map { (week, amount) -> AnalyticsPoint("W$week", amount) }
}

private fun monthlyPoints(
    selectedMonth: YearMonth,
    transactions: List<Transaction>,
): List<AnalyticsPoint> {
    return (5L downTo 0L).map { offset ->
        val month = selectedMonth.minusMonths(offset)
        val amount = FinancialCalculator.summarize(month, transactions).totalExpenses
        AnalyticsPoint(month.format(DateTimeFormatter.ofPattern("MMM", Locale.getDefault())), amount)
    }
}

private fun analyticsCategoryColor(category: String): Long = when (category.lowercase()) {
    "food" -> 0xFFE59B54
    "shopping" -> 0xFF8A78D7
    "transport" -> 0xFF4D9BD1
    "home", "housing", "rent" -> 0xFF1F9D72
    "health" -> 0xFFD66C8D
    else -> 0xFF627181
}
