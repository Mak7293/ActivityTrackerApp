package com.example.runningtracker.db

import android.graphics.Bitmap
import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date


@Entity(tableName = "running_table")
data class RunningEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    val date: Date? = null,
    var runningImg: Uri? = null,
    var runningAvgSpeedKMH: Double = 0.0,
    var runningDistanceInMeters: Int = 0,
    var runningTimeInMillis: Long = 0L,
    var activity_type: String = "",
    val stepCount: Int = 0,
    var caloriesBurned: Double = 0.0
)

