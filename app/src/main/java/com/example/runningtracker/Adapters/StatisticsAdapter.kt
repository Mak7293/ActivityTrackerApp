package com.example.runningtracker.Adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.runningtracker.databinding.StaticRecyclerItemRowBinding
import com.example.runningtracker.models.day.Day
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.round


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
        getListOfTrackingImageView(item, holder)
    }

    override fun getItemCount(): Int {
        return list.size
    }
    private fun getListOfTrackingImageView(item: Day,holder: ViewHolder){
        val runningEntity = item.day[item.day.keys.last()]!!
        val imageList: MutableList<Uri> = mutableListOf()
        for(i in runningEntity){
            if(i.runningImg != null){
                imageList.add(i.runningImg!!)
            }
        }
        when(imageList.size){
            0     ->   {
                holder.binding.ivHolder.visibility = View.VISIBLE
                holder.binding.iv1.visibility = View.INVISIBLE
                holder.binding.iv2.visibility = View.INVISIBLE
                holder.binding.iv3.visibility = View.INVISIBLE
            }
            1     ->   {
                holder.binding.ivHolder.visibility = View.VISIBLE
                holder.binding.iv1.visibility = View.VISIBLE
                holder.binding.iv1.setImageURI(imageList[0])

            }
            2     ->   {
                holder.binding.ivHolder.visibility = View.VISIBLE
                holder.binding.iv1.visibility = View.VISIBLE
                holder.binding.iv1.setImageURI(imageList[0])
                holder.binding.iv2.visibility = View.VISIBLE
                holder.binding.iv2.setImageURI(imageList[1])

            }
            else  ->   {
                holder.binding.ivHolder.visibility = View.VISIBLE
                holder.binding.iv1.visibility = View.VISIBLE
                holder.binding.iv1.setImageURI(imageList[0])
                holder.binding.iv2.visibility = View.VISIBLE
                holder.binding.iv2.setImageURI(imageList[1])
                holder.binding.iv3.visibility = View.VISIBLE
                holder.binding.iv3.setImageURI(imageList[2])
            }
        }
    }
    private fun getStringFormattedDate(date: Date): String{
        return sdf.format(Date(date.time))
    }
    private fun AllBurnedCaloriesInSpecificDay(day: Day): Float{
        var totalCalories: Float = 0.0F
        for(i in day.day.get(day.day.keys.last())!!){
            totalCalories += i.caloriesBurned.toFloat()
        }
        return (round(totalCalories*10) /10)
    }

}