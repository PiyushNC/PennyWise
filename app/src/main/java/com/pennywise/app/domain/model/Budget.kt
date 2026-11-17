package com.pennywise.app.domain.model

data class Budget(
    val id: Long,
    val monthStart: Long,
    val category: String?,
    val amount: Double,
)
