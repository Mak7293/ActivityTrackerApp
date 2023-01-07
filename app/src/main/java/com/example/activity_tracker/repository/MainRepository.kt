package com.example.activity_tracker.repository

import com.example.activity_tracker.db.RunningDao
import com.example.activity_tracker.db.RunningEntity
import java.util.*
import javax.inject.Inject

class MainRepository@Inject constructor(
    private val runDao: RunningDao
) {

    suspend fun insertRun(run: RunningEntity) = runDao.insertRun(run)

    suspend fun deleteRun(run: RunningEntity) = runDao.deleteRun(run)

    fun getAllActivitySortedByDate() = runDao.getAllActivitySortedByDate()

    fun getAllActivityInSpecificDate(date: Date) = runDao.getAllActivityInSpecificDate(date)
}