package com.pennywise.app.data.repository

import com.pennywise.app.data.local.BudgetDao
import com.pennywise.app.data.local.BudgetEntity
import com.pennywise.app.domain.model.Budget
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class BudgetRepository(
    private val dao: BudgetDao,
) {
    fun observeForMonth(monthStart: Long): Flow<List<Budget>> =
        dao.observeForMonth(monthStart).map { budgets -> budgets.map { it.toDomain() } }

    suspend fun getById(id: Long): Budget? = dao.getById(id)?.toDomain()

    suspend fun existsForMonthAndCategory(
        monthStart: Long,
        category: String?,
        excludingId: Long = 0L,
    ): Boolean = dao.getByMonthAndCategory(monthStart, category.orEmpty())
        ?.let { it.id != excludingId } == true

    suspend fun save(budget: Budget): Long {
        val entity = budget.toEntity()
        return if (budget.id == 0L) {
            dao.insert(entity)
        } else {
            dao.update(entity)
            budget.id
        }
    }

    suspend fun delete(budget: Budget) {
        dao.delete(budget.toEntity())
    }

    private fun BudgetEntity.toDomain() = Budget(
        id = id,
        monthStart = monthStart,
        category = categoryKey.ifBlank { null },
        amount = amount,
    )

    private fun Budget.toEntity() = BudgetEntity(
        id = id,
        monthStart = monthStart,
        categoryKey = category.orEmpty(),
        amount = amount,
    )
}
