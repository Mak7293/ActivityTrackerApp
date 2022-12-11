package com.example.runningtracker.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.fragment.findNavController
import com.example.runningtracker.R
import com.example.runningtracker.databinding.FragmentUserRegisterBinding


class UserRegisterFragment : Fragment() {

    private var binding: FragmentUserRegisterBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserRegisterBinding.inflate(layoutInflater)

        /*
        // bring back action bar
        WindowCompat.setDecorFitsSystemWindows(requireActivity().window, true)
        WindowInsetsControllerCompat(requireActivity().window, container!!).show(
            WindowInsetsCompat.Type.systemBars())
        */

        return binding?.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }


}