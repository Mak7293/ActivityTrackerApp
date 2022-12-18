package com.example.runningtracker.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.runningtracker.path_model.path.Polyline
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.concurrent.TimeUnit
import kotlin.math.round

object PrimaryUtility {

    fun hasLocationPermissions(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            (ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(context,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED)
        }else{
            (ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(context,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(context,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                    == PackageManager.PERMISSION_GRANTED)
        }
    }
    fun calculateDistance(pathPoints: MutableList<Polyline>): Double{
        var distance: Double = 0.0
        for(polyline in pathPoints){
            val line = org.osmdroid.views.overlay.Polyline()
            line.apply {
                outlinePaint.color = Color.RED
                outlinePaint.strokeWidth = 10f
                setPoints(polyline.latLang)
            }
            distance += line.distance
        }
        return distance
    }
    fun getFormattedDistance(distanceInMeter: Double): String{
        if(distanceInMeter < 1000){
            val df = DecimalFormat("###.#")
            df.roundingMode = RoundingMode.CEILING
            return "${df.format(distanceInMeter).toDouble()} meter"
        }else{
            val df = DecimalFormat("##.##")
            df.roundingMode = RoundingMode.CEILING
            return "${df.format(distanceInMeter/1000).toDouble()} km"
        }
    }
    fun getAvgSpeed(distanceInMeter: Double, timeRun: Long): Double {
        return (round((distanceInMeter / (timeRun / 1000)) * 3.6)*100)/100
    }
    fun calculateBurnedCalories(time: Long, distance: Double, weight: Float,activity: String): Double{
        val speedInKmHr = getAvgSpeed(distance,time)
        val timeInMinute: Double = time.toDouble()/(1000*60)
        val met: Float = if(activity == Constants.ACTIVITY_RUN_OR_WALK) {
            when {
                speedInKmHr < 6.4 -> 5.0f
                6.4 <= speedInKmHr && speedInKmHr < 8.0 -> 8.3f
                8.0 <= speedInKmHr && speedInKmHr < 8.35 -> 9.0f
                8.35 <= speedInKmHr && speedInKmHr < 9.6 -> 9.8f
                9.6 <= speedInKmHr && speedInKmHr < 10.7 -> 10.5f
                10.7 <= speedInKmHr && speedInKmHr < 11.2 -> 11.0f
                11.2 <= speedInKmHr && speedInKmHr < 12.0 -> 11.5f
                12.0 <= speedInKmHr && speedInKmHr < 12.8 -> 11.8f
                12.8 <= speedInKmHr && speedInKmHr < 13.8 -> 12.3f
                13.8 <= speedInKmHr && speedInKmHr < 14.4 -> 12.8f
                14.4 <= speedInKmHr && speedInKmHr < 16.0 -> 14.5f
                16.0 <= speedInKmHr && speedInKmHr < 17.6 -> 16.0f
                17.6 <= speedInKmHr && speedInKmHr < 19.2 -> 19.0f
                19.2 <= speedInKmHr && speedInKmHr < 20.8 -> 19.8f
                20.8 <= speedInKmHr && speedInKmHr < 22.4 -> 23.0f
                else -> 25.0f
            }
        }else {  // in this case activity is bicycling
            when {
                speedInKmHr < 9.0                         -> 3.5f
                9.0  <= speedInKmHr && speedInKmHr < 16.0 -> 4.0f
                16.0 <= speedInKmHr && speedInKmHr < 19.0 -> 6.2f
                19.0 <= speedInKmHr && speedInKmHr < 22.5 -> 7.2f
                22.5 <= speedInKmHr && speedInKmHr < 25.5 -> 9.0f
                25.5 <= speedInKmHr && speedInKmHr < 29.0 -> 11.0f
                29.0 <= speedInKmHr && speedInKmHr < 32.0 -> 12.8f
                32.0 <= speedInKmHr && speedInKmHr < 35.2 -> 14.8f
                35.2 <= speedInKmHr && speedInKmHr < 38.5 -> 16.5f
                else -> 18.5f
            }
        }
        val caloriesBurnedPerMinute = (met * weight * 3.5)/200
        return (round(caloriesBurnedPerMinute * timeInMinute * 100)) /100
    }
    fun getFormattedStopWatchTime(ms: Long,includeMillis: Boolean = false): String{
        var milliseconds = ms
        val hours = TimeUnit.MILLISECONDS.toHours(milliseconds)
        milliseconds -= TimeUnit.HOURS.toMillis(hours)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds)
        milliseconds -= TimeUnit.MINUTES.toMillis(minutes)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds)
        if (!includeMillis){
            return "${ if (hours < 10)   "0" else "" }$hours:" +
                    "${ if (minutes < 10) "0" else "" }$minutes:" +
                    "${ if (seconds < 10) "0" else "" }$seconds"
        }
        milliseconds -= TimeUnit.SECONDS.toMillis(seconds)
        milliseconds /= 10
        return  "${ if (hours < 10)   "0" else "" }$hours:" +
                "${ if (minutes < 10) "0" else "" }$minutes:" +
                "${ if (seconds < 10) "0" else "" }$seconds:" +
                "${ if (milliseconds < 10) "0" else "" }$milliseconds"
    }
}