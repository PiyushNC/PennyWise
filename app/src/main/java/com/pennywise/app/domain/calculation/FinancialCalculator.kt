package com.pennywise.app.domain.calculation

import com.pennywise.app.domain.model.Transaction
import com.pennywise.app.domain.model.TransactionType
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId

data class CategoryExpense(
    val category: String,
    val amount: Double,
)

data class MonthlyFinancialSummary(
    val month: YearMonth,
    val transactions: List<Transaction>,
    val totalIncome: Double,
    val totalExpenses: Double,
    val categoryExpenses: List<CategoryExpense>,
    val daysInScope: Int,
) {
    val averageDailyExpense: Double
        get() = totalExpenses / daysInScope.coerceAtLeast(1)

    val highestSpendingCategory: CategoryExpense?
        get() = categoryExpenses.firstOrNull()
}

object FinancialCalculator {
    fun summarize(
        month: YearMonth,
        transactions: List<Transaction>,
    ): MonthlyFinancialSummary {
        val monthTransactions = transactions.filter { it.toYearMonth() == month }
        val expenses = monthTransactions.filter { it.type == TransactionType.EXPENSE }
        val income = monthTransactions.filter { it.type == TransactionType.INCOME }
        val totalExpenses = expenses.sumOf { it.amount }
        val categoryExpenses = expenses
            .groupBy { it.category }
            .map { (category, values) -> CategoryExpense(category, values.sumOf { it.amount }) }
            .sortedByDescending { it.amount }

        val daysInScope = if (month == YearMonth.now()) {
            LocalDate.now().dayOfMonth
        } else {
            month.lengthOfMonth()
        }

        return MonthlyFinancialSummary(
            month = month,
            transactions = monthTransactions,
            totalIncome = income.sumOf { it.amount },
            totalExpenses = totalExpenses,
            categoryExpenses = categoryExpenses,
            daysInScope = daysInScope,
        )
    }

    fun budgetUtilization(spent: Double, budget: Double): Float =
        if (budget > 0.0) (spent / budget).toFloat().coerceAtLeast(0f) else 0f

    fun transactionDate(transaction: Transaction): LocalDate =
        Instant.ofEpochMilli(transaction.date).atZone(ZoneId.systemDefault()).toLocalDate()

    fun monthStart(month: YearMonth): Long =
        month.atDay(1)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

    fun monthFromStart(monthStart: Long): YearMonth =
        YearMonth.from(Instant.ofEpochMilli(monthStart).atZone(ZoneId.systemDefault()))

    private fun Transaction.toYearMonth(): YearMonth =
        YearMonth.from(Instant.ofEpochMilli(date).atZone(ZoneId.systemDefault()))
}
