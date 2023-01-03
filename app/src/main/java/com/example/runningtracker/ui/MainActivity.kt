package com.example.runningtracker.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.runningtracker.R
import com.example.runningtracker.databinding.ActivityMainBinding
import com.example.runningtracker.databinding.DialogLayoutBinding
import com.example.runningtracker.db.RunningEntity
import com.example.runningtracker.ui.fragment.*
import com.example.runningtracker.ui.view_model.StatisticsViewModel
import com.example.runningtracker.util.Constants
import com.example.runningtracker.util.NavUtils
import com.example.runningtracker.util.PrimaryUtility
import com.example.runningtracker.util.Theme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private val viewModel: StatisticsViewModel by viewModels()

    @Inject
    lateinit var sharedPref: SharedPreferences

    companion object{
        var currentTheme: MutableLiveData<String> = MutableLiveData()
        lateinit var run: List<RunningEntity>
    }

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentTheme.postValue(sharedPref.getString(Constants.THEME_KEY, Constants.THEME_DEFAULT))
        observeLiveData()
        getAllTotalActivityInSpecificDay()

        Theme.setUpUi(binding,this)

        applyEnglishLanguage()
        binding.bottomNavigationView.menu.getItem(1).isChecked = true
        changeDestination()

        if(PrimaryUtility.isServiceRunning(this,"TrackingService")){
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            navigateToTrackingFragment()
        }else if(PrimaryUtility.isServiceRunning(this,"StepCountingService")){
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            navigateToStepCountingFragment()
        }
        initializeDailySteps()
    }
    private fun observeLiveData(){
        currentTheme.observe(this) {
            Theme.setProperTheme(sharedPref,this@MainActivity,navController)
        }
    }
    private fun initializeDailySteps(){
        if(sharedPref.getInt(Constants.KEY_STEPS,0) == 0){
            sharedPref.edit().putInt(Constants.KEY_STEPS,1000).apply()
        }
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
        val dialogBinding = DialogLayoutBinding.inflate(layoutInflater)
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
    private fun getAllTotalActivityInSpecificDay(){
        lifecycleScope.launch((Dispatchers.Main)) {
            viewModel.totalActivitySortedByDate().collect{
                run = it
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        when(intent?.action){
            Constants.ACTION_SHOW_TRACKING_FRAGMENT     ->   navigateToTrackingFragment()
            Constants.ACTION_SHOW_STEP_COUNTER_FRAGMENT ->   navigateToStepCountingFragment()
        }
    }
    private fun navigateToTrackingFragment(){
        navController.navigate(R.id.action_global_trackingFragment)
    }
    private fun navigateToStepCountingFragment(){
        navController.navigate(R.id.action_global_stepCountingFragment)
    }
    override fun onBackPressed() {
        val currentFragment = NavUtils.getCurrentFragment(this).keys.last()
        Log.d("currentFragment",currentFragment.toString())
        when(currentFragment){
            is SplashScreenFragment ->  {
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