package com.example.activity_tracker.ui.view_model

import androidx.lifecycle.ViewModel
import com.example.activity_tracker.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.*
import javax.inject.Inject


@HiltViewModel
class StatisticsViewModel@Inject constructor(
    private val mainRepository: MainRepository
):ViewModel() {


    fun totalActivitySortedByDate() = mainRepository.getAllActivitySortedByDate()

    fun getTotalActivityInSpecificDay(date: Date) = mainRepository.getAllActivityInSpecificDate(date)

}