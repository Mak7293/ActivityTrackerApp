package com.example.runningtracker.ui.fragment

import android.Manifest.permission.*
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.example.runningtracker.R
import com.example.runningtracker.databinding.FragmentTrackingBinding


class TrackingFragment : Fragment() {

    private var binding: FragmentTrackingBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentTrackingBinding.inflate(layoutInflater)
        return binding?.root

    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        accessBackgroundLocation.launch(ACCESS_BACKGROUND_LOCATION)

        setUpToolbar()
    }
    private val accessBackgroundLocation: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()){
                isGranted ->
            if(isGranted){
                Toast.makeText(requireContext(),"Permission Granted" +
                        "For access background location", Toast.LENGTH_LONG).show()
            }else{
                findNavController().navigate(R.id.action_trackingFragment_to_stepCounterFragment)
                Toast.makeText(requireContext(),"Please allow all time access to this app " +
                        "for proper tracking of your activity", Toast.LENGTH_LONG).show()
            }
        }
    private fun setUpToolbar(){
        (activity as AppCompatActivity).setSupportActionBar(binding?.toolbar)
        val actionBar = (activity as AppCompatActivity).supportActionBar
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            binding?.toolbar?.setNavigationIcon(ContextCompat.getDrawable(
                requireContext(), R.drawable.ic_back
            ))
            binding?.toolbar?.setNavigationOnClickListener {
                findNavController().navigate(R.id.action_trackingFragment_to_stepCounterFragment)

            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}