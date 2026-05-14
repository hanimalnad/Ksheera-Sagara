package com.example.ksheerasagara.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "milk_entries")
data class MilkEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: String = "",          // ← NEW
    val date: String,
    val cowName: String,
    val liters: Double,
    val fatPercent: Double,
    val snfPercent: Double,
    val ratePerLiter: Double,
    val totalIncome: Double
)