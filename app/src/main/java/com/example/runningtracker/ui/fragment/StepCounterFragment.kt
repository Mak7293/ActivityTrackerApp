package com.example.runningtracker.ui.fragment

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.runningtracker.R
import com.example.runningtracker.databinding.FragmentStepCounterBinding
import com.example.runningtracker.util.Constants
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class StepCounterFragment : Fragment() {

    private var binding: FragmentStepCounterBinding? = null
    private var name: String? = null

    @Inject
    lateinit var sharedPref: SharedPreferences


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentStepCounterBinding.inflate(layoutInflater)
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        return binding?.root
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        name = sharedPref.getString(Constants.KEY_NAME,"")
        setUpToolbar()
        binding?.fab?.setOnClickListener {
            getPermission()
        }
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
                        R.id.action_stepCounterFragment_to_trackingFragment)
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


    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}