package com.example.activity_tracker.di

import android.app.Application
import android.app.PendingIntent
import android.content.Intent
import android.content.res.Configuration
import androidx.core.app.NotificationCompat
import com.example.activity_tracker.R
import com.example.activity_tracker.ui.MainActivity
import com.example.activity_tracker.util.Constants
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.scopes.ServiceScoped
import javax.inject.Named


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
    @Named("tracking_fragment")
    fun provideMainActivityPendingIntentForTracking(
        app: Application
    ): PendingIntent {

        return PendingIntent.getActivity(
            app,
            0,
            Intent(app, MainActivity::class.java).also {
                it.action = Constants.ACTION_SHOW_TRACKING_FRAGMENT
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    @Provides
    @ServiceScoped
    @Named("tracking_fragment")
    fun provideBaseNotificationBuilderForTracking(
        app: Application,
        @Named("tracking_fragment")pendingIntent: PendingIntent
    ):NotificationCompat.Builder {
        val currentNightMode = app.resources.configuration
            .uiMode and Configuration.UI_MODE_NIGHT_MASK
        val icon = if(currentNightMode == Configuration.UI_MODE_NIGHT_NO){
            R.drawable.ic_notification_run_day_24dp
        }else{
            R.drawable.ic_notification_run_night_24dp
        }
        return NotificationCompat.Builder(
            app, Constants.NOTIFICATION_CHANNEL_ID_TRACKING)
            .setAutoCancel(false)
            .setOngoing(true)
            .setSmallIcon(icon)
            .setContentTitle("Running App")
            .setContentText("00:00:00")
            .setContentIntent(pendingIntent)
    }

    @Provides
    @ServiceScoped
    @Named("step_counter_fragment")
    fun provideMainActivityPendingIntentForStepCounting(
        app: Application
    ): PendingIntent {
        return PendingIntent.getActivity(
            app,
            0,
            Intent(app, MainActivity::class.java).also {
                it.action = Constants.ACTION_SHOW_STEP_COUNTER_FRAGMENT
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
    @Provides
    @ServiceScoped
    @Named("step_counter_fragment")
    fun provideBaseNotificationBuilderForStepCounting(
        app: Application,
        @Named("step_counter_fragment")pendingIntent: PendingIntent
    ):NotificationCompat.Builder {
        return NotificationCompat.Builder(
            app, Constants.NOTIFICATION_CHANNEL_ID_STEP_COUNTING)
            .setAutoCancel(false)
            .setOngoing(true)
            .setContentTitle("Step Counting")
            .setContentText("00 Steps")
            .setContentIntent(pendingIntent)
    }
}