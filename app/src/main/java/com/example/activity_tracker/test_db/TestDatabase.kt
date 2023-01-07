package com.example.activity_tracker.test_db

import android.content.Context
import com.example.activity_tracker.db.RunningEntity
import com.example.activity_tracker.ui.view_model.MainViewModel
import com.example.activity_tracker.util.Constants
import java.text.SimpleDateFormat
import java.util.*


class TestDatabase(
    private val sdf: SimpleDateFormat,
    private val viewModel: MainViewModel,
    private val context: Context
){
    private fun generateDate(pastDay: Float): Date? {
        val dateTimeStamp = Calendar.getInstance().timeInMillis - (pastDay * 86_400_000).toLong()
        val dateString = sdf.format(Date(dateTimeStamp))
        val date: Date? = sdf.parse(dateString)
        return date
    }
    private fun generateTimeInMillis(minute: Float): Long {
        return (minute * 60 * 1000).toLong()
    }

    private fun getTestRunningList():List<RunningEntity>{

        val run: List<RunningEntity> = listOf(

            RunningEntity(
                id = null,
                date = generateDate(0.2f),
                runningImg = null,
                runningAvgSpeedKMH = 7.0,
                runningDistanceInMeters = 3000,
                runningTimeInMillis = generateTimeInMillis(6.0f),
                activity_type = Constants.ACTIVITY_RUN_OR_WALK,
                stepCount = 0,
                caloriesBurned = 17.5
            ),
            RunningEntity(
                id = null,
                date = generateDate(1.4f),
                runningImg = null,
                runningAvgSpeedKMH = 25.0,
                runningDistanceInMeters = 15000,
                runningTimeInMillis = generateTimeInMillis(12.0f),
                activity_type = Constants.ACTIVITY_CYCLING,
                stepCount = 0,
                caloriesBurned = 18.0
            ),
            RunningEntity(
                id = null,
                date = generateDate(0.02f),
                runningImg = null,
                runningAvgSpeedKMH = 10.0,
                runningDistanceInMeters = 5000,
                runningTimeInMillis = generateTimeInMillis(10.0f),
                activity_type = Constants.ACTIVITY_RUN_OR_WALK,
                stepCount = 0,
                caloriesBurned = 20.3
            ),
            RunningEntity(
                id = null,
                date = generateDate(2.6f),
                runningImg = null,
                runningAvgSpeedKMH = 10.0,
                runningDistanceInMeters = 5000,
                runningTimeInMillis = generateTimeInMillis(10.0f),
                activity_type = Constants.ACTIVITY_RUN_OR_WALK,
                stepCount = 0,
                caloriesBurned = 20.3
            ),
            RunningEntity(
                id = null,
                date = generateDate(1.35f),
                runningImg = null,
                runningAvgSpeedKMH = 15.0,
                runningDistanceInMeters = 20000,
                runningTimeInMillis = generateTimeInMillis(18.0f),
                activity_type = Constants.ACTIVITY_CYCLING,
                stepCount = 0,
                caloriesBurned = 22.0
            ),
            RunningEntity(
                id = null,
                date = generateDate(2.15f),
                runningImg = null,
                runningAvgSpeedKMH = 5.0,
                runningDistanceInMeters = 3000,
                runningTimeInMillis = generateTimeInMillis(22.0f),
                activity_type = Constants.ACTIVITY_RUN_OR_WALK,
                stepCount = 0,
                caloriesBurned = 15.0
            ),
            RunningEntity(
                id = null,
                date = generateDate(4.1f),
                runningImg = null,
                runningAvgSpeedKMH = 12.0,
                runningDistanceInMeters = 7000,
                runningTimeInMillis = generateTimeInMillis(18.3f),
                activity_type = Constants.ACTIVITY_CYCLING,
                stepCount = 0,
                caloriesBurned = 17.5
            ),
            RunningEntity(
                id = null,
                date = generateDate(3.35f),
                runningImg = null,
                runningAvgSpeedKMH = 10.0,
                runningDistanceInMeters = 5000,
                runningTimeInMillis = generateTimeInMillis(10.0f),
                activity_type = Constants.ACTIVITY_CYCLING,
                stepCount = 0,
                caloriesBurned = 20.3
            ),
            RunningEntity(
                id = null,
                date = generateDate(5.2f),
                runningImg = null,
                runningAvgSpeedKMH = 10.0,
                runningDistanceInMeters = 5000,
                runningTimeInMillis = generateTimeInMillis(10.0f),
                activity_type = Constants.ACTIVITY_RUN_OR_WALK,
                stepCount = 0,
                caloriesBurned = 27.5
            ),
            RunningEntity(
                id = null,
                date = generateDate(5.45f),
                runningImg = null,
                runningAvgSpeedKMH = 10.0,
                runningDistanceInMeters = 5000,
                runningTimeInMillis = generateTimeInMillis(10.0f),
                activity_type = Constants.ACTIVITY_CYCLING,
                stepCount = 0,
                caloriesBurned = 16.7
            ),
            RunningEntity(
                id = null,
                date = generateDate(6.5f),
                runningImg = null,
                runningAvgSpeedKMH = 10.0,
                runningDistanceInMeters = 5000,
                runningTimeInMillis = generateTimeInMillis(10.0f),
                activity_type = Constants.ACTIVITY_RUN_OR_WALK,
                stepCount = 0,
                caloriesBurned = 25.0
            ),
            RunningEntity(
                id = null,
                date = generateDate(7.68f),
                runningImg = null,
                runningAvgSpeedKMH = 10.0,
                runningDistanceInMeters = 5000,
                runningTimeInMillis = generateTimeInMillis(10.0f),
                activity_type = Constants.ACTIVITY_CYCLING,
                stepCount = 0,
                caloriesBurned = 31.25
            ),
            RunningEntity(
                id = null,
                date = generateDate(7.9f),
                runningImg = null,
                runningAvgSpeedKMH = 10.0,
                runningDistanceInMeters = 5000,
                runningTimeInMillis = generateTimeInMillis(10.0f),
                activity_type = Constants.ACTIVITY_RUN_OR_WALK,
                stepCount = 0,
                caloriesBurned = 33.2
            )
        )
        return run
    }
    fun saveFakeDateToDb(list: List<RunningEntity> = getTestRunningList()){
        for (i in list){
            viewModel.insertRun(i)
        }
    }
}