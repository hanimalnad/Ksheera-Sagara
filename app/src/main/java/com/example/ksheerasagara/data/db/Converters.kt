package com.example.ksheerasagara.data.db

import androidx.room.TypeConverter
import com.example.ksheerasagara.data.model.ExpenseCategory

class Converters {
    @TypeConverter
    fun fromExpenseCategory(value: ExpenseCategory): String {
        return value.name
    }

    @TypeConverter
    fun toExpenseCategory(value: String): ExpenseCategory {
        return try {
            ExpenseCategory.valueOf(value)
        } catch (e: IllegalArgumentException) {
            ExpenseCategory.OTHER
        }
    }
}