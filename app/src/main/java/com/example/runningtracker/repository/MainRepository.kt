package com.example.runningtracker.repository

import com.example.runningtracker.db.RunningDao
import com.example.runningtracker.db.RunningEntity
import javax.inject.Inject

class MainRepository@Inject constructor(
    private val runDao: RunningDao
) {

    suspend fun insertRun(run: RunningEntity) = runDao.insertRun(run)

    suspend fun deleteRun(run: RunningEntity) = runDao.deleteRun(run)
}