package com.example.activity_tracker.db

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
    var runningAvgSpeedKMH: Double? = null,
    var runningDistanceInMeters: Int = 0,
    var runningTimeInMillis: Long? = null,
    var activity_type: String = "",
    val stepCount: Int? = null,
    var caloriesBurned: Double = 0.0
)

