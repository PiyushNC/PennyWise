package com.pennywise.app.domain.model

enum class TransactionType {
    INCOME,
    EXPENSE,
}

data class Transaction(
    val id: Long,
    val title: String,
    val amount: Double,
    val type: TransactionType,
    val category: String,
    val note: String,
    val date: Long,
    val paymentMethod: String,
)
