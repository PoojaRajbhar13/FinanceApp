package com.example.projectz.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projectz.domain.model.Goal
import com.example.projectz.domain.model.Transaction
import com.example.projectz.domain.model.TransactionType
import com.example.projectz.domain.repository.FinanceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject

data class DashboardState(
    val balance: Double = 0.0,
    val totalIncome: Double = 0.0,
    val totalExpenses: Double = 0.0,
    val recentTransactions: List<Transaction> = emptyList(),
    val primaryGoal: Goal? = null,
    val showAddGoalDialog: Boolean = false,
    val isLoading: Boolean = true
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: FinanceRepository
) : ViewModel() {

    private val _isAddGoalDialogVisible = MutableStateFlow(false)

    val state: StateFlow<DashboardState> = combine(
        repository.getTransactions(),
        repository.getPrimaryGoal(),
        _isAddGoalDialogVisible
    ) { transactions, goal, showGoalDialog ->
        val income = transactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
        val expenses = transactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
        val balance = income - expenses
        
        DashboardState(
            balance = balance,
            totalIncome = income,
            totalExpenses = expenses,
            recentTransactions = transactions.take(3),
            primaryGoal = goal,
            showAddGoalDialog = showGoalDialog,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardState(isLoading = true)
    )

    fun deleteTransaction(id: String) {
        viewModelScope.launch {
            repository.deleteTransaction(id)
        }
    }

    fun showAddGoalDialog() {
        _isAddGoalDialogVisible.value = true
    }

    fun hideAddGoalDialog() {
        _isAddGoalDialogVisible.value = false
    }

    fun saveGoal(title: String, targetAmount: Double, dailyAmount: Double, deadline: LocalDate?) {
        viewModelScope.launch {
            val newGoal = Goal(
                id = UUID.randomUUID().toString(),
                title = title,
                targetAmount = targetAmount,
                currentAmount = 0.0,
                deadline = deadline,
                isStreakActive = false,
                streakDays = 0,
                dailyGoalAmount = dailyAmount,
                lastSavedDate = null,
                createdAt = LocalDate.now()
            )
            repository.updateGoal(newGoal)
            hideAddGoalDialog()
        }
    }
}
