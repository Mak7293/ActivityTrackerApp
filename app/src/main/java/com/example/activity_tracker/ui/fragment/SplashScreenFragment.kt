package com.example.activity_tracker.ui.fragment

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.activity_tracker.R
import com.example.activity_tracker.databinding.FragmentSplashScreenBinding
import kotlinx.coroutines.delay


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
            delay(2000L)
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
