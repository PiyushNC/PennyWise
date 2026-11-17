package com.pennywise.app.data.repository

import com.pennywise.app.data.local.TransactionDao
import com.pennywise.app.data.local.TransactionEntity
import com.pennywise.app.domain.model.Transaction
import com.pennywise.app.domain.model.TransactionType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TransactionRepository(
    private val dao: TransactionDao,
) {
    val transactions: Flow<List<Transaction>> = dao.observeTransactions().map { entities ->
        entities.map { it.toDomain() }
    }

    suspend fun getTransaction(id: Long): Transaction? =
        dao.getTransaction(id)?.toDomain()

    suspend fun save(transaction: Transaction): Long {
        val entity = transaction.toEntity()
        return if (transaction.id == 0L) {
            dao.insert(entity)
        } else {
            dao.update(entity)
            transaction.id
        }
    }

    suspend fun delete(transaction: Transaction) {
        dao.delete(transaction.toEntity())
    }

    private fun TransactionEntity.toDomain() = Transaction(
        id = id,
        title = title,
        amount = amount,
        type = TransactionType.valueOf(type),
        category = category,
        note = note,
        date = date,
        paymentMethod = paymentMethod,
    )

    private fun Transaction.toEntity() = TransactionEntity(
        id = id,
        title = title,
        amount = amount,
        type = type.name,
        category = category,
        note = note,
        date = date,
        paymentMethod = paymentMethod,
    )
}
