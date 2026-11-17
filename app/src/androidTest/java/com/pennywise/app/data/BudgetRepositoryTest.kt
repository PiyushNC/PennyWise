package com.pennywise.app.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pennywise.app.data.local.AppDatabase
import com.pennywise.app.data.repository.BudgetRepository
import com.pennywise.app.data.repository.TransactionRepository
import com.pennywise.app.domain.model.Budget
import com.pennywise.app.domain.model.Transaction
import com.pennywise.app.domain.model.TransactionType
import com.pennywise.app.viewmodel.BudgetViewModel
import com.pennywise.app.viewmodel.currentMonthStart
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BudgetRepositoryTest {
    private lateinit var database: AppDatabase
    private lateinit var budgetRepository: BudgetRepository
    private lateinit var transactionRepository: TransactionRepository

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        budgetRepository = BudgetRepository(database.budgetDao())
        transactionRepository = TransactionRepository(database.transactionDao())
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun budgetsPersistAndDuplicateCategoryIsDetected() = runBlocking {
        val month = currentMonthStart()
        val budget = Budget(0, month, "Food", 5_000.0)
        val id = budgetRepository.save(budget)

        assertTrue(budgetRepository.existsForMonthAndCategory(month, "Food"))
        assertFalse(budgetRepository.existsForMonthAndCategory(month, "Food", excludingId = id))
        assertFalse(budgetRepository.existsForMonthAndCategory(month, "Transport"))
        assertEquals(1, budgetRepository.observeForMonth(month).first().size)
    }

    @Test
    fun onlyCurrentMonthExpensesCountTowardsCategorySpending() = runBlocking {
        val month = currentMonthStart()
        transactionRepository.save(
            Transaction(0, "Lunch", 500.0, TransactionType.EXPENSE, "Food", "", month + 1000, "UPI"),
        )
        transactionRepository.save(
            Transaction(0, "Salary", 50_000.0, TransactionType.INCOME, "Food", "", month + 2000, "Bank transfer"),
        )
        transactionRepository.save(
            Transaction(0, "Old lunch", 1_000.0, TransactionType.EXPENSE, "Food", "", month - 1, "Cash"),
        )

        val expenses = transactionRepository.transactions.first()
            .filter { it.type == TransactionType.EXPENSE }
            .filter { it.date in month until com.pennywise.app.viewmodel.nextMonthStart(month) }
            .filter { it.category == "Food" }
            .sumOf { it.amount }

        assertEquals(500.0, expenses, 0.0)
    }
}
