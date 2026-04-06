package com.example.projectz.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.projectz.domain.model.Category
import com.example.projectz.domain.model.TransactionType
import java.time.LocalDate

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey
    val id: String,
    val amount: Double,
    val type: TransactionType,
    val category: Category,
    val date: LocalDate,
    val notes: String
)
