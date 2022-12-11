package com.example.runningtracker.ui

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.runningtracker.R
import com.example.runningtracker.databinding.ActivityMainBinding
import com.example.runningtracker.ui.fragment.*
import dagger.hilt.android.AndroidEntryPoint

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


        changeDestination()

    }

    private fun changeDestination(){
        val host: NavHostFragment = supportFragmentManager
            .findFragmentById(R.id.navHostFragment) as NavHostFragment? ?: return
        navController = host.navController

        binding.bottomNavigationView.setupWithNavController(navController)
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

    override fun onBackPressed() {
        val currentFragment = supportFragmentManager?.findFragmentById(R.id.navHostFragment)
            ?.childFragmentManager?.fragments?.first()
        Log.d("currentFragment",currentFragment.toString())
        when(currentFragment){
            is SplashScreenFragment ->  this.finish()
            is UserRegisterFragment ->  this.finish()
            is StepCounterFragment ->  this.finish()
            is StatisticsFragment ->  this.finish()
            is HomeFragment ->  this.finish()
            else  ->   super.onBackPressed()
        }

    }


}