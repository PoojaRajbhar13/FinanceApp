package com.example.projectz.domain.model

import java.time.LocalDate

enum class TransactionType {
    INCOME, EXPENSE
}

enum class Category {
    FOOD, TRANSPORT, ENTERTAINMENT, SHOPPING, BILLS, SALARY, FREELANCE, SAVINGS, OTHER
}

data class Transaction(
    val id: String,
    val amount: Double,
    val type: TransactionType,
    val category: Category,
    val date: LocalDate,
    val notes: String
)

data class Goal(
    val id: String,
    val title: String,
    val targetAmount: Double,
    val currentAmount: Double,
    val deadline: LocalDate?,
    val isStreakActive: Boolean = false,
    val streakDays: Int = 0,
    val dailyGoalAmount: Double = 0.0,
    val lastSavedDate: LocalDate? = null,
    val createdAt: LocalDate = LocalDate.now()
) {
    val progress: Float
        get() = if (targetAmount > 0) (currentAmount / targetAmount).toFloat().coerceIn(0f, 1f) else 0f
        
    val daysLeft: Int
        get() {
            if (deadline == null) return -1
            val days = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), deadline).toInt()
            return if (days > 0) days else 0
        }
        
    val requiredPerDay: Double
        get() {
            if (daysLeft <= 0) return 0.0
            val remaining = targetAmount - currentAmount
            return if (remaining > 0) remaining / daysLeft else 0.0
        }
}
