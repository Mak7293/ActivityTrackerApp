package com.example.runningtracker.ui

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.example.runningtracker.R
import com.example.runningtracker.databinding.ActivityMainBinding
import com.example.runningtracker.databinding.CancelRunDialogBinding
import com.example.runningtracker.ui.fragment.*
import com.example.runningtracker.util.Constants
import com.example.runningtracker.util.NavUtils
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController



    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNavigationView.itemIconTintList = ContextCompat
            .getColorStateList(this, R.drawable.color_state_list_resource)
        window.decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
        window.statusBarColor = ContextCompat.getColor(this, R.color.white)
        applyEnglishLanguage()
        binding.bottomNavigationView.menu.getItem(1).isChecked = true
        changeDestination()

    }
    private fun changeDestination(){

        val host: NavHostFragment = supportFragmentManager
            .findFragmentById(R.id.navHostFragment) as NavHostFragment? ?: return
        navController = host.navController

        binding.bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when(item.itemId) {
                R.id.stepCounterFragment -> {
                    when(NavUtils.getCurrentFragment(this).keys.first()){
                        is HomeFragment        -> {
                            navController.navigate(
                                R.id.stepCounterFragment, null,
                                NavUtils.navOptions(this)[Constants.SLIDE_RIGHT])
                        }
                        is StatisticsFragment  -> {
                            navController.navigate(
                                R.id.stepCounterFragment, null,
                                NavUtils.navOptions(this)[Constants.SLIDE_LEFT])
                        }
                    }
                }
                R.id.homeFragment -> {
                    when(NavUtils.getCurrentFragment(this).keys.first()){
                        is StepCounterFragment   -> {
                            navController.navigate(
                                R.id.homeFragment, null,
                                NavUtils.navOptions(this)[Constants.SLIDE_LEFT])
                        }
                        is StatisticsFragment    -> {
                            navController.navigate(
                                R.id.homeFragment, null,
                                NavUtils.navOptions(this)[Constants.SLIDE_LEFT])
                        }
                    }
                }
                R.id.statisticsFragment -> {
                    when(NavUtils.getCurrentFragment(this).keys.first()){
                        is StepCounterFragment   -> {
                            navController.navigate(
                                R.id.statisticsFragment, null,
                                NavUtils.navOptions(this)[Constants.SLIDE_RIGHT])
                        }
                        is HomeFragment    -> {
                            navController.navigate(
                                R.id.statisticsFragment, null,
                                NavUtils.navOptions(this)[Constants.SLIDE_RIGHT])
                        }
                    }
                }
            }
            true
        }
        binding.bottomNavigationView.setOnNavigationItemReselectedListener { /*NO-OP */}

        navController.addOnDestinationChangedListener(
            NavController.OnDestinationChangedListener { controller, destination, arguments ->
                when(destination.id){
                    R.id.homeFragment, R.id.stepCounterFragment, R.id.statisticsFragment ->
                        binding.bottomNavigationView.visibility = View.VISIBLE
                    else  ->  binding.bottomNavigationView.visibility = View.GONE
                }
            })
    }

    private fun showExitAppDialog(
        header: String = resources.getString(R.string.exit_app_header_dialog),
        content: String = resources.getString(R.string.exit_app_content_dialog)
    ){
        val dialog: Dialog = Dialog(this,R.style.DialogTheme)
        val dialogBinding = CancelRunDialogBinding.inflate(layoutInflater)
        dialog.apply {
            setContentView(dialogBinding.root)
            setCanceledOnTouchOutside(false)
            setCancelable(false)
            dialogBinding.apply {
                tvContent.text = content
                tvHeader.text = header
                btnYes.setOnClickListener {
                    this@MainActivity.finish()
                    dialog.dismiss()
                }
                btnNo.setOnClickListener {
                    dialog.dismiss()
                }
            }
            window?.setBackgroundDrawableResource(android.R.color.transparent)
            show()
        }
    }

    private fun applyEnglishLanguage(locale: Locale = Locale.ENGLISH){
        val config = this.resources.configuration
        val sysLocale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.locales.get(0)
        } else {
            //Legacy
            config.locale
        }
        if (sysLocale.language != locale.language) {
            Locale.setDefault(locale)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                config.setLocale(locale)
            } else {
                //Legacy
                config.locale = locale
            }
            resources.updateConfiguration(config, resources.displayMetrics)
        }
    }
    override fun onBackPressed() {
        val currentFragment = NavUtils.getCurrentFragment(this).keys.last()
        Log.d("currentFragment",currentFragment.toString())
        when(currentFragment){
            is SplashScreenFragment -> {
                showExitAppDialog()
            }
            is UserRegisterFragment ->  {
                showExitAppDialog()
            }
            is StepCounterFragment  ->  {
                showExitAppDialog()
            }
            is StatisticsFragment   ->  {
                showExitAppDialog()
            }
            is HomeFragment         ->  {
                showExitAppDialog()
            }
            is DailyReportDetailFragment  ->  {
                navController.navigate(
                    R.id.action_dailyReportDetailFragment_to_statisticsFragment,
                    null,
                    NavUtils.navOptions(this)[Constants.SLIDE_BOTTOM]
                )
            }
            is TrackingFragment  ->  {
                navController.navigate(
                    R.id.action_trackingFragment_to_stepCounterFragment,
                    null,
                    NavUtils.navOptions(this)[Constants.SLIDE_BOTTOM]
                )
            }
            else  ->   super.onBackPressed()
        }
    }
}