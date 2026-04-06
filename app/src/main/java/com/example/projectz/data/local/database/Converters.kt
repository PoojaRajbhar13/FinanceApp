package com.example.projectz.data.local.database

import androidx.room.TypeConverter
import com.example.projectz.domain.model.Category
import com.example.projectz.domain.model.TransactionType
import java.time.LocalDate

class Converters {
    @TypeConverter
    fun fromLocalDate(date: LocalDate?): String? {
        return date?.toString()
    }

    @TypeConverter
    fun toLocalDate(dateString: String?): LocalDate? {
        return dateString?.let { LocalDate.parse(it) }
    }

    @TypeConverter
    fun fromTransactionType(type: TransactionType?): String? {
        return type?.name
    }

    @TypeConverter
    fun toTransactionType(typeString: String?): TransactionType? {
        return typeString?.let { TransactionType.valueOf(it) }
    }

    @TypeConverter
    fun fromCategory(category: Category?): String? {
        return category?.name
    }

    @TypeConverter
    fun toCategory(categoryString: String?): Category? {
        return categoryString?.let { Category.valueOf(it) }
    }
}
