package com.example.projectz.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projectz.domain.model.Transaction
import com.example.projectz.domain.model.TransactionType
import com.example.projectz.domain.repository.FinanceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TransactionListState(
    val allTransactions: List<Transaction> = emptyList(),
    val filteredTransactions: List<Transaction> = emptyList(),
    val currentFilter: TransactionFilter = TransactionFilter.ALL
)

enum class TransactionFilter {
    ALL, INCOME, EXPENSE
}

@HiltViewModel
class TransactionListViewModel @Inject constructor(
    private val repository: FinanceRepository
) : ViewModel() {

    private val _filter = MutableStateFlow(TransactionFilter.ALL)

    val state: StateFlow<TransactionListState> = combine(
        repository.getTransactions(),
        _filter
    ) { transactions, filter ->
        val filtered = when (filter) {
            TransactionFilter.ALL -> transactions
            TransactionFilter.INCOME -> transactions.filter { it.type == TransactionType.INCOME }
            TransactionFilter.EXPENSE -> transactions.filter { it.type == TransactionType.EXPENSE }
        }
        TransactionListState(
            allTransactions = transactions,
            filteredTransactions = filtered,
            currentFilter = filter
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TransactionListState()
    )

    fun onFilterSelected(filter: TransactionFilter) {
        _filter.value = filter
    }

    fun deleteTransaction(id: String) {
        viewModelScope.launch {
            repository.deleteTransaction(id)
        }
    }
}
