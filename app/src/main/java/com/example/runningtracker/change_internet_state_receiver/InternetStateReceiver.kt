package com.example.runningtracker.change_internet_state_receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.runningtracker.databinding.FragmentTrackingBinding
import com.example.runningtracker.util.PrimaryUtility

class InternetStateReceiver(
    private val context: Context,
    private val binding: FragmentTrackingBinding): BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        checkInternetConnection()
    }
    private fun checkInternetConnection(){
        if(PrimaryUtility.checkInternetConnection(context)){
            binding.tvCheckInternetConnection.visibility = View.GONE
        }else{
            binding.tvCheckInternetConnection.visibility = View.VISIBLE
        }
    }
}