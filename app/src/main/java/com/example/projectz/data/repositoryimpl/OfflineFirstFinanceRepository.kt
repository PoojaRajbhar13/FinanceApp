package com.example.projectz.data.repositoryimpl

import com.example.projectz.data.local.dao.GoalDao
import com.example.projectz.data.local.dao.TransactionDao
import com.example.projectz.data.local.entity.GoalEntity
import com.example.projectz.data.local.entity.TransactionEntity
import com.example.projectz.domain.model.Goal
import com.example.projectz.domain.model.Transaction
import com.example.projectz.domain.repository.FinanceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class OfflineFirstFinanceRepository(
    private val transactionDao: TransactionDao,
    private val goalDao: GoalDao
) : FinanceRepository {

    override fun getTransactions(): Flow<List<Transaction>> {
        return transactionDao.getAllTransactions().map { entities ->
            entities.map { it.toTransaction() }
        }
    }

    override fun getPrimaryGoal(): Flow<Goal?> {
        return goalDao.getPrimaryGoal().map { it?.toGoal() }
    }

    override suspend fun addTransaction(transaction: Transaction) {
        transactionDao.insertTransaction(transaction.toEntity())
    }

    override suspend fun updateTransaction(transaction: Transaction) {
        transactionDao.updateTransaction(transaction.toEntity())
    }

    override suspend fun deleteTransaction(transactionId: String) {
        transactionDao.deleteTransactionById(transactionId)
    }

    override suspend fun updateGoal(goal: Goal) {
        goalDao.insertGoal(goal.toEntity())
    }

    override suspend fun deleteGoal(goalId: String) {
        goalDao.deleteGoalById(goalId)
    }
}

// Extension functions for mapping
private fun TransactionEntity.toTransaction(): Transaction {
    return Transaction(
        id = id,
        amount = amount,
        type = type,
        category = category,
        date = date,
        notes = notes
    )
}

private fun Transaction.toEntity(): TransactionEntity {
    return TransactionEntity(
        id = id,
        amount = amount,
        type = type,
        category = category,
        date = date,
        notes = notes
    )
}

private fun GoalEntity.toGoal(): Goal {
    return Goal(
        id = id,
        title = title,
        targetAmount = targetAmount,
        currentAmount = currentAmount,
        deadline = deadline,
        isStreakActive = isStreakActive,
        streakDays = streakDays,
        dailyGoalAmount = dailyGoalAmount,
        lastSavedDate = lastSavedDate,
        createdAt = createdAt
    )
}

private fun Goal.toEntity(): GoalEntity {
    return GoalEntity(
        id = id,
        title = title,
        targetAmount = targetAmount,
        currentAmount = currentAmount,
        deadline = deadline,
        isStreakActive = isStreakActive,
        streakDays = streakDays,
        dailyGoalAmount = dailyGoalAmount,
        lastSavedDate = lastSavedDate,
        createdAt = createdAt
    )
}
