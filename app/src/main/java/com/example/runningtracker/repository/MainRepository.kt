package com.example.runningtracker.repository

import com.example.runningtracker.db.RunningDao
import com.example.runningtracker.db.RunningEntity
import hilt_aggregated_deps._com_example_mvvmrunningtrackerapp_di_AppModule
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