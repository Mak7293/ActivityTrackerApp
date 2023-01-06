package com.example.runningtracker.ui.fragment

import android.content.SharedPreferences
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.transition.ChangeBounds
import android.transition.TransitionManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.view.animation.AnticipateOvershootInterpolator
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.runningtracker.R
import com.example.runningtracker.databinding.FragmentHomeBinding
import com.example.runningtracker.db.RunningEntity
import com.example.runningtracker.ui.MainActivity
import com.example.runningtracker.ui.view_model.StatisticsViewModel
import com.example.runningtracker.util.Constants
import com.example.runningtracker.util.Theme
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.time.Duration.Companion.days


@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var binding: FragmentHomeBinding? = null

    @Inject
    lateinit var sharedPref: SharedPreferences

    @Inject
    lateinit var sdf: SimpleDateFormat

    private val statisticsViewModel: StatisticsViewModel by viewModels()

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

        Theme.setUpHomeFragmentUi(requireActivity(),binding!!)
        setUiTextView()
        Log.d("backstack",fragmentManager?.backStackEntryCount.toString())
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
        binding?.ibEditStep?.setOnClickListener {
            showEditText(binding!!.llStep,binding!!.tvStep,
                binding!!.llEtStep,binding!!.etStep,Constants.KEY_STEPS)
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
        binding?.ibSaveStep?.setOnClickListener {
            hideEditText(binding!!.llStep,binding!!.tvStep,binding!!.llEtStep,
                binding!!.etStep,Constants.KEY_STEPS)
        }
        binding?.themeRadioGroup?.setOnCheckedChangeListener {  group , checkId ->
            onRadioThemeButtonClicked(checkId)
        }
        calculateBurnedCaloriesPerDay(statisticsViewModel)
    }
    private fun setUiTextView(){
        binding?.tvName?.text =
            "Name: " +sharedPref.getString(Constants.KEY_NAME,"")
        binding?.tvWeight?.text =
            "Weight: " +sharedPref.getFloat(Constants.KEY_WEIGHT,0F).toString()+" Kg"
        binding?.tvHeight?.text =
            "Height: " +sharedPref.getInt(Constants.KEY_HEIGHT,0).toString()+" Cm"
        binding?.tvAge?.text =
            "Age: " +sharedPref.getInt(Constants.KEY_AGE,0).toString()+" Years Old"
        binding?.tvStep?.text =
            "Daily Steps: " +sharedPref.getInt(Constants.KEY_STEPS,0).toString()+" steps"
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
                Constants.KEY_STEPS   ->  etView.setText(sharedPref.getInt(key,0).toString())
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
                Constants.KEY_STEPS     -> {
                    tvView.text =
                        "Daily Steps: " + sharedPref.getInt(Constants.KEY_STEPS, 0)
                            .toString() + " steps"
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
                Constants.KEY_STEPS    -> {
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
    private fun calculateBurnedCaloriesPerDay(vm: StatisticsViewModel){
        val dates = mutableSetOf<Date>()
        for (i in MainActivity.run) {
            if(dates.size <= 10) {
                i.date?.let {
                    dates.add(it)
                }
            }
        }
        lifecycleScope.launch(Dispatchers.IO) {
            val runningList = mutableListOf<Pair<Date,List<RunningEntity>>>()
            for (i in dates) {
                val list = vm.getTotalActivityInSpecificDay(i)
                runningList.add(Pair(i,list))
            }
            val caloriesPerDay = mutableListOf<Pair<Date,Double>>()
            for(i in runningList){
                var calories = 0.0
                for(j in i.second){
                    calories += j.caloriesBurned
                }
                caloriesPerDay.add(Pair(i.first,calories))
            }
            withContext(Dispatchers.Main){
                Log.d("caloriesBurn",caloriesPerDay.toString())
                if(!caloriesPerDay.isNullOrEmpty()) {
                    binding?.chartCaloriesBurned?.visibility = View.VISIBLE
                    binding?.tvNoData?.visibility = View.GONE
                    createGraph(caloriesPerDay)
                }else{
                    binding?.chartCaloriesBurned?.visibility = View.INVISIBLE
                    binding?.tvNoData?.visibility = View.VISIBLE
                }
            }
        }
    }
    private fun createGraph(caloriesPerDay: MutableList<Pair<Date,Double>>){
        val currentNightMode = requireActivity().resources.configuration
            .uiMode and Configuration.UI_MODE_NIGHT_MASK
        Log.d("calories",caloriesPerDay.toString())
        val barEntryList = caloriesPerDay.indices.map{  i  ->
            BarEntry(i.toFloat(), caloriesPerDay[i].second.toFloat())
        }
        val xAxisFormatter = DayAxisValueFormatter(caloriesPerDay)
        binding?.chartCaloriesBurned?.xAxis.apply {
            this?.position = XAxis.XAxisPosition.BOTTOM
            this?.setDrawGridLines(false)
            this?.granularity = 1f
            this?.labelRotationAngle = 30.0f
            this?.valueFormatter = xAxisFormatter
            if(currentNightMode == Configuration.UI_MODE_NIGHT_NO){
                this?.textColor = ContextCompat.getColor(requireContext(),R.color.dark_0)
                this?.axisLineColor = ContextCompat.getColor(requireContext(),R.color.dark_1)
            }else{
                this?.textColor = ContextCompat.getColor(requireContext(),R.color.dark_7)
                this?.axisLineColor = ContextCompat.getColor(requireContext(),R.color.dark_5)
            }
        }
        binding?.chartCaloriesBurned?.axisLeft.apply {
            this?.setDrawGridLines(true)
            if(currentNightMode == Configuration.UI_MODE_NIGHT_NO){
                this?.textColor = ContextCompat.getColor(requireContext(),R.color.dark_0)
                this?.axisLineColor = ContextCompat.getColor(requireContext(),R.color.dark_1)
            }else{
                this?.textColor = ContextCompat.getColor(requireContext(),R.color.dark_7)
                this?.axisLineColor = ContextCompat.getColor(requireContext(),R.color.dark_5)
            }
        }
        binding?.chartCaloriesBurned?.axisRight.apply {
            this?.axisLineColor = Color.TRANSPARENT
            this?.textColor = Color.TRANSPARENT
            this?.setDrawGridLines(false)
        }
        val barDataSet: BarDataSet = if(currentNightMode == Configuration.UI_MODE_NIGHT_NO){
            BarDataSet(barEntryList, "Calories Burned").apply {

                valueTextColor = ContextCompat.getColor(requireContext(),R.color.dark_0)
                valueTextSize = 10.0f
                color = ContextCompat.getColor(requireContext(),R.color.green_primary_color)
            }
        }else{
            BarDataSet(barEntryList, "Calories Burned").apply {
                valueTextColor = ContextCompat.getColor(requireContext(),R.color.dark_7)
                valueTextSize = 10.0f
                color = ContextCompat.getColor(requireContext(),R.color.yellow_primary_color)
            }
        }
        binding?.chartCaloriesBurned?.data = BarData(barDataSet)
        if(currentNightMode == Configuration.UI_MODE_NIGHT_NO){
            binding?.chartCaloriesBurned?.legend?.textColor =
                ContextCompat.getColor(requireContext(),R.color.dark_0)
        }else{
            binding?.chartCaloriesBurned?.legend?.textColor =
                ContextCompat.getColor(requireContext(),R.color.dark_7)
        }

        binding?.chartCaloriesBurned?.invalidate()
    }
    inner class DayAxisValueFormatter(private val list: MutableList<Pair<Date,Double>>): ValueFormatter() {
        override fun getFormattedValue(value: Float): String {
            return if(value - value.toInt() == 0.0f){
                val date = list[value.toInt()].first
                sdf.format(date)
            }else{
                ""
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}