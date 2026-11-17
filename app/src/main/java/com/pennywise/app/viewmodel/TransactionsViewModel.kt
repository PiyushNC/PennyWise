package com.pennywise.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.pennywise.app.data.repository.TransactionRepository
import com.pennywise.app.domain.model.Transaction
import com.pennywise.app.domain.model.TransactionType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class TransactionFilter {
    ALL,
    INCOME,
    EXPENSE,
}

enum class TransactionSort {
    DATE,
    AMOUNT,
}

data class TransactionsUiState(
    val transactions: List<Transaction> = emptyList(),
    val query: String = "",
    val filter: TransactionFilter = TransactionFilter.ALL,
    val category: String? = null,
    val sort: TransactionSort = TransactionSort.DATE,
    val isLoading: Boolean = true,
) {
    val categories: List<String>
        get() = transactions.map { it.category }.distinct().sorted()
}

class TransactionsViewModel(
    private val repository: TransactionRepository,
) : ViewModel() {
    private val query = MutableStateFlow("")
    private val filter = MutableStateFlow(TransactionFilter.ALL)
    private val category = MutableStateFlow<String?>(null)
    private val sort = MutableStateFlow(TransactionSort.DATE)

    val uiState: StateFlow<TransactionsUiState> = combine(
        repository.transactions,
        query,
        filter,
        category,
        sort,
    ) { transactions, search, typeFilter, categoryFilter, sortOrder ->
        val filtered = transactions
            .filter { it.title.contains(search.trim(), ignoreCase = true) }
            .filter {
                typeFilter == TransactionFilter.ALL ||
                    (typeFilter == TransactionFilter.INCOME && it.type == TransactionType.INCOME) ||
                    (typeFilter == TransactionFilter.EXPENSE && it.type == TransactionType.EXPENSE)
            }
            .filter { categoryFilter == null || it.category == categoryFilter }
            .let { list ->
                when (sortOrder) {
                    TransactionSort.DATE -> list.sortedByDescending { it.date }
                    TransactionSort.AMOUNT -> list.sortedByDescending { it.amount }
                }
            }
        TransactionsUiState(
            transactions = filtered,
            query = search,
            filter = typeFilter,
            category = categoryFilter,
            sort = sortOrder,
            isLoading = false,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), TransactionsUiState())

    fun setQuery(value: String) {
        query.value = value
    }

    fun setFilter(value: TransactionFilter) {
        filter.value = value
    }

    fun setCategory(value: String?) {
        category.value = value
    }

    fun setSort(value: TransactionSort) {
        sort.value = value
    }

    fun delete(transaction: Transaction) {
        viewModelScope.launch { repository.delete(transaction) }
    }

    suspend fun getTransaction(id: Long): Transaction? = repository.getTransaction(id)

    fun save(transaction: Transaction) {
        viewModelScope.launch { repository.save(transaction) }
    }

    class Factory(
        private val repository: TransactionRepository,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(TransactionsViewModel::class.java)) {
                return TransactionsViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
        }
    }
}
