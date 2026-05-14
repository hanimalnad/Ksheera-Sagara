package com.example.ksheerasagara.data.db

import androidx.room.*
import com.example.ksheerasagara.data.model.Cow
import kotlinx.coroutines.flow.Flow

@Dao
interface CowDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(cow: Cow)

    @Delete
    suspend fun delete(cow: Cow)

    @Query("SELECT * FROM cows WHERE userId = :userId")
    fun getAllCows(userId: String): Flow<List<Cow>>
}