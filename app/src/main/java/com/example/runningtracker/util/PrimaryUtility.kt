package com.example.runningtracker.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import androidx.core.content.ContextCompat
import com.example.runningtracker.model.path.Polyline
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.concurrent.TimeUnit

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
    fun getFormattedAvgSpeed(distanceInMeter: Double, timeRun: Long): String {
        val df = DecimalFormat("##.##")
        df.roundingMode = RoundingMode.CEILING
        return "${df.format((distanceInMeter / (timeRun / 1000)) * 3.6).toDouble()} km/hr"
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