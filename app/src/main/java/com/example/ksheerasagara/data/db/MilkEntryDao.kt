package com.example.ksheerasagara.data.db

import androidx.room.*
import com.example.ksheerasagara.data.model.MilkEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface MilkEntryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: MilkEntry)

    @Delete
    suspend fun delete(entry: MilkEntry)

    @Query("SELECT * FROM milk_entries WHERE userId = :userId ORDER BY date DESC")
    fun getAllEntries(userId: String): Flow<List<MilkEntry>>

    @Query("""
        SELECT * FROM milk_entries 
        WHERE userId = :userId AND date LIKE :monthPrefix || '%' 
        ORDER BY date DESC
    """)
    fun getEntriesByMonth(userId: String, monthPrefix: String): Flow<List<MilkEntry>>

    @Query("""
        SELECT SUM(totalIncome) FROM milk_entries 
        WHERE userId = :userId AND date LIKE :monthPrefix || '%'
    """)
    fun getTotalIncomeForMonth(userId: String, monthPrefix: String): Flow<Double?>

    @Query("""
        SELECT SUM(liters) FROM milk_entries 
        WHERE userId = :userId AND date LIKE :monthPrefix || '%'
    """)
    fun getTotalLitersForMonth(userId: String, monthPrefix: String): Flow<Double?>

    @Query("""
        SELECT * FROM milk_entries 
        WHERE userId = :userId AND cowName = :cowName 
        ORDER BY date DESC
    """)
    fun getEntriesByCow(userId: String, cowName: String): Flow<List<MilkEntry>>
}