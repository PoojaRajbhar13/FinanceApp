package com.example.projectz.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val targetAmount: Double,
    val currentAmount: Double,
    val deadline: LocalDate?,
    val isStreakActive: Boolean,
    val streakDays: Int,
    val dailyGoalAmount: Double,
    val lastSavedDate: LocalDate?,
    val createdAt: LocalDate
)
