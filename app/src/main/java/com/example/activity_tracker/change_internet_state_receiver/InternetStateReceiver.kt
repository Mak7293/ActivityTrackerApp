package com.example.activity_tracker.change_internet_state_receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.view.View
import com.example.activity_tracker.databinding.FragmentTrackingBinding
import com.example.activity_tracker.util.PrimaryUtility

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