package com.example.projectz.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projectz.domain.model.Goal
import com.example.projectz.domain.repository.FinanceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class GoalsViewModel @Inject constructor(
    private val repository: FinanceRepository
) : ViewModel() {

    val goalState: StateFlow<Goal?> = repository.getPrimaryGoal()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Companion.WhileSubscribed(5000),
            initialValue = null
        )

    private val _uiEvents = MutableSharedFlow<String>()
    val uiEvents: SharedFlow<String> = _uiEvents.asSharedFlow()

    val isEditDialogVisible = MutableStateFlow(false)
    fun showEditDialog() { isEditDialogVisible.value = true }
    fun hideEditDialog() { isEditDialogVisible.value = false }

    val showDeleteDialog = MutableStateFlow(false)
    fun triggerDeleteDialog() { showDeleteDialog.value = true }
    fun onDeleteCancel() { showDeleteDialog.value = false }

    val showEditAmountDialog = MutableStateFlow(false)
    fun triggerEditAmountDialog() { showEditAmountDialog.value = true }
    fun hideEditAmountDialog() { showEditAmountDialog.value = false }

    fun addDailyAmount() {
        val currentGoal = goalState.value ?: return
        addMoney(currentGoal.dailyGoalAmount)
    }

    fun addMoney(amount: Double) {
        val currentGoal = goalState.value ?: return
        if (amount <= 0) return

        val today = LocalDate.now()
        val lastSaved = currentGoal.lastSavedDate

        var newStreak = currentGoal.streakDays
        var isStreakActive = currentGoal.isStreakActive

        if (lastSaved == null) {
            newStreak = 1
            isStreakActive = true
        } else if (lastSaved.isEqual(today)) {
            // Already saved today
        } else if (lastSaved.isEqual(today.minusDays(1))) {
            newStreak += 1
            isStreakActive = true
        } else {
            newStreak = 1
            isStreakActive = true
        }

        viewModelScope.launch {
            val updatedGoal = currentGoal.copy(
                currentAmount = currentGoal.currentAmount + amount,
                lastSavedDate = today,
                streakDays = newStreak,
                isStreakActive = isStreakActive
            )
            repository.updateGoal(updatedGoal)
            _uiEvents.emit("Amount added successfully ✅")
        }
    }

    fun updateSavedAmount(newAmount: Double) {
        val currentGoal = goalState.value ?: return
        if (newAmount < 0 || newAmount > currentGoal.targetAmount) {
            viewModelScope.launch { _uiEvents.emit("Invalid amount!") }
            return
        }

        viewModelScope.launch {
            val updated = currentGoal.copy(currentAmount = newAmount)
            repository.updateGoal(updated)
            hideEditAmountDialog()
            _uiEvents.emit("Amount updated successfully ✅")
        }
    }

    fun updateGoal(title: String, targetAmount: Double, dailyAmount: Double, deadline: LocalDate?) {
        val currentGoal = goalState.value ?: return
        viewModelScope.launch {
            val updated = currentGoal.copy(
                title = title,
                targetAmount = targetAmount,
                dailyGoalAmount = dailyAmount,
                deadline = deadline
            )
            repository.updateGoal(updated)
            hideEditDialog()
            _uiEvents.emit("Goal updated successfully ✅")
        }
    }

    fun createGoal(title: String, targetAmount: Double, dailyAmount: Double, deadline: LocalDate?) {
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
            hideEditDialog()
            _uiEvents.emit("Goal created successfully ✅")
        }
    }

    fun onDeleteConfirm() {
        val currentGoal = goalState.value ?: return
        viewModelScope.launch {
            repository.deleteGoal(currentGoal.id)
            showDeleteDialog.value = false
            _uiEvents.emit("Goal deleted successfully 🗑️")
        }
    }
}