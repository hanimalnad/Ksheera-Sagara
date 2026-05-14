package com.example.ksheerasagara.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class ExpenseCategory { FODDER, MEDICAL, LABOR, ELECTRICITY, OTHER }

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: String = "",          // ← NEW
    val date: String,
    val category: ExpenseCategory,
    val description: String,
    val amount: Double,
    val cowName: String = "General"
)