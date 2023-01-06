package com.example.runningtracker.ui.fragment

import android.app.Dialog
import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import android.content.res.Configuration.ORIENTATION_PORTRAIT
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.runningtracker.Adapters.DailyReportAdapterOrientationLandscape
import com.example.runningtracker.Adapters.DailyReportAdapterOrientationPortrait
import com.example.runningtracker.R
import com.example.runningtracker.databinding.DialogLayoutBinding
import com.example.runningtracker.databinding.FragmentDailyReportDetailBinding
import com.example.runningtracker.db.RunningEntity
import com.example.runningtracker.models.day.Day
import com.example.runningtracker.services.step_counter_service.StepCountingService
import com.example.runningtracker.services.tracking_service.TrackingService
import com.example.runningtracker.ui.MainActivity
import com.example.runningtracker.ui.view_model.MainViewModel
import com.example.runningtracker.util.Constants
import com.example.runningtracker.util.NavUtils
import com.example.runningtracker.util.PrimaryUtility
import com.example.runningtracker.util.Theme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import javax.inject.Inject
import kotlin.math.round

@AndroidEntryPoint
class DailyReportDetailFragment : Fragment() {

    private var binding: FragmentDailyReportDetailBinding? = null
    private lateinit var day: Day
    private val args: DailyReportDetailFragmentArgs by navArgs()
    private var menu: Menu? = null
    private val viewModel: MainViewModel by viewModels()

    @Inject
    lateinit var sdf: SimpleDateFormat

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDailyReportDetailBinding.inflate(layoutInflater)

