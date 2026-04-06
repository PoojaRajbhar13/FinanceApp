package com.example.projectz.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.projectz.data.local.dao.GoalDao
import com.example.projectz.data.local.dao.TransactionDao
import com.example.projectz.data.local.dao.UserPreferenceDao
import com.example.projectz.data.local.entity.GoalEntity
import com.example.projectz.data.local.entity.TransactionEntity
import com.example.projectz.data.local.entity.UserPreferenceEntity

@Database(
    entities = [TransactionEntity::class, GoalEntity::class, UserPreferenceEntity::class],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class FinanceDatabase : RoomDatabase() {
    abstract val transactionDao: TransactionDao
    abstract val goalDao: GoalDao
    abstract val userPreferenceDao: UserPreferenceDao
}
