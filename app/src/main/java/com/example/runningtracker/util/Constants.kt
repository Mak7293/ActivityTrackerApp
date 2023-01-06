package com.example.runningtracker.util

object Constants {
    const val ACTION_SAVE_STEPS_TO_DATABASE = "actionSaveStepsToDatabase"
    const val ACTION_START_OR_RESUME_TRACKING_SERVICE = "actionStartOrResumeTrackingService"
    const val ACTION_PAUSE_TRACKING_SERVICE = "actionPauseTrackingService"
    const val ACTION_STOP_TRACKING_SERVICE = "actionStopTrackingService"
    const val ACTION_SHOW_TRACKING_FRAGMENT = "actionShowTrackingFragment"
    const val ACTION_SHOW_STEP_COUNTER_FRAGMENT = "actionShowStepCounterFragment"

    const val SHARED_PREFERENCES_NAME = "shared_preferences"
    const val KEY_NAME = "key_name"
    const val KEY_HEIGHT = "key_height"
    const val KEY_AGE = "key_age"
    const val KEY_WEIGHT = "key_weight"
    const val KEY_STEPS = "key_steps"
    const val KEY_FIRST_TIME_TOGGLE = "key_first_time_toggle"

    const val ACTIVITY_RUN_OR_WALK = "activity_run_or_walk"
    const val ACTIVITY_CYCLING = "activity_bicycling"
    const val ACTIVITY_STEPS = "activity_counting_steps"
    const val Activity_Type = "Activity_type"

    const val ACTION_STOP_RUN = "action_stop_run"
    const val ACTION_RESET_RUN = "action_reset_run"
    const val ACTION_DELETE_RUN = "action_delete_run"
    const val ACTION_STOP_ALL_SERVICE = "action_stop_all_service"

    const val RUNNING_DATABASE_NAME = "running_db"

    const val TIMER_UPDATE_INTERVAL = 100L
    const val LOCATION_UPDATE_INTERVAL = 3000L
    const val FASTEST_LOCATION_INTERVAL = 1000L

    const val STATE_UP = "up"
    const val STATE_DOWN = "down"

    const val THEME_DAY = "themeDay"
    const val THEME_NIGHT = "themeNight"
    const val THEME_DEFAULT = "themeDefault"
    const val THEME_KEY = "themeKey"

    const val MAP_ZOOM = 18.5

    const val NOTIFICATION_CHANNEL_ID_TRACKING = "tracking_channel"
    const val NOTIFICATION_CHANNEL_NAME_TRACKING= "tracking"
    const val NOTIFICATION_ID_TRACKING= 1

    const val NOTIFICATION_CHANNEL_ID_STEP_COUNTING = "step_counting_channel"
    const val NOTIFICATION_CHANNEL_NAME_STEP_COUNTING= "step_counting"
    const val NOTIFICATION_ID_STEP_COUNTING= 2

    const val SLIDE_LEFT = "slide_left"
    const val SLIDE_RIGHT = "slide_right"
    const val SLIDE_TOP = "slide_top"
    const val SLIDE_BOTTOM = "slide_bottom"

    const val IMAGE_DIRECTORY = "RunningTrackerDirectory"

    const val ACTION_START_COUNTING_SERVICE = "StartCountingService"
    const val ACTION_STOP_COUNTING_SERVICE = "StopCountingService"

    var currentOrientation: Int? = null
    var currentTheme: String? = null


}