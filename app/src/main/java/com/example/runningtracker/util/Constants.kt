package com.example.runningtracker.util

import android.app.Activity

object Constants {
    const val ACTION_START_OR_RESUME_SERVICE = "actionStartOrResumeService"
    const val ACTION_PAUSE_SERVICE = "actionPauseService"
    const val ACTION_STOP_SERVICE = "actionStopService"
    const val ACTION_SHOW_TRACKING_FRAGMENT = "actionShowTrackingFragment"

    const val SHARED_PREFERENCES_NAME = "shared_preferences"

    const val ACTIVITY_RUN_OR_WALK = "activity_run_or_walk"
    const val ACTIVITY_BICYCLING = "activity_bicycling"
    const val Activity_Type = "Activity_type"


    const val TIMER_UPDATE_INTERVAL = 100L
    const val LOCATION_UPDATE_INTERVAL = 5000L
    const val FASTEST_LOCATION_INTERVAL = 2000L

    const val MAP_ZOOM = 18.5

    const val NOTIFICATION_CHANNEL_ID = "tracking_channel"
    const val NOTIFICATION_CHANNEL_NAME= "tracking"
    const val NOTIFICATION_ID= 1


    var currentOrientation: Int? = null

}