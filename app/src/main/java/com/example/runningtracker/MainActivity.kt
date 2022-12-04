package com.example.runningtracker

import android.annotation.SuppressLint
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.runningtracker.databinding.ActivityMainBinding

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
        binding.bottomNavigationView.itemTextColor = ContextCompat
            .getColorStateList(this, R.drawable.color_state_list_resource)

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
                    R.id.homeFragment,R.id.stepCounterFragment,R.id.statisticsFragment  ->
                        binding.bottomNavigationView.visibility = View.VISIBLE
                    else  ->  binding.bottomNavigationView.visibility = View.GONE
                }
            })
    }

}