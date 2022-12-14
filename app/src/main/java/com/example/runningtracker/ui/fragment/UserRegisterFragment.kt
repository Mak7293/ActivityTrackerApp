package com.example.runningtracker.ui.fragment

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.runningtracker.R
import com.example.runningtracker.databinding.FragmentUserRegisterBinding
import com.example.runningtracker.util.Constants
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class UserRegisterFragment : Fragment() {

    private var binding: FragmentUserRegisterBinding? = null

    @Inject
    lateinit var sharedPref: SharedPreferences
    private var isFirstTime = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserRegisterBinding.inflate(layoutInflater)

        return binding?.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        isFirstTime = sharedPref.getBoolean(Constants.KEY_FIRST_TIME_TOGGLE,true)
        if (!isFirstTime){
            /*
            we need pop setupFragment in app back stack ,the user should not be able
            to return to setup fragment after going to setup fragment
            */
            val navOptions = NavOptions
                .Builder()
                .setPopUpTo(R.id.userRegisterFragment, true)
                .build()
            findNavController().navigate(
                R.id.action_userRegisterFragment_to_stepCounterFragment,
                savedInstanceState,
                navOptions
            )
        }
        binding?.btnSaveUserDetails?.setOnClickListener {
            val success = writePersonalDataToSharedPref()
            if(success) {
                val navOptions = NavOptions
                    .Builder()
                    .setPopUpTo(R.id.userRegisterFragment, true)
                    .build()
                findNavController().navigate(
                    R.id.action_userRegisterFragment_to_stepCounterFragment,
                    savedInstanceState,
                    navOptions
                )
            } else{
                Snackbar.make(requireView(),"Please Enter all the fields, we need this" +
                        " data to calculate burned calories", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun writePersonalDataToSharedPref(): Boolean{
        val name = binding?.etName?.text.toString()
        val weight = binding?.etWeight?.text.toString()
        val height = binding?.etHeight?.text.toString()
        val age = binding?.etAge?.text.toString()
        if(name.isEmpty() || weight.isEmpty() || height.isEmpty() || age.isEmpty()){
            return false
        }
        sharedPref
            .edit()
            .putString(Constants.KEY_NAME, name)
            .putFloat(Constants.KEY_WEIGHT, weight.toFloat())
            .putInt(Constants.KEY_HEIGHT,weight.toInt())
            .putInt(Constants.KEY_AGE,weight.toInt())
            .putBoolean(Constants.KEY_FIRST_TIME_TOGGLE, false)
            .apply()
        return  true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }


}