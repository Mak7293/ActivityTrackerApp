package com.example.runningtracker.util

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.NavController
import com.example.runningtracker.Adapters.StatisticsAdapter
import com.example.runningtracker.R
import com.example.runningtracker.databinding.ActivityMainBinding
import com.example.runningtracker.databinding.FragmentDailyReportDetailBinding
import com.example.runningtracker.databinding.FragmentHomeBinding
import com.example.runningtracker.databinding.FragmentTrackingBinding
import com.example.runningtracker.databinding.FragmentUserRegisterBinding
import com.example.runningtracker.ui.MainActivity
import kotlinx.coroutines.MainScope

object Theme {

    fun setProperTheme(sharedPref: SharedPreferences,context: Context,navController: NavController){
        when(MainActivity.currentTheme.value){
            Constants.THEME_DAY    ->  {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                saveThemeStateToSharedPref(Constants.THEME_KEY,Constants.THEME_DAY,sharedPref)
            }
            Constants.THEME_NIGHT    -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                saveThemeStateToSharedPref(Constants.THEME_KEY,Constants.THEME_NIGHT,sharedPref)
            }
            Constants.THEME_DEFAULT    -> {
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
            binding.contentLayout?.background = ContextCompat
                .getDrawable(context,R.drawable.card_background_night)

        }
    }
    fun setUpHomeFragmentUi(context: Context,binding: FragmentHomeBinding){
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
    fun setUpStatisticsAdapterUi(context: Context, holder: StatisticsAdapter.ViewHolder){
        val currentNightMode = context.resources.configuration
            .uiMode and Configuration.UI_MODE_NIGHT_MASK
        if(currentNightMode == Configuration.UI_MODE_NIGHT_NO){
            holder.binding.iv1.borderColor = ContextCompat
                .getColor(context,R.color.green_primary_color)
            holder.binding.iv2.borderColor = ContextCompat
                .getColor(context,R.color.green_primary_color)
            holder.binding.iv3.borderColor = ContextCompat
                .getColor(context,R.color.green_primary_color)
        }else if(currentNightMode == Configuration.UI_MODE_NIGHT_YES){
            holder.binding.iv1.borderColor = ContextCompat
                .getColor(context,R.color.yellow_primary_color)
            holder.binding.iv2.borderColor = ContextCompat
                .getColor(context,R.color.yellow_primary_color)
            holder.binding.iv3.borderColor = ContextCompat
                .getColor(context,R.color.yellow_primary_color)
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

}