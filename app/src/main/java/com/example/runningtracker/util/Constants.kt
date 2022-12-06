package com.example.runningtracker.util

object Constants {
    const val ACTION_START_OR_RESUME_SERVICE = "actionStartOrResumeService"
    const val ACTION_PAUSE_SERVICE = "actionPauseService"
    const val ACTION_STOP_SERVICE = "actionStopService"
    const val ACTION_SHOW_TRACKING_FRAGMENT = "actionShowTrackingFragment"

    const val TIMER_UPDATE_INTERVAL = 100L
    const val LOCATION_UPDATE_INTERVAL = 5000L
    const val FASTEST_LOCATION_INTERVAL = 2000L

    const val NOTIFICATION_CHANNEL_ID = "tracking_channel"
    const val NOTIFICATION_CHANNEL_NAME= "tracking"
    const val NOTIFICATION_ID= 1

}