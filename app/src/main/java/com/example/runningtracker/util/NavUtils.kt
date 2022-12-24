package com.example.runningtracker.util

import android.app.Activity
import android.content.Context
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import com.example.runningtracker.R
import com.example.runningtracker.ui.MainActivity
import com.example.runningtracker.ui.fragment.*

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
            .build()
        val optionsSlideInBottom = NavOptions.Builder()
            .setLaunchSingleTop(true)
            .setEnterAnim(R.anim.slide_in_bottom)
            .setExitAnim(R.anim.slide_out_bottom)
            .build()
        return mapOf(Constants.SLIDE_LEFT to optionsSlideInLeft,
            Constants.SLIDE_RIGHT to optionsSlideInRight,
            Constants.SLIDE_TOP to optionsSlideInTop,
            Constants.SLIDE_BOTTOM to optionsSlideInBottom
        )
    }

}