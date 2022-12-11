package com.example.runningtracker.ui.fragment

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.runningtracker.R
import com.example.runningtracker.databinding.FragmentSplashScreenBinding


class SplashScreenFragment : Fragment() {

    private var binding: FragmentSplashScreenBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSplashScreenBinding.inflate(layoutInflater)
        // remove bar on top of screen
        activity?.window?.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Handler(Looper.getMainLooper()).postDelayed({
            findNavController().navigate(R.id.action_splashScreenFragment_to_userRegisterFragment)

        },2500)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}
