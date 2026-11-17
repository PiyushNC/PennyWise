package com.pennywise.app

import android.app.Application
import com.pennywise.app.data.local.AppDatabase
import com.pennywise.app.data.repository.TransactionRepository
import com.pennywise.app.data.repository.BudgetRepository
import com.pennywise.app.data.repository.UserPreferencesRepository

class PennyWiseApplication : Application() {
    val transactionRepository: TransactionRepository by lazy {
        TransactionRepository(AppDatabase.getInstance(this).transactionDao())
    }

    val budgetRepository: BudgetRepository by lazy {
        BudgetRepository(AppDatabase.getInstance(this).budgetDao())
    }

    val userPreferencesRepository: UserPreferencesRepository by lazy {
        UserPreferencesRepository(this)
    }
}
