package com.example.runningtracker.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update
import com.example.runningtracker.db.RunningEntity


@Dao
interface RunningDao {

    @Insert
    suspend fun insertRun(run: RunningEntity)

    @Delete
    suspend fun deleteRun(run: RunningEntity)

}