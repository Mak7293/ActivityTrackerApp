package com.example.activity_tracker.util

import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import com.example.activity_tracker.R
import com.example.activity_tracker.ui.MainActivity
import com.example.activity_tracker.ui.fragment.*

object NavUtils {
    fun getCurrentFragment(activity: MainActivity): Map<Fragment?,Int>{
        val currentFragment = activity.supportFragmentManager.findFragmentById(R.id.navHostFragment)
            ?.childFragmentManager?.fragments?.first()
        return when(currentFragment){
            is StatisticsFragment        ->   mapOf(currentFragment to R.id.statisticsFragment)
            is StepCounterFragment       ->   mapOf(currentFragment to R.id.stepCounterFragment)
            is HomeFragment              ->   mapOf(currentFragment to R.id.homeFragment)
            is DailyReportDetailFragment ->   mapOf(currentFragment to R.id.dailyReportDetailFragment)
            is TrackingFragment          ->   mapOf(currentFragment to R.id.trackingFragment)
            else                         ->   mapOf()
        }
    }
    fun navOptions(activity: MainActivity): Map<String, NavOptions>{
        val optionsSlideInLeft = NavOptions.Builder()
            .setLaunchSingleTop(true)
            .setEnterAnim(R.anim.slide_in_right)
            .setExitAnim(R.anim.slide_out_right)
            .setPopUpTo(getCurrentFragment(activity).values.first(),true)
            .build()
        val optionsSlideInRight = NavOptions.Builder()
            .setLaunchSingleTop(true)
            .setEnterAnim(R.anim.slide_in_left)
            .setExitAnim(R.anim.slide_out_left)
            .setPopUpTo(getCurrentFragment(activity).values.first(),true)
            .build()
        val optionsSlideInTop = NavOptions.Builder()
            .setLaunchSingleTop(true)
            .setEnterAnim(R.anim.slide_in_top)
            .setExitAnim(R.anim.slide_out_top)
            .setPopEnterAnim(R.anim.slide_in_top)
            .setPopExitAnim(R.anim.slide_out_top)
            .setPopUpTo(getCurrentFragment(activity).values.first(),true)
            .build()
        val optionsSlideInBottom = NavOptions.Builder()
            .setLaunchSingleTop(true)
            .setEnterAnim(R.anim.slide_in_bottom)
            .setExitAnim(R.anim.slide_out_bottom)
            .setPopEnterAnim(R.anim.slide_in_bottom)
            .setPopExitAnim(R.anim.slide_out_bottom)
            .setPopUpTo(getCurrentFragment(activity).values.first(),true)
            .build()
        return mapOf(Constants.SLIDE_LEFT to optionsSlideInLeft,
            Constants.SLIDE_RIGHT to optionsSlideInRight,
            Constants.SLIDE_TOP to optionsSlideInTop,
            Constants.SLIDE_BOTTOM to optionsSlideInBottom
        )
    }
}