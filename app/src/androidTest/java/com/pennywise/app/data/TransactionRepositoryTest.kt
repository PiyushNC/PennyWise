package com.pennywise.app.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pennywise.app.data.local.AppDatabase
import com.pennywise.app.data.repository.TransactionRepository
import com.pennywise.app.domain.model.Transaction
import com.pennywise.app.domain.model.TransactionType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TransactionRepositoryTest {
    private lateinit var database: AppDatabase
    private lateinit var repository: TransactionRepository

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = TransactionRepository(database.transactionDao())
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertReadUpdateAndDeleteTransaction() = runBlocking {
        val transaction = Transaction(
            id = 0,
            title = "Monthly salary",
            amount = 75_000.0,
            type = TransactionType.INCOME,
            category = "Salary",
            note = "Primary account",
            date = 1_700_000_000_000,
            paymentMethod = "Bank transfer",
        )

        val insertedId = repository.save(transaction)
        val inserted = repository.getTransaction(insertedId)
        assertNotNull(inserted)
        assertEquals("Monthly salary", inserted?.title)

        val updated = inserted!!.copy(amount = 80_000.0)
        repository.save(updated)
        assertEquals(80_000.0, repository.getTransaction(insertedId)?.amount ?: 0.0, 0.0)

        val observed = repository.transactions.first()
        assertEquals(1, observed.size)
        assertEquals(insertedId, observed.single().id)

        repository.delete(updated)
        assertNull(repository.getTransaction(insertedId))
        assertEquals(emptyList<Transaction>(), repository.transactions.first())
    }
}
