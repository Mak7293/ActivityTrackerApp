package com.example.activity_tracker.ui.fragment

import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.activity_tracker.R
import com.example.activity_tracker.databinding.FragmentUserRegisterBinding
import com.example.activity_tracker.util.Constants
import com.example.activity_tracker.util.Theme
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

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P ){
            binding?.mainLayout?.background = ContextCompat.getDrawable(
                requireContext(),R.drawable.ic_background)
        }
        Theme.setUpUserRegisterFragmentUi(requireContext(),binding!!)

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
            .putInt(Constants.KEY_HEIGHT,weight.toFloat().toInt())
            .putInt(Constants.KEY_AGE,weight.toFloat().toInt())
            .putBoolean(Constants.KEY_FIRST_TIME_TOGGLE, false)
            .apply()
        return  true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }


}