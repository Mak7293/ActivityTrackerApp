package com.example.activity_tracker.util

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.view.Menu
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.activity_tracker.Adapters.StatisticsAdapter
import com.example.activity_tracker.R
import com.example.activity_tracker.databinding.ActivityMainBinding
import com.example.activity_tracker.databinding.FragmentDailyReportDetailBinding
import com.example.activity_tracker.databinding.FragmentHomeBinding
import com.example.activity_tracker.databinding.FragmentStepCounterBinding
import com.example.activity_tracker.databinding.FragmentUserRegisterBinding
import com.example.activity_tracker.ui.MainActivity

object Theme {

    fun setProperTheme(sharedPref: SharedPreferences,context: Context,navController: NavController){
        when(MainActivity.currentTheme.value){
            Constants.THEME_DAY      ->  {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                saveThemeStateToSharedPref(Constants.THEME_KEY,Constants.THEME_DAY,sharedPref)
            }
            Constants.THEME_NIGHT    ->  {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                saveThemeStateToSharedPref(Constants.THEME_KEY,Constants.THEME_NIGHT,sharedPref)
            }
            Constants.THEME_DEFAULT  ->  {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                saveThemeStateToSharedPref(Constants.THEME_KEY,Constants.THEME_DEFAULT,sharedPref)
            }
        }
    }
    private fun saveThemeStateToSharedPref(
        key: String,
        value: String,
        sharedPref: SharedPreferences
    ){
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putString(key, value)
        editor.apply()
    }
    fun setUpUi(binding: ActivityMainBinding, context: Context){
        val currentNightMode = context.resources.configuration
            .uiMode and Configuration.UI_MODE_NIGHT_MASK
        if(currentNightMode == Configuration.UI_MODE_NIGHT_NO){
            binding.bottomNavigationView.itemIconTintList = ContextCompat
                .getColorStateList(context,R.color.bottom_navigation_icon_tint_day)
            binding.bottomNavigationView.itemTextColor = ContextCompat
                .getColorStateList(context,R.color.bottom_navigation_item_text_color_day)
        }else if(currentNightMode == Configuration.UI_MODE_NIGHT_YES){
            binding.bottomNavigationView.itemIconTintList = ContextCompat
                .getColorStateList(context,R.color.bottom_navigation_icon_tint_night)
            binding.bottomNavigationView.itemTextColor = ContextCompat
                .getColorStateList(context,R.color.bottom_navigation_item_text_color_night)
        }
    }
    fun setUpUserRegisterFragmentUi(context: Context,binding: FragmentUserRegisterBinding){
        val currentNightMode = context.resources.configuration
            .uiMode and Configuration.UI_MODE_NIGHT_MASK
        if(currentNightMode == Configuration.UI_MODE_NIGHT_NO){
            binding.contentLayout.background = ContextCompat
                .getDrawable(context,R.drawable.card_background_day)
        }else if(currentNightMode == Configuration.UI_MODE_NIGHT_YES){
            binding.contentLayout.background = ContextCompat
                .getDrawable(context,R.drawable.card_background_night)

        }
    }
    fun setUpStatusBarColorForUnderApi29Dark(activity: Activity){
        val currentNightMode = activity.resources.configuration
            .uiMode and Configuration.UI_MODE_NIGHT_MASK
        if(currentNightMode == Configuration.UI_MODE_NIGHT_NO){
            activity.window.statusBarColor = ContextCompat.getColor(activity, R.color.dark_4)
        }else if(currentNightMode == Configuration.UI_MODE_NIGHT_YES){
            activity.window.statusBarColor = ContextCompat.getColor(activity, R.color.dark_1)
        }
    }
    fun setUpStatusBarColorForUnderApi29Light(activity: Activity){
        val currentNightMode = activity.resources.configuration
            .uiMode and Configuration.UI_MODE_NIGHT_MASK
        if(currentNightMode == Configuration.UI_MODE_NIGHT_NO){
            activity.window.statusBarColor = ContextCompat.getColor(activity, R.color.dark_7)
        }else if(currentNightMode == Configuration.UI_MODE_NIGHT_YES){
            activity.window.statusBarColor = ContextCompat.getColor(activity, R.color.dark_0)
        }
    }
    fun setUpHomeFragmentUi(activity: Activity, binding: FragmentHomeBinding){
        val currentNightMode = activity.resources.configuration
            .uiMode and Configuration.UI_MODE_NIGHT_MASK
        if(currentNightMode == Configuration.UI_MODE_NIGHT_NO){
            binding.contentLayout.background = ContextCompat
                .getDrawable(activity,R.drawable.card_background_day)
            binding.chartLayout.background = ContextCompat
                .getDrawable(activity,R.drawable.card_background_day)
        }else if(currentNightMode == Configuration.UI_MODE_NIGHT_YES){
            binding.contentLayout.background = ContextCompat
                .getDrawable(activity,R.drawable.card_background_night)
            binding.chartLayout.background = ContextCompat
                .getDrawable(activity,R.drawable.card_background_night)
        }
        setUpStatusBarColorForUnderApi29Light(activity)

    }
    fun setUpStatisticsAdapterUi(activity: Activity, holder: StatisticsAdapter.ViewHolder){
        val currentNightMode = activity.resources.configuration
            .uiMode and Configuration.UI_MODE_NIGHT_MASK
        if(currentNightMode == Configuration.UI_MODE_NIGHT_NO){
            holder.binding.iv1.borderColor = ContextCompat
                .getColor(activity,R.color.green_primary_color)
            holder.binding.iv2.borderColor = ContextCompat
                .getColor(activity,R.color.green_primary_color)
            holder.binding.iv3.borderColor = ContextCompat
                .getColor(activity,R.color.green_primary_color)
        }else if(currentNightMode == Configuration.UI_MODE_NIGHT_YES){
            holder.binding.iv1.borderColor = ContextCompat
                .getColor(activity,R.color.yellow_primary_color)
            holder.binding.iv2.borderColor = ContextCompat
                .getColor(activity,R.color.yellow_primary_color)
            holder.binding.iv3.borderColor = ContextCompat
                .getColor(activity,R.color.yellow_primary_color)
        }
    }
    fun setUpDailyReportDetailUi(context: Context,binding: FragmentDailyReportDetailBinding){
        val currentNightMode = context.resources.configuration
            .uiMode and Configuration.UI_MODE_NIGHT_MASK
        if(currentNightMode == Configuration.UI_MODE_NIGHT_NO){
            binding.ivSteps.borderColor = ContextCompat
                .getColor(context,R.color.dark_3)
            binding.ivTotalCalories.borderColor = ContextCompat
                .getColor(context,R.color.dark_3)
            binding.ivSteps.circleBackgroundColor = ContextCompat
                .getColor(context,R.color.dark_8)
            binding.ivTotalCalories.circleBackgroundColor = ContextCompat
                .getColor(context,R.color.dark_8)
        }else if(currentNightMode == Configuration.UI_MODE_NIGHT_YES){
            binding.ivSteps.borderColor = ContextCompat
                .getColor(context,R.color.dark_5)
            binding.ivTotalCalories.borderColor = ContextCompat
                .getColor(context,R.color.dark_5)
            binding.ivSteps.circleBackgroundColor = ContextCompat
                .getColor(context,R.color.dark_3)
            binding.ivTotalCalories.circleBackgroundColor = ContextCompat
                .getColor(context,R.color.dark_3)
        }
    }
    fun setUpStepCounterUi(activity: Activity,binding: FragmentStepCounterBinding){
        val currentNightMode = activity.resources.configuration
            .uiMode and Configuration.UI_MODE_NIGHT_MASK
        if(currentNightMode == Configuration.UI_MODE_NIGHT_NO){
            binding.progressBar.dotColor = ContextCompat.getColor(
                activity,R.color.green_primary_color)
            binding.progressBar.progressColor = ContextCompat.getColor(
                activity,R.color.green_primary_color)
            binding.progressBar.textColor = ContextCompat.getColor(
                activity,R.color.green_primary_color)
        }else if(currentNightMode == Configuration.UI_MODE_NIGHT_YES){
            binding.progressBar.dotColor = ContextCompat.getColor(
                activity,R.color.yellow_primary_color)
            binding.progressBar.progressColor = ContextCompat.getColor(
                activity,R.color.yellow_primary_color)
            binding.progressBar.textColor = ContextCompat.getColor(
                activity,R.color.yellow_primary_color)
        }
        setUpStatusBarColorForUnderApi29Dark(activity)
    }
    fun setUpMenuItemUi(context: Context,menu: Menu){
        val currentNightMode = context.resources.configuration
            .uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK
        if(currentNightMode == android.content.res.Configuration.UI_MODE_NIGHT_NO){
            menu.getItem(0)?.icon?.setTint(ContextCompat.getColor(context,R.color.dark_0))
        }else if(currentNightMode == android.content.res.Configuration.UI_MODE_NIGHT_YES){
            menu.getItem(0)?.icon?.setTint(ContextCompat.getColor(context,R.color.dark_7))
        }
    }
}