package com.example.mvvmrunningtrackerapp.di

import android.app.Application
import android.app.PendingIntent
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.runningtracker.R
import com.example.runningtracker.ui.MainActivity
import com.example.runningtracker.util.Constants
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.scopes.ServiceScoped


@Module
@InstallIn(ServiceComponent::class)
object ServiceModule {

    @ServiceScoped
    @Provides
    fun provideFusedLocationProviderClient(
        app: Application
    ): FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(app)

    @Provides
    @ServiceScoped
    fun provideMainActivityPendingIntent(
        app: Application
    ): PendingIntent = PendingIntent.getActivity(
        app,
        0,
        Intent(app, MainActivity::class.java).also {
            it.action = Constants.ACTION_SHOW_TRACKING_FRAGMENT
        },
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    @Provides
    @ServiceScoped
    fun provideBaseNotificationBuilder(
        app: Application,
        pendingIntent: PendingIntent
    ) =  NotificationCompat.Builder(
        app,Constants.NOTIFICATION_CHANNEL_ID)
        .setAutoCancel(false)
        .setOngoing(true)
        .setSmallIcon(R.drawable.ic_directions_run_black_24dp)
        .setContentTitle("Running App")
        .setContentText("00:00:00")
        .setContentIntent(pendingIntent)
}