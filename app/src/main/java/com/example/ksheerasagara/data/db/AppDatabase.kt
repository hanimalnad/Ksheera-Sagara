package com.example.ksheerasagara.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.ksheerasagara.data.model.Cow
import com.example.ksheerasagara.data.model.Expense
import com.example.ksheerasagara.data.model.MilkEntry

@Database(
    entities  = [MilkEntry::class, Expense::class, Cow::class],
    version   = 2,          // ← bumped from 1 to 2
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun milkEntryDao(): MilkEntryDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun cowDao(): CowDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        // Migration: add userId column (empty string default) to all 3 tables
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE milk_entries ADD COLUMN userId TEXT NOT NULL DEFAULT ''"
                )
                database.execSQL(
                    "ALTER TABLE expenses ADD COLUMN userId TEXT NOT NULL DEFAULT ''"
                )
                database.execSQL(
                    "ALTER TABLE cows ADD COLUMN userId TEXT NOT NULL DEFAULT ''"
                )
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "ksheera_sagara_db"
                )
                    .addMigrations(MIGRATION_1_2)   // ← apply migration
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}