package com.example.runningtracker.services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.runningtracker.R
import com.example.runningtracker.models.path.PolyLines
import com.example.runningtracker.models.path.Polyline
import com.example.runningtracker.util.Constants
import com.example.runningtracker.util.PrimaryUtility
import com.google.android.gms.location.*
import dagger.hilt.android.AndroidEntryPoint
import org.osmdroid.util.GeoPoint
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class TrackingService: LifecycleService() {

    private val TAG = "Tracking Service"

    private var isFirstRun = true
    private var serviceKilled = false

    private var timer = Timer()

    @Inject
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    @Inject
    lateinit var baseNotificationBuilder: NotificationCompat.Builder

    lateinit var currentNotificationBuilder: NotificationCompat.Builder


    companion object{
        val timeRunInMillis = MutableLiveData<Long>()
        val isTracking = MutableLiveData<Boolean>()
        val pathPoints = MutableLiveData<PolyLines>()

    }
    private fun postInitialValues(){
        isTracking.postValue(false)
        pathPoints.postValue(PolyLines(mutableListOf(Polyline(mutableListOf()))))
        timeRunInMillis.postValue(0L)

    }

    override fun onCreate() {
        super.onCreate()

        Log.d(TAG,"service started")

        currentNotificationBuilder = baseNotificationBuilder

        postInitialValues()
        //fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        isTracking.observe(this, Observer{
            updateLocationTracking(it)
            updateNotificationTrackingState(it)
        })

    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        intent?.let {
            when(it.action){
                Constants.ACTION_START_OR_RESUME_SERVICE   ->   {
                    if (isFirstRun){
                        startForegroundService()
                        isFirstRun = false
                    }else{
                        Log.d(TAG,"Resuming service")
                        if (timeRunInMillis.value!! > 0) {
                            timer = Timer()
                            startTimer()
                        }
                    }
                }
                Constants.ACTION_PAUSE_SERVICE   ->   {
                    Log.d(TAG,"Paused service")
                    pauseService()
                }
                Constants.ACTION_STOP_SERVICE   ->   {
                    Log.d(TAG,"Stopped service")
                    killService()
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun startTimer() {

        addEmptyPolyline()
        isTracking.postValue(true)
        Log.d("!!!!!!",timeRunInMillis.value.toString())
        timer.schedule(object : TimerTask() {
            override fun run() {
                timeRunInMillis.postValue(timeRunInMillis.value?.plus(100L) ?: 100L)
            }
        }, 0, Constants.TIMER_UPDATE_INTERVAL)
    }

    private fun pauseService(){
        isTracking.postValue(false)
        timer.cancel()

    }

    private fun updateNotificationTrackingState(isTracking: Boolean){
        val notificationActionText = if(isTracking) "Pause" else "Resume"
        val pendingIntent = if (isTracking){
            val pauseIntent = Intent(
                this,
                TrackingService::class.java).apply {
                action = Constants.ACTION_PAUSE_SERVICE
            }
            PendingIntent.getService(
                this,
                1,
                pauseIntent,
                FLAG_UPDATE_CURRENT or FLAG_IMMUTABLE)
        } else{
            val resumeIntent = Intent(
                this,
                TrackingService::class.java).apply {
                action = Constants.ACTION_START_OR_RESUME_SERVICE
            }
            PendingIntent.getService(
                this,
                2,
                resumeIntent,
                FLAG_UPDATE_CURRENT or FLAG_IMMUTABLE)
        }
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        currentNotificationBuilder.javaClass.getDeclaredField("mActions").apply {
            isAccessible = true
            set(currentNotificationBuilder, ArrayList<NotificationCompat.Action>())
        }
        if(!serviceKilled) {
            currentNotificationBuilder = baseNotificationBuilder
                .addAction(R.drawable.ic_pause_black_color, notificationActionText, pendingIntent)
            notificationManager.notify(
                Constants.NOTIFICATION_ID,
                currentNotificationBuilder.build()
            )
        }
    }

    @SuppressLint("MissingPermission")
    private fun updateLocationTracking(isTracking: Boolean) {
        if(isTracking){
            if (PrimaryUtility.hasLocationPermissions(this)){
                val request = LocationRequest().apply {
                    interval = Constants.LOCATION_UPDATE_INTERVAL
                    fastestInterval = Constants.FASTEST_LOCATION_INTERVAL
                    priority = Priority.PRIORITY_HIGH_ACCURACY
                }
                fusedLocationProviderClient.requestLocationUpdates(
                    request,
                    locationCallback,
                    Looper.getMainLooper()
                )
            }
        } else{
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
    }

    val locationCallback = object: LocationCallback(){
        override fun onLocationResult(result: LocationResult) {
            super.onLocationResult(result)
            if (isTracking.value!!){
                result.locations.let{   locations ->
                    for(location in locations){
                        addPathPoint(location)
                        Log.d(TAG,"NEW LOCATION: ${location.latitude}, ${location.longitude}")
                    }
                }
            }
        }
    }
    private fun addPathPoint(location: Location?){
        location?.let {
            val pos = GeoPoint(location.latitude,location.longitude)
            pathPoints.value?.apply {
                polyLines.last().latLang.add(pos)
                pathPoints.postValue(this)
            }
        }
    }
    private fun addEmptyPolyline() = pathPoints.value?.apply {
        polyLines.add(Polyline(mutableListOf()))
        pathPoints.postValue(this)
    } ?: pathPoints.postValue(PolyLines(mutableListOf(Polyline(mutableListOf()))))

    private fun killService(){
        serviceKilled = true
        isFirstRun = true
        pauseService()
        postInitialValues()
        stopForeground(true)
        stopSelf()

    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun startForegroundService(){
        Log.d(TAG,"Foreground service is started")
        startTimer()
        isTracking.postValue(true)

        val notificationManager = getSystemService(NOTIFICATION_SERVICE)
                as NotificationManager

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createNotificationChannel(notificationManager)
        }

        startForeground(Constants.NOTIFICATION_ID,baseNotificationBuilder.build())
        timeRunInMillis.observe(this, Observer {
            if(!serviceKilled) {
                val notification = currentNotificationBuilder
                    .setContentText(PrimaryUtility.getFormattedStopWatchTime(it ,false))
                notificationManager.notify(Constants.NOTIFICATION_ID, notification.build())
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager){
        val channel = NotificationChannel(
            Constants.NOTIFICATION_CHANNEL_ID,
            Constants.NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)
    }
}