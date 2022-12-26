package com.example.runningtracker.ui.fragment

import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.transition.ChangeBounds
import android.transition.TransitionManager
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.view.animation.AnticipateOvershootInterpolator
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.example.runningtracker.R
import com.example.runningtracker.databinding.FragmentHomeBinding
import com.example.runningtracker.ui.MainActivity
import com.example.runningtracker.util.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var binding: FragmentHomeBinding? = null

    @Inject
    lateinit var sharedPref: SharedPreferences


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(layoutInflater)
        themeTypeChecked()
        return binding?.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding?.tvName?.text =
            "Name: " +sharedPref.getString(Constants.KEY_NAME,"")
        binding?.tvWeight?.text =
            "Weight: " +sharedPref.getFloat(Constants.KEY_WEIGHT,0F).toString()+" Kg"
        binding?.tvHeight?.text =
            "Height: " +sharedPref.getInt(Constants.KEY_HEIGHT,0).toString()+" Cm"
        binding?.tvAge?.text =
            "Age: " +sharedPref.getInt(Constants.KEY_AGE,0).toString()+" Years Old"

        binding?.ibEditName?.setOnClickListener {
            showEditText(binding!!.llName,binding!!.tvName,
                binding!!.llEtName,binding!!.etName,Constants.KEY_NAME)
        }
        binding?.ibEditWeight?.setOnClickListener {
            showEditText(binding!!.llWeight,binding!!.tvWeight,
                binding!!.llEtWeight,binding!!.etWeight,Constants.KEY_WEIGHT)
        }
        binding?.ibEditHeight?.setOnClickListener {
            showEditText(binding!!.llHeight,binding!!.tvHeight,
                binding!!.llEtHeight,binding!!.etHeight,Constants.KEY_HEIGHT)
        }
        binding?.ibEditAge?.setOnClickListener {
            showEditText(binding!!.llAge,binding!!.tvAge,
                binding!!.llEtAge,binding!!.etAge,Constants.KEY_AGE)
        }

        binding?.ibSaveName?.setOnClickListener {
            hideEditText(binding!!.llName,binding!!.tvName,
                binding!!.llEtName,binding!!.etName,Constants.KEY_NAME)
        }
        binding?.ibSaveWeight?.setOnClickListener {
            hideEditText(binding!!.llWeight,binding!!.tvWeight,binding!!.llEtWeight,
                binding!!.etWeight,Constants.KEY_WEIGHT)
        }
        binding?.ibSaveHeight?.setOnClickListener {
            hideEditText(binding!!.llHeight,binding!!.tvHeight,binding!!.llEtHeight,
                binding!!.etHeight,Constants.KEY_HEIGHT)
        }
        binding?.ibSaveAge?.setOnClickListener {
            hideEditText(binding!!.llAge,binding!!.tvAge,binding!!.llEtAge,
                binding!!.etAge,Constants.KEY_AGE)
        }
        binding?.themeRadioGroup?.setOnCheckedChangeListener {  group , checkId ->
            onRadioThemeButtonClicked(checkId)
        }
    }

    private fun showEditText(
        llView: LinearLayout, tvView: TextView,
        llEtView: LinearLayout, etView: EditText, key: String){
        val jobName: Job = lifecycleScope.launch(Dispatchers.Main){
            llView.layoutParams?.width = 0
            tvView.visibility = View.INVISIBLE
            val transition = ChangeBounds()
            transition.interpolator = AnticipateOvershootInterpolator(0.5f)
            transition.duration = 1000L
            TransitionManager.beginDelayedTransition(llView, transition)
            llView.requestLayout()
            delay(1200L)
        }
        lifecycleScope.launch(Dispatchers.Main) {
            jobName.join()
            llEtView.layoutParams?.width = LayoutParams.MATCH_PARENT
            val transition1 = ChangeBounds()
            transition1.interpolator = AnticipateOvershootInterpolator(0.5f)
            transition1.duration = 1000L
            TransitionManager.beginDelayedTransition(llEtView, transition1)
            llEtView.requestLayout()
            delay(1000L)
            etView.visibility = View.VISIBLE
            when(key){
                Constants.KEY_NAME    ->  etView.setText(sharedPref.getString(key,""))
                Constants.KEY_WEIGHT  ->  etView.setText(sharedPref.getFloat(key,0F).toString())
                Constants.KEY_HEIGHT  ->  etView.setText(sharedPref.getInt(key,0).toString())
                Constants.KEY_AGE     ->  etView.setText(sharedPref.getInt(key,0).toString())
            }
        }
    }
    private  fun hideEditText(
        llView: LinearLayout, tvView: TextView,
        llEtView: LinearLayout, etView: EditText, key: String){

        saveFields(etView,key)
        val jobName: Job = lifecycleScope.launch(Dispatchers.Main){
            llEtView.layoutParams?.width = 0
            etView.visibility = View.INVISIBLE
            val transition = ChangeBounds()
            transition.interpolator = AnticipateOvershootInterpolator(1.0f)
            transition.duration = 1000L
            TransitionManager.beginDelayedTransition(llEtView, transition)
            llEtView.requestLayout()
            delay(1200L)
        }
        lifecycleScope.launch(Dispatchers.Main) {
            jobName.join()
            llView.layoutParams?.width = LayoutParams.MATCH_PARENT
            val transition1 = ChangeBounds()
            transition1.interpolator = AnticipateOvershootInterpolator(1.0f)
            transition1.duration = 1000L
            TransitionManager.beginDelayedTransition(llView, transition1)
            llView.requestLayout()
            delay(1000L)
            when(key){
                Constants.KEY_NAME    -> {
                    tvView.text = "Name: " + sharedPref.getString(Constants.KEY_NAME, "")
                }
                Constants.KEY_WEIGHT  -> {
                    tvView.text = "Weight: " + sharedPref.getFloat(Constants.KEY_WEIGHT, 0F)
                        .toString() + " Kg"
                }
                Constants.KEY_HEIGHT  -> {
                    tvView.text =
                        "Height: " + sharedPref.getInt(Constants.KEY_HEIGHT, 0)
                            .toString() + " Cm"
                }
                Constants.KEY_AGE     -> {
                    tvView.text =
                        "Age: " + sharedPref.getInt(Constants.KEY_AGE, 0)
                            .toString() + " Years Old"
                }
            }
            tvView.visibility = View.VISIBLE
        }
    }
    private fun saveFields(etView: EditText, key: String){
        if(etView.text.toString().isNotEmpty()){
            when(key){
                Constants.KEY_NAME    -> {
                    sharedPref.edit().putString(key, etView.text.toString()).apply()
                }
                Constants.KEY_WEIGHT  -> {
                    sharedPref.edit().putFloat(key, etView.text.toString().toFloat()).apply()
                }
                Constants.KEY_HEIGHT  -> {
                    sharedPref.edit().putInt(key, etView.text.toString().toFloat().toInt()).apply()
                }
                Constants.KEY_AGE     -> {
                    sharedPref.edit().putInt(key, etView.text.toString().toFloat().toInt()).apply()
                }
            }
        }
    }
    private fun onRadioThemeButtonClicked(checkId: Int) {
        // Check which radio button was clicked

        when (checkId) {
            R.id.radio_btn_day     -> {
                MainActivity.currentTheme.postValue(Constants.THEME_DAY)
            }
            R.id.radio_btn_night   -> {
                MainActivity.currentTheme.postValue(Constants.THEME_NIGHT)
            }
            R.id.radio_btn_default -> {
                MainActivity.currentTheme.postValue(Constants.THEME_DEFAULT)
            }
        }
    }
    private fun themeTypeChecked(){
        when (MainActivity.currentTheme.value) {
            Constants.THEME_DEFAULT -> {
                binding?.radioBtnDefault?.isChecked = true
            }
            Constants.THEME_DAY -> {
                binding?.radioBtnDay?.isChecked = true
            }
            Constants.THEME_NIGHT -> {
                binding?.radioBtnNight?.isChecked = true
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}