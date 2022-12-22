package com.example.runningtracker.Adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.runningtracker.databinding.StaticRecyclerItemRowBinding
import com.example.runningtracker.models.day.Day
import java.text.SimpleDateFormat
import java.util.*



class StatisticsAdapter(
    private val list: List<Day>,
    private val sdf: SimpleDateFormat,
    private val btnListener:(day: Day) -> Unit):
    RecyclerView.Adapter<StatisticsAdapter.ViewHolder>(){

    class ViewHolder(val binding: StaticRecyclerItemRowBinding)
        :RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(StaticRecyclerItemRowBinding.inflate(
            LayoutInflater.from(parent.context),parent,false))
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.binding.tvDate.text = "Date: ${getStringFormattedDate(item.day.keys.first())}"
        holder.binding.tvCalories.text = "Calories Burned:" +
                " ${AllBurnedCaloriesInSpecificDay(item)} Cal"
        holder.binding.btnDetails.setOnClickListener {
            btnListener.invoke(item)
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }
    private fun getStringFormattedDate(date: Date): String{
        return sdf.format(Date(date.time))
    }
    private fun AllBurnedCaloriesInSpecificDay(day: Day): Float{
        var totalCalories: Float = 0.0F
        val a = day.day.get(day.day.keys.last())!!
        Log.d("adapterFirst", day.day.get(day.day.keys.first())!!.toString())
        Log.d("adapterLast", day.day.get(day.day.keys.last())!!.toString())
        for(i in day.day.get(day.day.keys.last())!!){
            totalCalories += i.caloriesBurned.toFloat()
        }
        return totalCalories
    }

}