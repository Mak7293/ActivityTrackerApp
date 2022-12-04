package com.example.runningtracker

import android.annotation.SuppressLint
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.runningtracker.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomAppbar.background = ContextCompat.getDrawable(
            this, R.color.background)
        binding.bottomNavigationView.background = null
        binding.bottomNavigationView.menu.getItem(1).isEnabled = false

        binding.fab.setOnClickListener {
            Toast.makeText(this,
                "fab clicked", Toast.LENGTH_SHORT).show()
            Log.d("!!!!!!!!!!!!","clicked")
        }
        binding.bottomNavigationView.itemIconTintList = ContextCompat
            .getColorStateList(this, R.drawable.color_state_list_resource)
    }
}