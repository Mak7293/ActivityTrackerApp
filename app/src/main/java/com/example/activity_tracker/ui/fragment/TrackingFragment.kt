package com.example.activity_tracker.ui.fragment

import android.Manifest.permission.*
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.*
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.transition.ChangeBounds
import android.transition.TransitionManager
import android.util.Log
import android.view.*
import android.view.animation.AnticipateOvershootInterpolator
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.view.drawToBitmap
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.activity_tracker.R
import com.example.activity_tracker.databinding.FragmentTrackingBinding
import com.example.activity_tracker.services.tracking_service.TrackingService
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import androidx.lifecycle.Observer
import com.example.activity_tracker.change_internet_state_receiver.InternetStateReceiver
import com.example.activity_tracker.databinding.DialogLayoutBinding
import com.example.activity_tracker.db.RunningEntity
import com.example.activity_tracker.models.path.Polyline
import com.example.activity_tracker.test_db.TestDatabase
import com.example.activity_tracker.ui.MainActivity
import com.example.activity_tracker.ui.view_model.MainViewModel
import com.example.activity_tracker.util.*
import com.example.activity_tracker.util.Constants.currentOrientation
import dagger.hilt.android.AndroidEntryPoint
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class TrackingFragment : Fragment() {

    private var binding: FragmentTrackingBinding? = null
    private lateinit var mapController: IMapController
    private var marker: Marker? = null
    private var addPolyLines: Boolean = false
    private var constraintSet: ConstraintSet = ConstraintSet()
    private var isFirstTimeLaunch = true

    @Inject
    lateinit var sharedPref: SharedPreferences

    @Inject
    lateinit var simpleDateFormat: SimpleDateFormat

    private val viewModel: MainViewModel by viewModels()

    private var isTracking = false
    private var pathPoints = mutableListOf<Polyline>()
    private var allPolyline = mutableListOf<org.osmdroid.views.overlay.Polyline>()
    private var modalBottomSheet: MaterialBottomSheet? = null

    private var currentTimeInMillis = 0L
    private var menu: Menu? = null

    lateinit var  receiver: InternetStateReceiver

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentTrackingBinding.inflate(layoutInflater)
        Configuration.getInstance()
            .load(requireContext().applicationContext,
                activity?.getPreferences(Context.MODE_PRIVATE))

        // in fragment we need setHasOptionsMenu to true in onCreateView,
        // in fragment onCreateOptionsMenu only call when we use setHasOptionsMenu to true
        // only in activity onCreateOptionsMenu called by default
        setHasOptionsMenu(true)
        return binding?.root
    }
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpToolbar()

        binding?.osMap?.setTileSource(TileSourceFactory.MAPNIK)
        mapController = binding?.osMap?.controller!!
        binding?.osMap!!.setBuiltInZoomControls(false)
        binding?.osMap!!.setMultiTouchControls(false)
        binding?.osMap?.setTileSource(TileSourceFactory.MAPNIK)

        if(currentOrientation == null) {
            currentOrientation = requireContext().resources.configuration.orientation
        }
        // we need intialize currentOrientaiton with null value somewhere

        binding?.btnResetTracking?.setOnClickListener {
            showTrackingDialog(
                resources.getString(R.string.reset_tracking_header_dialog),
                resources.getString(R.string.reset_tracking_content_dialog),
                Constants.ACTION_RESET_RUN
            )
        }
        binding?.btnSaveToDb?.setOnClickListener {
            try{
                zoomToSeeWholeTrack()
            }catch (e: IllegalArgumentException){
                Toast.makeText(requireContext(),
                    "There is no Track to save in DataBase.",Toast.LENGTH_SHORT).show()
            }
        }
        binding?.fabPauseStart?.setOnClickListener {
            toggleRun()
        }
        /////observeLivedata
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q ) {
            accessBackgroundLocation.launch(ACCESS_BACKGROUND_LOCATION)
        }else{
            /* this line of code will be work when service is running and app is fully closed,
             but user decide to open app by clicking on notification*/
            if(!PrimaryUtility.isServiceRunning(requireContext(),"TrackingService")){
                fireBottomSheet()
            }else{
                addPolyLines = true
            }
        }
        observeLiveData()
        registerReceiver()
    }
    private fun registerReceiver() {
        receiver = InternetStateReceiver(requireContext(),binding!!)
        requireActivity().registerReceiver(receiver,
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }
    private fun getPositionMarker(): Marker { //Singelton
        if (marker == null) {
            marker = Marker(binding?.osMap)
            marker!!.title = "Here I am"
            marker!!.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            if(sharedPref.getString(Constants.Activity_Type,"") ==
                Constants.ACTIVITY_CYCLING){
                marker!!.icon = ContextCompat.getDrawable(requireContext(), R.drawable.marker_bicycle)
            }else if(sharedPref.getString(Constants.Activity_Type,"") ==
                Constants.ACTIVITY_RUN_OR_WALK){
                marker!!.icon = ContextCompat.getDrawable(requireContext(), R.drawable.marker_walk)
            }else{
                marker!!.icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_location)
            }
            binding?.osMap?.overlays?.add(marker)
        }
        return marker!!
    }
    private val accessBackgroundLocation: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()){
                isGranted ->
            if(isGranted){
                /* this line of code will be work when service is running and app is fully closed,
                but user decide to open app by clicking on notification*/
                if(!PrimaryUtility.isServiceRunning(requireContext(),"TrackingService")){
                    fireBottomSheet()
                }else{
                    addPolyLines = true
                }
            }else{
                isFirstTimeLaunch = false
                alertDialogForPermissionDenied()
                Toast.makeText(requireContext(),
                    resources.getString(R.string.all_time_location_permission),
                    Toast.LENGTH_LONG).show()
            }
        }
    private fun setUpToolbar(){
        (activity as AppCompatActivity).setSupportActionBar(binding?.toolbar)
        val actionBar = (activity as AppCompatActivity).supportActionBar
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)

            binding?.toolbar?.setNavigationOnClickListener {
                backToStepCounterFragment()


            }
        }
    }
    private fun alertDialogForPermissionDenied(){
        val builder = AlertDialog.Builder(requireContext())
        builder.apply {
            setTitle("Permission Denied")
            setMessage(resources.getString(R.string.all_time_location_permission))
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
                backToStepCounterFragment()
            }
            create()
            setCancelable(false)
            show()
        }
    }
    private fun backToStepCounterFragment(){
        currentOrientation = null
        findNavController().navigate(
            R.id.action_trackingFragment_to_stepCounterFragment,
            null,
            NavUtils.navOptions(activity as MainActivity)[Constants.SLIDE_BOTTOM]
        )
    }
    private fun observeLiveData(){
        TrackingService.isTracking.observe(viewLifecycleOwner, Observer {
            updateTracking(it)
        })
        TrackingService.pathPoints.observe(viewLifecycleOwner, Observer {
            pathPoints = it.polyLines
            addLatestPolyline()
            moveCameraToUserLocation()
            if(currentTimeInMillis > 1000L) {
                updateUiText()
            }
            //polyline survive from screen rotation
            if(currentOrientation != requireContext().resources.configuration.orientation){
                addAllPolyLines()

                currentOrientation = requireContext().resources.configuration.orientation
            }
            //polyline survive from screen reopening app
            if(addPolyLines){
                addAllPolyLines()
                addPolyLines = false
            }
        })
        TrackingService.timeRunInMillis.observe(viewLifecycleOwner, Observer {
            currentTimeInMillis = it
            val formattedTime = PrimaryUtility.getFormattedStopWatchTime(currentTimeInMillis, true)
            binding?.tvDuration?.text = formattedTime

        })
    }
    private fun fireBottomSheet(){
        if(currentOrientation == requireContext().resources.configuration.orientation){
            modalBottomSheet = MaterialBottomSheet()
            modalBottomSheet?.show(requireActivity().supportFragmentManager, MaterialBottomSheet.TAG)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateUiText(){
        val distance = PrimaryUtility.calculateDistance(pathPoints)
        val caloriesBurned = PrimaryUtility.calculateBurnedCalories(
            currentTimeInMillis,
            PrimaryUtility.calculateDistance(pathPoints),
            sharedPref.getFloat(Constants.KEY_WEIGHT,0f),
            sharedPref.getString(Constants.Activity_Type,"")!!
        )
        binding?.tvCalories?.text = "$caloriesBurned Cal"
        binding?.tvDistance?.text = PrimaryUtility.getFormattedDistance(distance)
        binding?.tvAvgSpeed?.text = "${PrimaryUtility.getAvgSpeed(distance,currentTimeInMillis)} km/hr"
    }
    private fun toggleRun(){
        if (isTracking){
            menu?.getItem(0)?.isVisible = true
            slideUp(1000)
            PrimaryUtility.sendCommandToService(
                Constants.ACTION_PAUSE_TRACKING_SERVICE,
                requireContext(),
                TrackingService::class.java)
        } else{
            slideDown(1000)
            PrimaryUtility.sendCommandToService(
                Constants.ACTION_START_OR_RESUME_TRACKING_SERVICE,
                requireContext(),
                TrackingService::class.java
            )
        }
    }
    private fun updateTracking(isTracking: Boolean){
        this.isTracking = isTracking
        if(!isTracking && currentTimeInMillis > 0L){
            binding?.fabPauseStart?.setImageDrawable(
                ContextCompat.getDrawable(requireContext(),R.drawable.ic_start))
            binding?.fabPauseStart?.imageTintList = ColorStateList
                .valueOf(Color.parseColor("#00BB09"))
        }else if (isTracking){
            binding?.fabPauseStart?.setImageDrawable(
                ContextCompat.getDrawable(requireContext(),R.drawable.ic_stop))
            binding?.fabPauseStart?.imageTintList = ColorStateList
                .valueOf(Color.parseColor("#FF0000"))
            menu?.getItem(0)?.isVisible = true
        }
    }
    private fun moveCameraToUserLocation(){
        if (pathPoints.isNotEmpty() && pathPoints.last().latLang.isNotEmpty()){

            mapController.setZoom(Constants.MAP_ZOOM)
            mapController.animateTo(pathPoints.last().latLang.last())
            getPositionMarker().position = pathPoints.last().latLang.last()
        }
    }
    private fun zoomToSeeWholeTrack(){
        val bounds: BoundingBox = BoundingBox.fromGeoPoints(pathPoints.getPoint())

        binding?.osMap?.zoomToBoundingBox(
            bounds, false,50,19.0,0L
        )
        binding?.osMap?.invalidate()
        accessReadWritePermission.launch(arrayOf(WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE))

    }
    private fun MutableList<Polyline>.getPoint(): List<GeoPoint>{
        val geoPointList: MutableList<GeoPoint> = mutableListOf()
        for(i in this.indices){
            for(l in this[i].latLang)
            geoPointList.add(l)
        }
        return geoPointList
    }
    private fun endRunAndSaveToDb(savedImageUri: Uri){
        val distanceInMeter = PrimaryUtility.calculateDistance(pathPoints).toInt()
        val avgSpeed = PrimaryUtility.getAvgSpeed(
            PrimaryUtility.calculateDistance(pathPoints),currentTimeInMillis)
        val date: Date? = PrimaryUtility.getCurrentDateInDateFormat(simpleDateFormat)
        val activityType = sharedPref.getString(Constants.Activity_Type,"")
        val caloriesBurned = PrimaryUtility.calculateBurnedCalories(
            currentTimeInMillis,
            PrimaryUtility.calculateDistance(pathPoints),
            sharedPref.getFloat(Constants.KEY_WEIGHT,0f),
            activityType!!
        )
        val run = RunningEntity(
            date = date,
            runningImg = savedImageUri,
            runningAvgSpeedKMH = avgSpeed,
            runningDistanceInMeters = distanceInMeter,
            runningTimeInMillis = currentTimeInMillis,
            activity_type = activityType,
            stepCount = null,
            caloriesBurned = caloriesBurned
        )
        viewModel.insertRun(run)
        Toast.makeText(requireContext(),"Track Was Saved to Database successfully.",
            Toast.LENGTH_LONG).show()
        stopRun()
    }
    private val accessReadWritePermission: ActivityResultLauncher<Array<String>> =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
                permissions -> permissions.entries.forEach {
            val permissionName = it.key
            val isGranted = it.value
            if (isGranted) {
                if (permissionName == WRITE_EXTERNAL_STORAGE) {
                    binding?.osMap?.addOnFirstLayoutListener { _, _, _, _, _ -> }
                    saveImageToInternalStorage(binding?.osMap?.drawToBitmap()!!)
                }
            }else{
                if (permissionName == WRITE_EXTERNAL_STORAGE) {
                    Toast.makeText(requireContext(),
                        "Permission Denied For saving tracking image to Database",
                        Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    private fun saveImageToInternalStorage(bitmap: Bitmap) {
        val wrapper = ContextWrapper(requireContext())
        var file = wrapper.getDir(Constants.IMAGE_DIRECTORY,Context.MODE_PRIVATE)
        file = File(file,"${UUID.randomUUID()}.jpg")
        try {
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream)
            stream.flush()
            stream.close()
            endRunAndSaveToDb(Uri.parse(file.absolutePath))
        }catch (e: IOException){
            e.printStackTrace()
            Log.d("errorSaveBitmap","!!!!!!!")
        }
    }
    private fun addAllPolyLines(){
        for(polyline in pathPoints){
            val line = org.osmdroid.views.overlay.Polyline()
            line.apply {
                outlinePaint.color = Color.RED
                outlinePaint.strokeWidth = 10f
                setPoints(polyline.latLang)
                allPolyline.add(this)
            }

            binding?.osMap?.overlayManager?.add(line)
        }
    }
    private fun addLatestPolyline(){
        if(pathPoints.isNotEmpty() && pathPoints.last().latLang.size > 1){
            val line = org.osmdroid.views.overlay.Polyline()
            line.apply {
                outlinePaint.color = Color.RED
                outlinePaint.strokeWidth = 10f
                setPoints(pathPoints.last().latLang)
                allPolyline.add(this)
            }
            binding?.osMap?.overlayManager?.add(line)
        }
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.cancel_run_menu,menu)
        this.menu = menu
    }
    //we can change visibility of menu item with onPrepareOptionsMenu
    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        if(currentTimeInMillis > 0L){
            this.menu?.getItem(0)?.isVisible = true
        }
        Theme.setUpMenuItemUi(requireContext(),menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.cancel_run  ->   {
                showTrackingDialog(
                    resources.getString(R.string.cancel_run_header_dialog),
                    resources.getString(R.string.cancel_run_content_dialog),
                    Constants.ACTION_STOP_RUN
                )
            }
        }
        return super.onOptionsItemSelected(item)
    }
    private fun showTrackingDialog(header: String, content: String, action: String){
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
                        Constants.ACTION_STOP_RUN  ->   stopRun()
                        Constants.ACTION_RESET_RUN ->    {
                            Toast.makeText(requireContext(),"Track was reset",
                                Toast.LENGTH_LONG).show()
                            PrimaryUtility.sendCommandToService(
                                Constants.ACTION_STOP_TRACKING_SERVICE,
                                requireContext(),
                                TrackingService::class.java
                            )
                            for(i in allPolyline) {
                                binding?.osMap?.overlayManager?.remove(i)
                            }
                            allPolyline.clear()
                            mapController.setZoom(18.5)
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
    private fun stopRun(){
        binding?.tvDuration?.text = "00:00:00:00"
        PrimaryUtility.sendCommandToService(
            Constants.ACTION_STOP_TRACKING_SERVICE,
            requireContext(),
            TrackingService::class.java
        )
        backToStepCounterFragment()
    }
    private fun slideUp(duration: Long = 500){
        constraintSet.clone(binding?.mainConstraintLayout)
        constraintSet.clear(R.id.ll_save_reset,ConstraintSet.TOP)
        constraintSet.connect(R.id.ll_save_reset,ConstraintSet.BOTTOM,R.id.main_constraint_layout,ConstraintSet.BOTTOM)
        constraintSet.clear(R.id.fab_pause_start,ConstraintSet.BOTTOM)
        constraintSet.connect(R.id.fab_pause_start,ConstraintSet.BOTTOM,R.id.ll_save_reset,ConstraintSet.TOP,30)

        val transition = ChangeBounds()
        transition.interpolator = AnticipateOvershootInterpolator(1.0f)
        transition.duration = duration
        TransitionManager.beginDelayedTransition(binding?.mainConstraintLayout, transition)

        constraintSet.applyTo(binding?.mainConstraintLayout)

    }
    private fun slideDown(duration: Long = 500) {
        constraintSet.clone(binding?.mainConstraintLayout)
        constraintSet.connect(R.id.ll_save_reset,ConstraintSet.TOP,R.id.main_constraint_layout,ConstraintSet.BOTTOM)
        constraintSet.connect(R.id.fab_pause_start,ConstraintSet.BOTTOM, R.id.main_constraint_layout,ConstraintSet.BOTTOM,30)
        constraintSet.clear(R.id.ll_save_reset,ConstraintSet.BOTTOM)

        val transition = ChangeBounds()
        transition.interpolator = AnticipateOvershootInterpolator(1.0f)
        transition.duration = duration
        TransitionManager.beginDelayedTransition(binding?.mainConstraintLayout, transition)

        constraintSet.applyTo(binding?.mainConstraintLayout)

    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onResume() {
        super.onResume()
        binding?.osMap?.onResume()
        if(ContextCompat.checkSelfPermission(requireContext(),
                ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED && !isFirstTimeLaunch
        ){
            accessBackgroundLocation.launch(ACCESS_BACKGROUND_LOCATION)
        }
    }
    override fun onPause() {
        super.onPause()
        binding?.osMap?.onPause()
    }

    override fun onDestroyView() {
        binding?.osMap?.onDetach()
        binding = null
        requireActivity().unregisterReceiver(receiver)
        super.onDestroyView()
    }
}