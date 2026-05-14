package com.example.ksheerasagara.data.repository

import com.example.ksheerasagara.data.db.AppDatabase
import com.example.ksheerasagara.data.model.*

class DairyRepository(private val db: AppDatabase, private val userId: String) {

    // Milk
    fun getAllMilkEntries() =
        db.milkEntryDao().getAllEntries(userId)

    fun getIncomeByMonth(month: String) =
        db.milkEntryDao().getTotalIncomeForMonth(userId, month)

    suspend fun insertMilk(entry: MilkEntry) =
        db.milkEntryDao().insert(entry.copy(userId = userId))

    suspend fun deleteMilk(entry: MilkEntry) =
        db.milkEntryDao().delete(entry)

    // Expenses
    fun getAllExpenses() =
        db.expenseDao().getAllExpenses(userId)

    fun getTotalExpensesByMonth(month: String) =
        db.expenseDao().getTotalExpensesForMonth(userId, month)

    fun getExpenseByCategory(cat: ExpenseCategory, month: String) =
        db.expenseDao().getExpenseByCategory(userId, cat, month)

    suspend fun insertExpense(expense: Expense) =
        db.expenseDao().insert(expense.copy(userId = userId))

    suspend fun deleteExpense(expense: Expense) =
        db.expenseDao().delete(expense)

    // Cows
    fun getAllCows() =
        db.cowDao().getAllCows(userId)

    suspend fun insertCow(cow: Cow) =
        db.cowDao().insert(cow.copy(userId = userId))

    suspend fun deleteCow(cow: Cow) =
        db.cowDao().delete(cow)
}