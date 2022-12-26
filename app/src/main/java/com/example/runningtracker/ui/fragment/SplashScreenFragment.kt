package com.example.runningtracker.ui.fragment

import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.runningtracker.R
import com.example.runningtracker.databinding.FragmentSplashScreenBinding
import com.example.runningtracker.util.Constants
import kotlinx.coroutines.delay
import javax.inject.Inject


class SplashScreenFragment : Fragment() {

    private var binding: FragmentSplashScreenBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSplashScreenBinding.inflate(layoutInflater)
        // remove bar on top of screen
        requireActivity().window?.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launchWhenResumed {
            delay(1000L)
            val navOptions = NavOptions
                .Builder()
                .setPopUpTo(R.id.splashScreenFragment, true)
                .build()
            findNavController().navigate(
                R.id.action_splashScreenFragment_to_userRegisterFragment,
                savedInstanceState,
                navOptions
            )
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}
