package com.example.haojie_huang_myruns3.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface InformationDatabaseDao
{
    @Insert
    suspend fun insertEntry(information: Information)

    @Query("SELECT * FROM information_table")
    fun getAllEntries(): Flow<List<Information>>

    @Query("DELETE FROM information_table")
    suspend fun deleteAll()

    @Query("DELETE FROM information_table WHERE id = :key")
    suspend fun deleteEntry(key: Long)

    @Query("SELECT COUNT(id) FROM information_table")
    suspend fun getCount(): Int
}