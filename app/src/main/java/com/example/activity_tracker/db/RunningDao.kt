package com.example.activity_tracker.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import java.util.*


@Dao
interface RunningDao {

    @Insert
    suspend fun insertRun(run: RunningEntity)

    @Delete
    suspend fun deleteRun(run: RunningEntity)

    @Query("SELECT * FROM running_table ORDER BY date DESC")
    fun getAllActivitySortedByDate(): Flow<List<RunningEntity>>

    @Query("SELECT * FROM running_table WHERE date=:date")
    fun getAllActivityInSpecificDate(date: Date): List<RunningEntity>

}