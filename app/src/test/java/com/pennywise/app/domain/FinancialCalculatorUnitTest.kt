package com.pennywise.app.domain

import com.pennywise.app.domain.calculation.FinancialCalculator
import com.pennywise.app.domain.model.Transaction
import com.pennywise.app.domain.model.TransactionType
import java.time.YearMonth
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class FinancialCalculatorUnitTest {
    private val month = YearMonth.of(2025, 11)

    @Test
    fun summarizeUsesSelectedMonthAndExcludesIncomeFromExpenses() {
        val monthStart = FinancialCalculator.monthStart(month)
        val summary = FinancialCalculator.summarize(
            month = month,
            transactions = listOf(
                transaction("Salary", 50_000.0, TransactionType.INCOME, "Salary", monthStart),
                transaction("Food", 500.0, TransactionType.EXPENSE, "Food", monthStart + 86_400_000),
                transaction("Old food", 900.0, TransactionType.EXPENSE, "Food", monthStart - 1),
            ),
        )

        assertEquals(50_000.0, summary.totalIncome, 0.0)
        assertEquals(500.0, summary.totalExpenses, 0.0)
        assertEquals(500.0, summary.categoryExpenses.single().amount, 0.0)
    }

    @Test
    fun summarizeAggregatesCategoriesAndCalculatesAverageDailyExpense() {
        val monthStart = FinancialCalculator.monthStart(month)
        val summary = FinancialCalculator.summarize(
            month = month,
            transactions = listOf(
                transaction("Groceries", 600.0, TransactionType.EXPENSE, "Food", monthStart),
                transaction("Lunch", 400.0, TransactionType.EXPENSE, "Food", monthStart + 1),
                transaction("Metro", 300.0, TransactionType.EXPENSE, "Transport", monthStart + 2),
            ),
        )

        assertEquals(1_000.0, summary.categoryExpenses.first { it.category == "Food" }.amount, 0.0)
        assertEquals("Food", summary.highestSpendingCategory?.category)
        assertEquals(1_300.0 / month.lengthOfMonth(), summary.averageDailyExpense, 0.0001)
    }

    @Test
    fun emptyMonthHasSafeZeroValues() {
        val summary = FinancialCalculator.summarize(month, emptyList())

        assertEquals(0.0, summary.totalIncome, 0.0)
        assertEquals(0.0, summary.totalExpenses, 0.0)
        assertEquals(0.0, summary.averageDailyExpense, 0.0)
        assertNull(summary.highestSpendingCategory)
        assertEquals(0f, FinancialCalculator.budgetUtilization(100.0, 0.0))
    }

    private fun transaction(
        title: String,
        amount: Double,
        type: TransactionType,
        category: String,
        date: Long,
    ) = Transaction(
        id = 0L,
        title = title,
        amount = amount,
        type = type,
        category = category,
        note = "",
        date = date,
        paymentMethod = "UPI",
    )
}
