package com.example.projectz.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projectz.domain.model.Category
import com.example.projectz.domain.model.Transaction
import com.example.projectz.domain.model.TransactionType
import com.example.projectz.domain.repository.FinanceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject

data class AddTransactionState(
    val amount: String = "",
    val type: TransactionType = TransactionType.EXPENSE,
    val category: Category = Category.FOOD,
    val notes: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSaved: Boolean = false
)

@HiltViewModel
class AddTransactionViewModel @Inject constructor(
    private val repository: FinanceRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AddTransactionState())
    val state: StateFlow<AddTransactionState> = _state.asStateFlow()

    private var editingTransactionId: String? = null
    private var isInitializedForEdit = false

    fun initForEdit(transactionId: String?) {
        if (transactionId == null || isInitializedForEdit) return
        isInitializedForEdit = true
        editingTransactionId = transactionId
        
        viewModelScope.launch {
            val transactions = repository.getTransactions().first()
            val transaction = transactions.find { it.id == transactionId }
            if (transaction != null) {
                _state.update {
                    it.copy(
                        amount = transaction.amount.toString(),
                        type = transaction.type,
                        category = transaction.category,
                        notes = transaction.notes
                    )
                }
            }
        }
    }

    fun onAmountChange(amount: String) {
        if (amount.isEmpty() || amount.matches(Regex("^\\d*\\.?\\d{0,2}\$"))) {
            _state.update { it.copy(amount = amount, error = null) }
        }
    }

    fun onTypeChange(type: TransactionType) {
        val defaultCategory = if (type == TransactionType.INCOME) Category.SALARY else Category.FOOD
        _state.update { it.copy(type = type, category = defaultCategory) }
    }

    fun onCategoryChange(category: Category) {
        _state.update { it.copy(category = category) }
    }

    fun onNotesChange(notes: String) {
        _state.update { it.copy(notes = notes) }
    }

    fun saveTransaction() {
        val amountValue = _state.value.amount.toDoubleOrNull()
        if (amountValue == null || amountValue <= 0) {
            _state.update { it.copy(error = "Please enter a valid amount") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            
            if (editingTransactionId != null) {
                val transactions = repository.getTransactions().first()
                val existing = transactions.find { it.id == editingTransactionId }
                if (existing != null) {
                    val updated = existing.copy(
                        amount = amountValue,
                        type = _state.value.type,
                        category = _state.value.category,
                        notes = _state.value.notes.trim()
                    )
                    repository.updateTransaction(updated)
                }
            } else {
                val transaction = Transaction(
                    id = UUID.randomUUID().toString(),
                    amount = amountValue,
                    type = _state.value.type,
                    category = _state.value.category,
                    date = LocalDate.now(),
                    notes = _state.value.notes.trim()
                )
                repository.addTransaction(transaction)
            }
            
            _state.update { it.copy(isLoading = false, isSaved = true) }
        }
    }
}
