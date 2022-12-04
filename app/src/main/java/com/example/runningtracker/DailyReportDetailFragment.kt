package com.example.runningtracker

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.runningtracker.databinding.FragmentDailyReportDetailBinding


class DailyReportDetailFragment : Fragment() {

    private var binding: FragmentDailyReportDetailBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDailyReportDetailBinding.inflate(layoutInflater)
        return binding?.root


    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }


}