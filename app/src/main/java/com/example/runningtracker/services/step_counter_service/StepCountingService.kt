package com.example.runningtracker.services.step_counter_service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.example.runningtracker.services.tracking_service.TrackingService
import com.example.runningtracker.ui.view_model.MainViewModel
import com.example.runningtracker.ui.view_model.StatisticsViewModel

import com.example.runningtracker.util.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import javax.inject.Named
import kotlin.math.pow
import kotlin.math.sqrt

@AndroidEntryPoint
class StepCountingService: LifecycleService(), SensorEventListener {

    private val TAG = "Step Counting Service"
    private var serviceKilled = false


    @Inject
    @Named("step_counter_fragment")
    lateinit var baseNotificationBuilder: NotificationCompat.Builder

    private lateinit var currentNotificationBuilder: NotificationCompat.Builder

    private var sensorManager: SensorManager? = null

    companion object{
        val steps = MutableLiveData<Int>()
        val isCounting = MutableLiveData<Boolean>()
    }
    /////////step counting variable///////
    private val mRawAccelValues = FloatArray(3)
    private val listLast20 = mutableListOf<Double>()
    private val listLast3 = mutableListOf<Double>()
    private var meanLast20: Double = 0.0
    private var meanLast3: Double = 0.0
    private var totalLast20: Double = 0.0
    private var totalLast3: Double = 0.0
    private var state: String = Constants.STATE_UP
    private var last3dAcc = 0.0
    /////////////////////


    private fun postInitialValues(){
        StepCountingService.isCounting.postValue(false)
        StepCountingService.steps.postValue(0)

    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG,"service started")

        currentNotificationBuilder = baseNotificationBuilder
        postInitialValues()

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        /*StepCountingService.isCounting.observe(this, androidx.lifecycle.Observer{
            updateNotificationStepCountingState(it)
        })*/
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        intent?.let {
            when (it.action) {
                Constants.ACTION_START_COUNTING_SERVICE -> {
                    Log.d(TAG, "Running service")
                    initializeSensor()
                    startForegroundService()
                    isCounting.postValue(true)
                }
                Constants.ACTION_STOP_COUNTING_SERVICE -> {
                    Log.d(TAG, "Stopped service")
                    killService()
                }
                else -> {

                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }
    private fun initializeSensor(){
        val stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        Log.d("sensor",stepSensor.toString())

        if (stepSensor == null) {
            Toast.makeText(applicationContext, "No sensor detected on this device",
                Toast.LENGTH_SHORT).show()
        } else {
            sensorManager?.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    private fun killService(){
        serviceKilled = true
        isCounting.postValue(false)
        sensorManager?.unregisterListener(this)
        stopForeground(true)
        stopSelf()

    }
    private fun updateNotificationStepCountingState(isCounting: Boolean){
        val intent = Intent(
                this,
                TrackingService::class.java).apply {
                action = Constants.ACTION_PAUSE_SERVICE
            }
        val pendingIntent =  PendingIntent.getService(
                this,
                1,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        /*currentNotificationBuilder.javaClass.getDeclaredField("mActions").apply {
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
        }*/
    }
    @RequiresApi(Build.VERSION_CODES.M)
    private fun startForegroundService(){
        Log.d(TAG,"Foreground service is started")
        StepCountingService.isCounting.postValue(true)

        val notificationManager = getSystemService(NOTIFICATION_SERVICE)
                as NotificationManager

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createNotificationChannel(notificationManager)
        }

        startForeground(Constants.NOTIFICATION_ID,baseNotificationBuilder.build())
        StepCountingService.steps.observe(this, androidx.lifecycle.Observer {
            if(!serviceKilled) {
                val notification = currentNotificationBuilder
                    .setContentText("${steps.value} step")
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

    override fun onSensorChanged(event: SensorEvent?) {
        if(event?.sensor?.type == Sensor.TYPE_ACCELEROMETER){
            mRawAccelValues[0] = event.values[0]
            mRawAccelValues[1] = event.values[1]
            mRawAccelValues[2] = event.values[2]

            last3dAcc = sqrt(mRawAccelValues[0].pow(2) + mRawAccelValues[1].pow(2)
                    + mRawAccelValues[2].pow(2)).toDouble()

            //Source: https://github.com/jonfroehlich/CSE590Sp2018
            if(listLast20.size < 20){
                listLast20.add(last3dAcc)
            }else{
                calculateMeanLast20()
                calculateMeanLast3()
                if((meanLast3 > meanLast20) && (meanLast3 > meanLast20 * 1.16) && state == Constants.STATE_UP){
                    var s  = steps.value
                    s = s?.plus(1)
                    steps.postValue(s!!)
                    state = Constants.STATE_DOWN
                }else if((meanLast3 < meanLast20) && (meanLast3 < meanLast20 * 0.84)){
                    state = Constants.STATE_UP
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        //TODO("Not yet implemented")
    }
    private fun calculateMeanLast20(){
        listLast20.removeAt(19)
        listLast20.add(0, last3dAcc)
        for (i in listLast20){
            totalLast20 += i
        }
        meanLast20 = (totalLast20 /20)
        totalLast20 = 0.0
    }
    private fun calculateMeanLast3(){
        listLast3.add(listLast20[0])
        listLast3.add(listLast20[1])
        listLast3.add(listLast20[2])
        for (i in listLast3){
            totalLast3 += i
        }
        meanLast3 = (totalLast3 /3)
        totalLast3 = 0.0
        listLast3.clear()
    }
}