        return binding?.root

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("backstack",fragmentManager?.backStackEntryCount.toString())
        Theme.setUpDailyReportDetailUi(requireContext(),binding!!)
        Theme.setUpStatusBarColorForUnderApi29Dark(requireActivity())
        setHasOptionsMenu(true)
        day = args.day
        Log.d("listRunningDailyReport",day.day.second.toString())
        setUpToolbar()
        setupRecyclerView()

    }
    private fun setUpToolbar(){
        (activity as AppCompatActivity).setSupportActionBar(binding?.toolbar)
        val actionBar = (activity as AppCompatActivity).supportActionBar
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            binding?.toolbarTv?.text = "Date: ${sdf.format(day.day.first)}"
            binding?.toolbar?.setNavigationOnClickListener {
                backToStatisticsFragment()
            }
        }
    }
    private fun backToStatisticsFragment(){
        findNavController().navigate(
            R.id.action_dailyReportDetailFragment_to_statisticsFragment,
            null,
            NavUtils.navOptions(activity as MainActivity)[Constants.SLIDE_BOTTOM]
        )
    }
    private fun setupRecyclerView(){
        val totalList = day.day.second
        val list = mutableListOf<RunningEntity>()
        for (i in totalList){
            if(i.activity_type == Constants.ACTIVITY_CYCLING ||
                i.activity_type == Constants.ACTIVITY_RUN_OR_WALK ){
                list.add(i)
            }
        }
        if (list.isNotEmpty()){
            if(requireContext().resources.configuration.orientation == ORIENTATION_PORTRAIT){
                binding?.rvStatistics?.visibility = View.VISIBLE
                binding?.divider?.visibility = View.VISIBLE
                val adapter = DailyReportAdapterOrientationPortrait(list,requireContext())
                binding?.rvStatistics?.layoutManager = LinearLayoutManager(
                    requireContext(), LinearLayoutManager.HORIZONTAL,false)
                binding?.rvStatistics?.adapter = adapter
                Log.d("recyclerview",list.toString())
            }else if(requireContext().resources.configuration.orientation == ORIENTATION_LANDSCAPE){
                binding?.rvStatistics?.visibility = View.VISIBLE
                binding?.divider?.visibility = View.VISIBLE
                val adapter = DailyReportAdapterOrientationLandscape(list,requireContext())
                binding?.rvStatistics?.layoutManager = LinearLayoutManager(
                    requireContext(), LinearLayoutManager.VERTICAL,false)
                binding?.rvStatistics?.adapter = adapter
                Log.d("recyclerview",list.toString())
            }
            setupUi()
        }else{
            binding?.rvStatistics?.visibility = View.GONE
            binding?.divider?.visibility = View.GONE
            setupUi()
        }
    }
    private fun setupUi(){
        binding?.tvSteps?.text = "Steps: ${sumSteps()}"
        binding?.tvDistance?.text = "Distance by steps: ${sumDistanceBySteps()} m"
        binding?.tvTotalCalories?.text = "Total Calories Burned: ${sumTotalCaloriesBurned()} Cal"
        binding?.tvCalories?.text = "Burned calories by steps: ${sumCaloriesBySteps()} Cal"
    }
    private fun sumSteps(): Int{
        var steps = 0
        val runningList = day.day.second
        for(i in runningList){
            if(i.activity_type == Constants.ACTIVITY_STEPS){
                steps += i.stepCount!!
            }
        }
        return steps
    }
    private fun sumDistanceBySteps(): Int{
        var distance = 0
        val runningList = day.day.second
        for(i in runningList){
            if(i.activity_type == Constants.ACTIVITY_STEPS){
                distance += i.runningDistanceInMeters!!
            }
        }
        return distance
    }
    private fun sumCaloriesBySteps(): Double{
        var calories = 0.0
        val runningList = day.day.second
        for(i in runningList){
            if(i.activity_type == Constants.ACTIVITY_STEPS){
                calories += i.caloriesBurned!!
            }
        }
        return (round(calories*10))/10
    }
    private fun sumTotalCaloriesBurned(): String{
        var totalCalories = 0.0
        val runningList = day.day.second
        for(i in runningList){
            totalCalories += i.caloriesBurned
        }
        return (round(totalCalories*10)/10).toString()
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.daily_report_menu,menu)
        this.menu = menu
    }
    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        Theme.setUpMenuItemUi(requireContext(),menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.delete_day  ->   {
                if(!PrimaryUtility.isServiceRunning(requireContext(),"TrackingService") &&
                    !PrimaryUtility.isServiceRunning(requireContext(),"StepCountingService")
                ){
                    showDialog(
                        resources.getString(R.string.delete_day_header),
                        resources.getString(R.string.delete_day_content),
                        Constants.ACTION_DELETE_RUN
                    )
                }else{
                    showDialog(
                        resources.getString(R.string.stop_all_service_header),
                        resources.getString(R.string.stop_all_service_content),
                        Constants.ACTION_STOP_ALL_SERVICE
                    )
                }
            }
        }
        return super.onOptionsItemSelected(item)
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
                        Constants.ACTION_DELETE_RUN   ->   {
                            lifecycleScope.launch(Dispatchers.Main){
                                val list = day.day.second
                                for(i in list){
                                    viewModel.deleteRun(i)
                                    if(i.runningImg != null){
                                        val file = File(i.runningImg!!.path)
                                        file.delete()
                                        if(!file.exists()){
                                            Log.d("delete", "file deleted")
                                        }
                                    }
                                }
                                withContext(Dispatchers.Main){
                                    delay(500L)
                                    backToStatisticsFragment()
                                }
                            }
                            Toast.makeText(requireContext(),"day deleted!!",Toast.LENGTH_SHORT).show()
                        }
                        Constants.ACTION_STOP_ALL_SERVICE  ->   {
                            stopAllService()
                            Toast.makeText(requireContext(),
                                "All service shouting down, you can delete day now.",
                                Toast.LENGTH_SHORT).show()
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
    private fun stopAllService(){
        if(PrimaryUtility.isServiceRunning(requireContext(),
                "TrackingService")){
            PrimaryUtility.sendCommandToService(
                Constants.ACTION_STOP_TRACKING_SERVICE,
                requireContext(),
                TrackingService::class.java
            )
        }else if(PrimaryUtility.isServiceRunning(requireContext(),
                "StepCountingService")){
            PrimaryUtility.sendCommandToService(
                Constants.ACTION_STOP_COUNTING_SERVICE,
                requireContext(),
                StepCountingService::class.java
            )
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}