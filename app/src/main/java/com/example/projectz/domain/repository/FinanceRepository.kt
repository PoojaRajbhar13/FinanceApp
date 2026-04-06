package com.example.projectz.domain.repository

import com.example.projectz.domain.model.Goal
import com.example.projectz.domain.model.Transaction
import kotlinx.coroutines.flow.Flow

interface FinanceRepository {
    fun getTransactions(): Flow<List<Transaction>>
    fun getPrimaryGoal(): Flow<Goal?>
    
    suspend fun addTransaction(transaction: Transaction)
    suspend fun updateTransaction(transaction: Transaction)
    suspend fun deleteTransaction(transactionId: String)
    
    suspend fun updateGoal(goal: Goal)
    suspend fun deleteGoal(goalId: String)
}
