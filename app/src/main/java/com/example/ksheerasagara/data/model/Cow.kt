package com.example.ksheerasagara.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cows")
data class Cow(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: String = "",          // ← NEW
    val name: String,
    val breed: String,
    val age: Int
)