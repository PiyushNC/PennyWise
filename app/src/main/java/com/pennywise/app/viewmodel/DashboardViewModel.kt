package com.pennywise.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.pennywise.app.data.repository.BudgetRepository
import com.pennywise.app.data.repository.TransactionRepository
import com.pennywise.app.domain.calculation.FinancialCalculator
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

data class DashboardTransaction(
    val title: String,
    val category: String,
    val amount: Double,
    val date: String,
    val isIncome: Boolean,
)

data class CategorySpending(
    val name: String,
    val amount: Double,
    val colorHex: Long,
)

data class DashboardUiState(
    val greeting: String = greetingForCurrentTime(),
    val currentDate: String = currentDateLabel(),
    val monthlyBudget: Double? = null,
    val monthlyExpenses: Double = 0.0,
    val monthlyIncome: Double = 0.0,
    val categorySpending: List<CategorySpending> = emptyList(),
    val recentTransactions: List<DashboardTransaction> = emptyList(),
) {
    val remainingBudget: Double?
        get() = monthlyBudget?.minus(monthlyExpenses)

    val spendingProgress: Float
        get() = monthlyBudget
            ?.takeIf { it > 0.0 }
            ?.let { (monthlyExpenses / it).toFloat() }
            ?.coerceAtLeast(0f)
            ?: 0f

    val hasFinancialData: Boolean
        get() = monthlyBudget != null ||
            monthlyExpenses > 0 ||
            monthlyIncome > 0 ||
            categorySpending.isNotEmpty() ||
            recentTransactions.isNotEmpty()
}

class DashboardViewModel(
    budgetRepository: BudgetRepository,
    transactionRepository: TransactionRepository,
) : ViewModel() {
    private val monthStart = com.pennywise.app.viewmodel.currentMonthStart()
    val uiState: StateFlow<DashboardUiState> = combine(
        budgetRepository.observeForMonth(monthStart),
        transactionRepository.transactions,
    ) { budgets, transactions ->
        val summary = FinancialCalculator.summarize(
            java.time.YearMonth.now(),
            transactions,
        )
        DashboardUiState(
            monthlyBudget = budgets.firstOrNull { it.category == null }?.amount,
            monthlyExpenses = summary.totalExpenses,
            monthlyIncome = summary.totalIncome,
            categorySpending = summary.categoryExpenses
                .map { category ->
                    CategorySpending(category.category, category.amount, categoryColor(category.category))
                }
                .take(4),
            recentTransactions = summary.transactions
                .sortedByDescending { it.date }
                .take(5)
                .map {
                    DashboardTransaction(
                        title = it.title,
                        category = it.category,
                        amount = it.amount,
                        date = java.text.SimpleDateFormat("d MMM", Locale.getDefault())
                            .format(java.util.Date(it.date)),
                        isIncome = it.type.name == "INCOME",
                    )
                },
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DashboardUiState())

    companion object {
        val previewState = DashboardUiState(
            greeting = "Good evening",
            currentDate = "Monday, 17 November 2026",
            monthlyBudget = 85_000.0,
            monthlyExpenses = 42_680.0,
            monthlyIncome = 96_500.0,
            categorySpending = listOf(
                CategorySpending("Housing", 18_000.0, 0xFF1F9D72),
                CategorySpending("Food", 8_420.0, 0xFFE59B54),
                CategorySpending("Shopping", 6_880.0, 0xFF8A78D7),
                CategorySpending("Transport", 4_260.0, 0xFF4D9BD1),
            ),
            recentTransactions = listOf(
                DashboardTransaction("Salary credit", "Salary", 96_500.0, "Today", true),
                DashboardTransaction("Apartment rent", "Home", 18_000.0, "Yesterday", false),
                DashboardTransaction("Grocery shopping", "Shopping", 2_450.0, "15 Nov", false),
                DashboardTransaction("Coffee & lunch", "Food", 680.0, "14 Nov", false),
                DashboardTransaction("Metro recharge", "Transport", 1_200.0, "13 Nov", false),
            ),
        )
    }
}

class DashboardViewModelFactory(
    private val budgetRepository: BudgetRepository,
    private val transactionRepository: TransactionRepository,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
            return DashboardViewModel(budgetRepository, transactionRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
    }
}

private fun categoryColor(category: String): Long = when (category.lowercase()) {
    "food" -> 0xFFE59B54
    "shopping" -> 0xFF8A78D7
    "transport" -> 0xFF4D9BD1
    "home", "housing", "rent" -> 0xFF1F9D72
    else -> 0xFF627181
}

private fun currentDateLabel(): String =
    LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy", Locale.getDefault()))

private fun greetingForCurrentTime(): String =
    when (java.time.LocalTime.now().hour) {
        in 5..11 -> "Good morning"
        in 12..17 -> "Good afternoon"
        else -> "Good evening"
    }
