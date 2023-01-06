package com.example.runningtracker.ui.fragment

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import antonkozyriatskyi.circularprogressindicator.CircularProgressIndicator
import com.example.runningtracker.R
import com.example.runningtracker.databinding.DialogLayoutBinding
import com.example.runningtracker.databinding.FragmentStepCounterBinding
import com.example.runningtracker.db.RunningEntity
import com.example.runningtracker.services.step_counter_service.StepCountingService
import com.example.runningtracker.services.tracking_service.TrackingService
import com.example.runningtracker.ui.MainActivity
import com.example.runningtracker.ui.view_model.MainViewModel
import com.example.runningtracker.ui.view_model.StatisticsViewModel
import com.example.runningtracker.util.Constants
import com.example.runningtracker.util.NavUtils
import com.example.runningtracker.util.PrimaryUtility
import com.example.runningtracker.util.Theme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class StepCounterFragment : Fragment() {

    private var binding: FragmentStepCounterBinding? = null
    private var name: String? = null

    private val mainViewModel: MainViewModel by viewModels()
    private val statisticsViewModel: StatisticsViewModel by viewModels()

    @Inject
    lateinit var sharedPref: SharedPreferences
    @Inject
    lateinit var simpleDateFormat: SimpleDateFormat

    private var isCounting: Boolean = false
    private var totalDistanceBySteps: Float? = null
    private var caloriesBurnedBySteps: Float? = null
    private var currentSteps: Int = 0


    companion object{
        var previousSteps: Int = 0
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentStepCounterBinding.inflate(layoutInflater)
        previousSteps = 0
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        return binding?.root
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        name = sharedPref.getString(Constants.KEY_NAME,"")
        setUpToolbar()
        Theme.setUpStepCounterUi(requireActivity(),binding!!)

        binding?.fab?.setOnClickListener {
            if(!PrimaryUtility.isServiceRunning(requireContext(),"StepCountingService")){
                getPermission()
            }else{
                showDialog(
                    resources.getString(R.string.step_counter_Save_toDataBase_Header),
                    resources.getString(R.string.step_counter_Save_toDataBase_Content),
                    Constants.ACTION_SAVE_STEPS_TO_DATABASE
                )
            }
        }

        binding?.btnStepStartStop?.setOnClickListener {
            if(!PrimaryUtility.isServiceRunning(requireContext(), "TrackingService")){
                if (!isCounting) {
                    binding?.btnStepStartStop?.text = "Stop"
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        sensorLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
                    } else {
                        PrimaryUtility.sendCommandToService(
                            Constants.ACTION_START_COUNTING_SERVICE,
                            requireContext(),
                            StepCountingService::class.java
                        )
                    }
                } else {
                    saveStepsToDatabase(mainViewModel)
                    binding?.btnStepStartStop?.text = "Start"
                    PrimaryUtility.sendCommandToService(
                        Constants.ACTION_STOP_COUNTING_SERVICE,
                        requireContext(),
                        StepCountingService::class.java
                    )
                }
            }else{
                showDialog(
                    resources.getString(R.string.cancel_run_header_dialog_1),
                    resources.getString(R.string.cancel_run_content_dialog_1),
                    Constants.ACTION_STOP_TRACKING_SERVICE
                )
            }
        }
        if(PrimaryUtility.isServiceRunning(requireContext(),"StepCountingService")){
            currentSteps = StepCountingService.steps.value!!
            getPreviousStepsInDay(statisticsViewModel,currentSteps)
        }else{
            getPreviousStepsInDay(statisticsViewModel)
        }

        observeLiveData()
    }
    private fun saveStepsToDatabase(vm: MainViewModel){
        val date: Date? = PrimaryUtility.getCurrentDateInDateFormat(simpleDateFormat)
        if(currentSteps != 0){
            lifecycleScope.launch(Dispatchers.IO){
                val run = RunningEntity(
                    date = date,
                    runningImg = null,
                    runningAvgSpeedKMH = null,
                    runningDistanceInMeters = PrimaryUtility.getDistanceBySteps(
                        currentSteps,
                        sharedPref.getInt(Constants.KEY_HEIGHT,160).toFloat()/100).toInt(),
                    runningTimeInMillis = null,
                    activity_type = Constants.ACTIVITY_STEPS,
                    stepCount = currentSteps,
                    caloriesBurned = PrimaryUtility.getCaloriesBurnedBySteps(currentSteps,
                        sharedPref.getFloat(Constants.KEY_WEIGHT, 65.0f).toInt()).toDouble()
                )
                vm.insertRun(run)
                delay(300L)
                withContext(Dispatchers.Main){
                    getPreviousStepsInDay(statisticsViewModel)
                }
            }
        }
    }
    private fun getPreviousStepsInDay(vm: StatisticsViewModel,initialSteps: Int = 0){
        previousSteps = 0
        val currentDate = PrimaryUtility.getCurrentDateInDateFormat(simpleDateFormat)
        val dates = mutableSetOf<Date>()

        for (i in MainActivity.run) {
            i.date?.let {
                dates.add(it)
            }
        }
        if(dates.contains(currentDate)){
            lifecycleScope.launch(Dispatchers.IO) {
                val list = vm.getTotalActivityInSpecificDay(currentDate!!)
                for(i in list){
                    if(i.activity_type == Constants.ACTIVITY_STEPS){
                        previousSteps += i.stepCount!!
                    }
                }
                Log.d("runpreviousInCo",previousSteps.toString())
                withContext(Dispatchers.Main){
                    setUiStatistics(previousSteps + initialSteps)
                }
            }
        }
    }
    private fun observeLiveData(){
        StepCountingService.isCounting.observe(viewLifecycleOwner, Observer {
            isCounting = it
            Log.d("isCounting!!",isCounting.toString())
            if(it){
                binding?.btnStepStartStop?.text = "Stop"
                binding?.btnStepStartStop?.background = ContextCompat.getDrawable(
                    requireContext(),R.drawable.btn_red_drawable)
                binding?.tvAlert?.visibility = View.VISIBLE
            }else{
                binding?.btnStepStartStop?.text = "Start"
                binding?.btnStepStartStop?.background = ContextCompat.getDrawable(
                    requireContext(),R.drawable.btn_green_drawable)
                binding?.tvAlert?.visibility = View.INVISIBLE
            }
        })
        StepCountingService.steps.observe(viewLifecycleOwner, Observer {
            currentSteps = it
            if(isCounting){
                Log.d("isCounting",isCounting.toString())
                setUiStatistics(currentSteps + previousSteps)
            }
        })
    }
    private fun setUiStatistics(_steps: Int){
        Log.d("run_step",_steps.toString())
        val progress: Double = (_steps).toDouble()/sharedPref.getInt(Constants.KEY_STEPS,0)
        lifecycleScope.launch(Dispatchers.Main) {
            binding?.progressBar?.setProgress(progress,1.0)
        }
        binding?.progressBar?.setProgressTextAdapter(timeTextAdapter(_steps))
        val heightInMeter = sharedPref.getInt(Constants.KEY_HEIGHT,160).toFloat()/100
        val weightInKg = sharedPref.getFloat(Constants.KEY_WEIGHT,70.0f).toInt()
        totalDistanceBySteps = PrimaryUtility.getDistanceBySteps(_steps,heightInMeter)
        binding?.tvDistance?.text="Distance By Steps: $totalDistanceBySteps m"
        caloriesBurnedBySteps = PrimaryUtility.getCaloriesBurnedBySteps(_steps,weightInKg)
        binding?.tvCalories?.text="Calories Burned By Steps: $caloriesBurnedBySteps Cal"
    }
    private fun timeTextAdapter(steps: Int): CircularProgressIndicator.ProgressTextAdapter{
        return CircularProgressIndicator.ProgressTextAdapter { _ ->
            "${steps}/${sharedPref.getInt(Constants.KEY_STEPS,0)}" }
    }
    private fun setUpToolbar(){
        (activity as AppCompatActivity).setSupportActionBar(binding?.toolbar)
        val actionBar = (activity as AppCompatActivity).supportActionBar
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            binding?.toolbar?.navigationIcon = null
            binding?.toolbarTv?.text = "Lets go " + name + "!!"
        }
    }
    private fun getPermission(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)){
            alertDialogForPermissionDenied()
        }else{
            checkLocationPermission.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }
    private val checkLocationPermission: ActivityResultLauncher<Array<String>> =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
                permissions -> permissions.entries.forEach {
            val permissionName = it.key
            val isGranted = it.value
            if (isGranted) {
                if (permissionName == Manifest.permission.ACCESS_FINE_LOCATION) {
                    findNavController().navigate(
                        R.id.action_stepCounterFragment_to_trackingFragment,null,
                        NavUtils.navOptions(activity as MainActivity)[Constants.SLIDE_TOP]
                    )
                }
            }else{
                Toast.makeText(requireContext(),"Please confirm require location" +
                        " permission",Toast.LENGTH_LONG).show()
            }
        }
    }
    private fun alertDialogForPermissionDenied(){
        val builder = AlertDialog.Builder(requireContext())
        builder.apply {
            setTitle("Permission Denied")
            setMessage(resources.getString(R.string.permission_denied_alert_dialog))
            setIcon(R.drawable.ic_alert)
            setPositiveButton(resources.getString(R.string.activate_permission)){  dialogInterface , which ->
                dialogInterface.dismiss()
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package",requireContext().packageName,null)
                intent.data = uri
                startActivity(intent)
            }
            setNegativeButton(resources.getString(R.string.negative)){  dialogInterface , which ->
                dialogInterface.dismiss()
            }
            setCancelable(false)
            val dialog = create()
            dialog.show()
            val positiveButton: Button = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            val negativeButton: Button = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            val layoutParamsPositiveBtn: LinearLayout.LayoutParams =  positiveButton.layoutParams as LinearLayout.LayoutParams
            val layoutParamsNegativeBtn: LinearLayout.LayoutParams =  negativeButton.layoutParams as LinearLayout.LayoutParams
            layoutParamsPositiveBtn.gravity = Gravity.START
            layoutParamsNegativeBtn.gravity = Gravity.START
            positiveButton.layoutParams = layoutParamsPositiveBtn
            positiveButton.layoutParams = layoutParamsNegativeBtn

        }
    }
    private val sensorLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()){
                isGranted ->
            if(isGranted){
                PrimaryUtility.sendCommandToService(
                    Constants.ACTION_START_COUNTING_SERVICE,
                    requireContext(),
                    StepCountingService::class.java
                )
            }else{
                Toast.makeText(requireContext(),"Permission Denied For Access To Sensor",
                    Toast.LENGTH_LONG).show()
            }
        }
    private fun showDialog(header: String, content: String, action: String){
        val dialog: Dialog = Dialog(requireContext(),R.style.DialogTheme)
        val dialogBinding = DialogLayoutBinding.inflate(layoutInflater)
        dialog.apply {
            setContentView(dialogBinding.root)
            setCanceledOnTouchOutside(false)
            setCancelable(false)
            dialogBinding.apply {
                tvContent.text = content
                tvHeader.text = header
                btnYes.setOnClickListener {
                    when(action){
                        Constants.ACTION_SAVE_STEPS_TO_DATABASE   ->{
                            saveStepsToDatabase(mainViewModel)
                            PrimaryUtility.sendCommandToService(
                                Constants.ACTION_STOP_COUNTING_SERVICE,
                                requireContext(),
                                StepCountingService::class.java
                            )
                            findNavController().navigate(
                                R.id.action_stepCounterFragment_to_trackingFragment,null,
                                NavUtils.navOptions(activity as MainActivity)[Constants.SLIDE_TOP]
                            )
                        }
                        Constants.ACTION_STOP_TRACKING_SERVICE  ->   {
                            PrimaryUtility.sendCommandToService(
                                Constants.ACTION_STOP_TRACKING_SERVICE,
                                requireContext(),
                                TrackingService::class.java
                            )
                        }
                    }

                    dialog.dismiss()
                }
                btnNo.setOnClickListener {
                    dialog.dismiss()
                }
            }
            window?.setBackgroundDrawableResource(android.R.color.transparent)
            show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}