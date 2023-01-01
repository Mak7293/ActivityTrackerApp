package com.example.runningtracker.ui.fragment

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer

import androidx.navigation.fragment.findNavController
import antonkozyriatskyi.circularprogressindicator.CircularProgressIndicator
import com.example.runningtracker.R
import com.example.runningtracker.databinding.FragmentStepCounterBinding
import com.example.runningtracker.services.step_counter_service.StepCountingService
import com.example.runningtracker.ui.MainActivity
import com.example.runningtracker.ui.view_model.MainViewModel
import com.example.runningtracker.util.Constants
import com.example.runningtracker.util.NavUtils
import com.example.runningtracker.util.PrimaryUtility
import com.example.runningtracker.util.Theme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class StepCounterFragment : Fragment() {

    private var binding: FragmentStepCounterBinding? = null
    private var name: String? = null

    private val viewModel: MainViewModel by viewModels()

    @Inject
    lateinit var sharedPref: SharedPreferences

    private var isCounting: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentStepCounterBinding.inflate(layoutInflater)
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        //customizeTheme()
        return binding?.root

    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        name = sharedPref.getString(Constants.KEY_NAME,"")
        setUpToolbar()
        Theme.setUpStepCounterUi(requireActivity(),binding!!)

        binding?.fab?.setOnClickListener {
            getPermission()
        }

        binding?.btnStepStartStop?.setOnClickListener {
            if(!isCounting){
                binding?.btnStepStartStop?.text = "Stop"
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q ){
                    sensorLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
                }else{
                    PrimaryUtility.sendCommandToStepCountingService(
                        Constants.ACTION_START_COUNTING_SERVICE,requireContext())
                }
            }else{
                binding?.btnStepStartStop?.text = "Start"
                PrimaryUtility.sendCommandToStepCountingService(
                    Constants.ACTION_STOP_COUNTING_SERVICE,requireContext())
            }
        }
        timeTextAdapter(0)
        observeLiveData()
    }
    private fun observeLiveData(){
        StepCountingService.isCounting.observe(viewLifecycleOwner, Observer {
            isCounting = it
            if(isCounting){
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
            val progress: Double = it.toDouble()/sharedPref.getInt(Constants.KEY_STEPS,0)
            binding?.progressBar?.setProgress(progress,1.0)
            binding?.progressBar?.setProgressTextAdapter(timeTextAdapter(it))
            setUiStatistics(it)
        })
    }
    private fun setUiStatistics(steps: Int){
        val heightInMeter = sharedPref.getInt(Constants.KEY_HEIGHT,160).toFloat()/100
        val weightInKg = sharedPref.getFloat(Constants.KEY_WEIGHT,70.0f).toInt()
        val totalDistance = PrimaryUtility.getDistanceBySteps(steps,heightInMeter)
        binding?.tvDistance?.text="Distance By Steps: $totalDistance m"
        val caloriesBurnedBySteps = PrimaryUtility.getCaloriesBurnedBySteps(steps,weightInKg)
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
            builder.setNegativeButton(resources.getString(R.string.negative)){  dialogInterface , which ->
                dialogInterface.dismiss()
            }
            create()
            setCancelable(false)
            show()
        }
    }
    private val sensorLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()){
                isGranted ->
            if(isGranted){
                PrimaryUtility.sendCommandToStepCountingService(
                    Constants.ACTION_START_COUNTING_SERVICE,requireContext())
            }else{
                Toast.makeText(requireContext(),"Permission Denied For Access To Sensor",
                    Toast.LENGTH_LONG).show()
            }
        }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}