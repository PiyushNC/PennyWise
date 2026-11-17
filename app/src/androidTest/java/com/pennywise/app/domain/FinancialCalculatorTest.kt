package com.pennywise.app.domain

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pennywise.app.domain.calculation.FinancialCalculator
import com.pennywise.app.domain.model.Transaction
import com.pennywise.app.domain.model.TransactionType
import java.time.YearMonth
import java.time.ZoneId
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FinancialCalculatorTest {
    private val month = YearMonth.of(2026, 11)
    private val monthStart = month.atDay(1)
        .atStartOfDay(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli()

    @Test
    fun summarizeExcludesIncomeAndOutOfMonthTransactions() {
        val summary = FinancialCalculator.summarize(
            month = month,
            transactions = listOf(
                transaction("Salary", 50_000.0, TransactionType.INCOME, "Salary", monthStart + 1_000),
                transaction("Food", 500.0, TransactionType.EXPENSE, "Food", monthStart + 2_000),
                transaction("Transport", 300.0, TransactionType.EXPENSE, "Transport", monthStart + 3_000),
                transaction("Old food", 900.0, TransactionType.EXPENSE, "Food", monthStart - 1),
            ),
        )

        assertEquals(50_000.0, summary.totalIncome, 0.0)
        assertEquals(800.0, summary.totalExpenses, 0.0)
        assertEquals(500.0, summary.categoryExpenses.first { it.category == "Food" }.amount, 0.0)
        assertEquals("Food", summary.highestSpendingCategory?.category)
    }

    @Test
    fun budgetUtilizationAvoidsDivisionByZero() {
        assertEquals(0f, FinancialCalculator.budgetUtilization(100.0, 0.0))
        assertEquals(0.5f, FinancialCalculator.budgetUtilization(500.0, 1_000.0))
    }

    @Test
    fun emptyMonthHasNoHighestCategoryAndZeroAverage() {
        val summary = FinancialCalculator.summarize(month, emptyList())

        assertEquals(0.0, summary.totalIncome, 0.0)
        assertEquals(0.0, summary.totalExpenses, 0.0)
        assertEquals(0.0, summary.averageDailyExpense, 0.0)
        assertNull(summary.highestSpendingCategory)
    }

    private fun transaction(
        title: String,
        amount: Double,
        type: TransactionType,
        category: String,
        date: Long,
    ) = Transaction(
        id = 0,
        title = title,
        amount = amount,
        type = type,
        category = category,
        note = "",
        date = date,
        paymentMethod = "UPI",
    )
}
