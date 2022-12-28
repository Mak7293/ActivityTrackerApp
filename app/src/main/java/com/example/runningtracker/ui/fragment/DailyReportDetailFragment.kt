package com.example.runningtracker.ui.fragment

import android.app.Dialog
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.runningtracker.Adapters.DailyReportAdapter
import com.example.runningtracker.R
import com.example.runningtracker.databinding.CancelRunDialogBinding
import com.example.runningtracker.databinding.FragmentDailyReportDetailBinding
import com.example.runningtracker.db.RunningEntity
import com.example.runningtracker.models.day.Day
import com.example.runningtracker.ui.MainActivity
import com.example.runningtracker.ui.view_model.MainViewModel
import com.example.runningtracker.util.Constants
import com.example.runningtracker.util.NavUtils
import com.example.runningtracker.util.Theme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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

        Theme.setUpDailyReportDetailUi(requireContext(),binding!!)
        setHasOptionsMenu(true)
        day = args.day
        setUpToolbar()
        setupRecyclerView()

    }
    private fun setUpToolbar(){
        (activity as AppCompatActivity).setSupportActionBar(binding?.toolbar)
        val actionBar = (activity as AppCompatActivity).supportActionBar
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            binding?.toolbarTv?.text = "Date: ${sdf.format(day.day.values.first().first().date!!)}"
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
        val totalList = day.day[day.day.keys.last()]!!
        val list = mutableListOf<RunningEntity>()
        for (i in totalList){
            if(i.activity_type == Constants.ACTIVITY_CYCLING ||
                i.activity_type == Constants.ACTIVITY_RUN_OR_WALK ){
                list.add(i)
            }
        }
        if (list.isNotEmpty()){
            binding?.rvStatistics?.visibility = View.VISIBLE
            binding?.divider?.visibility = View.VISIBLE
            if (list.size>1){
                changeHeightOfRecyclerView()
            }
            val adapter = DailyReportAdapter(list,requireContext())
            binding?.rvStatistics?.layoutManager = LinearLayoutManager(
                requireContext(), LinearLayoutManager.HORIZONTAL,false)
            binding?.rvStatistics?.adapter = adapter
            Log.d("recyclerview",list.toString())
            setupUi()
        }else{
            binding?.rvStatistics?.visibility = View.GONE
            binding?.divider?.visibility = View.GONE
            setupUi()
        }
    }
    private fun changeHeightOfRecyclerView(){
        binding?.rvStatistics?.layoutParams?.height = 1500.toDp()
        binding?.rvStatistics?.requestLayout()
    }
    private fun Int.toDp(): Int = (this/ Resources.getSystem().displayMetrics.density).toInt()
    private fun setupUi(){
        binding?.tvSteps?.text = "Steps: ${sumSteps()}"
        binding?.tvDistance?.text = "///Not Implemented"
        binding?.tvTotalCalories?.text = "Total Calories Burned: ${sumTotalCaloriesBurned()} Cal"
        binding?.tvCalories?.text = "///Not Implemented"
    }
    private fun sumSteps(): Int{
        var steps = 0
        val runningList = day.day.get(day.day.keys.last())!!
        for(i in runningList){
            if(i.activity_type == Constants.ACTIVITY_COUNTING_STEPS){
                steps += i.stepCount
            }
        }
        return steps
    }
    private fun sumTotalCaloriesBurned(): String{
        var totalCalories = 0.0
        val runningList = day.day.get(day.day.keys.last())!!
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
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.delete_day  ->   {
                showDeleteDayDialog(
                    resources.getString(R.string.delete_day_header),
                    resources.getString(R.string.delete_day_content)
                )
            }
        }
        return super.onOptionsItemSelected(item)
    }
    private fun showDeleteDayDialog(header: String, content: String){
        val dialog: Dialog = Dialog(requireContext(),R.style.DialogTheme)
        val dialogBinding = CancelRunDialogBinding.inflate(layoutInflater)
        dialog.apply {
            setContentView(dialogBinding.root)
            setCanceledOnTouchOutside(false)
            setCancelable(false)
            dialogBinding.apply {
                tvContent.text = content
                tvHeader.text = header
                btnYes.setOnClickListener {
                    dialog.dismiss()
                    backToStatisticsFragment()
                    lifecycleScope.launch(Dispatchers.Main){
                        val list = day.day[day.day.keys.last()]!!
                        for(i in list) {
                            viewModel.deleteRun(i)
                            if(i.runningImg != null) {
                                val file = File(i.runningImg!!.path)
                                file.delete()
                            }
                        }

                    }
                    Toast.makeText(requireContext(),"delete!!",Toast.LENGTH_SHORT).show()
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