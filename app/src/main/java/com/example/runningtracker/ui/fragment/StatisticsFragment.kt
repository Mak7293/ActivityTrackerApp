package com.example.runningtracker.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.runningtracker.Adapters.StatisticsAdapter
import com.example.runningtracker.databinding.FragmentStatisticsBinding
import com.example.runningtracker.db.RunningEntity
import com.example.runningtracker.models.day.Day
import com.example.runningtracker.ui.view_model.StatisticsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class StatisticsFragment : Fragment() {

    private var binding: FragmentStatisticsBinding? = null
    private val viewModel: StatisticsViewModel by viewModels()
    lateinit var run: List<RunningEntity>
    private val days = mutableListOf<Day>()

    @Inject
    lateinit var sdf: SimpleDateFormat

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentStatisticsBinding.inflate(layoutInflater)
        return binding?.root


    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.totalActivitySortedByDate().collect{
                run = it
                getMapOfActivityInSpecificDate()
            }
        }
    }
    private fun getMapOfActivityInSpecificDate() {
        val dates = mutableSetOf<Date>()
        for (i in run) {
            i.date?.let {
                dates.add(it)
            }
        }
        Log.d("date set", dates.toString())
        val datesList = dates.toMutableList()
        lifecycleScope.launch(Dispatchers.IO) {
            for (i in datesList) {
                val d = Day(mutableMapOf())
                val list = viewModel.getTotalActivityInSpecificDay(i)
                val a = mutableMapOf(i to list)
                d.day = a
                days.add(d)
            }
            Log.d("Day-ssss", days.toString())
            withContext(Dispatchers.Main){
                setupRecyclerView()
            }
        }


    }
    private fun setupRecyclerView(){
        if (days.isNotEmpty()){
            binding?.tvContent?.visibility = View.INVISIBLE
            binding?.rvStatistics?.visibility = View.VISIBLE
            val adapter = StatisticsAdapter(days, sdf
            ) { updateId, day ->
                goToDetailsFragment(updateId, day)
            }
            binding?.rvStatistics?.layoutManager = LinearLayoutManager(
                requireContext(), LinearLayoutManager.VERTICAL,false)
            binding?.rvStatistics?.adapter = adapter

        }else{
            binding?.tvContent?.visibility = View.VISIBLE
            binding?.rvStatistics?.visibility = View.INVISIBLE
        }
    }
    private fun goToDetailsFragment(id: Int, day:Day){

    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}
