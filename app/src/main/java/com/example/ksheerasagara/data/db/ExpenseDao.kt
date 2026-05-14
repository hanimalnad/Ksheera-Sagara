package com.example.ksheerasagara.data.db

import androidx.room.*
import com.example.ksheerasagara.data.model.Expense
import com.example.ksheerasagara.data.model.ExpenseCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(expense: Expense)

    @Delete
    suspend fun delete(expense: Expense)

    @Query("SELECT * FROM expenses WHERE userId = :userId ORDER BY date DESC")
    fun getAllExpenses(userId: String): Flow<List<Expense>>

    @Query("""
        SELECT * FROM expenses 
        WHERE userId = :userId AND date LIKE :monthPrefix || '%'
    """)
    fun getExpensesByMonth(userId: String, monthPrefix: String): Flow<List<Expense>>

    @Query("""
        SELECT SUM(amount) FROM expenses 
        WHERE userId = :userId AND date LIKE :monthPrefix || '%'
    """)
    fun getTotalExpensesForMonth(userId: String, monthPrefix: String): Flow<Double?>

    @Query("""
        SELECT SUM(amount) FROM expenses 
        WHERE userId = :userId 
        AND category = :category 
        AND date LIKE :monthPrefix || '%'
    """)
    fun getExpenseByCategory(
        userId: String,
        category: ExpenseCategory,
        monthPrefix: String
    ): Flow<Double?>
}