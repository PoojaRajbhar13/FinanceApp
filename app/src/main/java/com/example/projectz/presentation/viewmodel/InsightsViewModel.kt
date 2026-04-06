package com.example.projectz.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projectz.domain.model.Category
import com.example.projectz.domain.model.TransactionType
import com.example.projectz.domain.repository.FinanceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class CategoryExpense(
    val category: Category,
    val amount: Double
)

data class InsightsState(
    val highestExpenseCategory: CategoryExpense? = null,
    val expensesByCategory: List<CategoryExpense> = emptyList(),
    val isReady: Boolean = false
)

@HiltViewModel
class InsightsViewModel @Inject constructor(
    repository: FinanceRepository
) : ViewModel() {

    val state: StateFlow<InsightsState> = repository.getTransactions().map { transactions ->
        val expenses = transactions.filter { it.type == TransactionType.EXPENSE }
        val categoryMap = expenses.groupBy { it.category }
            .mapValues { entry -> entry.value.sumOf { it.amount } }
            
        val sortedExpenses = categoryMap.map { CategoryExpense(it.key, it.value) }
            .sortedByDescending { it.amount }
            
        InsightsState(
            highestExpenseCategory = sortedExpenses.firstOrNull(),
            expensesByCategory = sortedExpenses,
            isReady = true
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = InsightsState()
    )
}
