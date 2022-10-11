package com.example.weather.adapter

import android.content.Context
import android.content.SharedPreferences
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.weather.databinding.DaysItemBinding
import com.example.weather.models.DailyItem
import com.example.weather.utils.Constants
import java.text.SimpleDateFormat
import java.util.*

class DaysAdapter(var items: List<DailyItem>, var unit: String) :
    RecyclerView.Adapter<DaysAdapter.viewholder>() {
    private lateinit var sharedPreferences: SharedPreferences

    inner class viewholder(var binding: DaysItemBinding) : ViewHolder(binding.root) {

        fun bind(data: DailyItem) {
            sharedPreferences =
                binding.root.context.getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
            //visible layout
            Glide.with(binding.root).load(Constants.IMG_LINK + data.weather!![0]?.icon + ".png")
                .into(binding.icon)
            binding.txtDate.text =
                SimpleDateFormat("EEEE,dd MMM ", Locale.US).format(data.dt!! * 1000)

            binding.maxTemp.text = "${data.temp?.max?.toInt().toString()}°"
            binding.minTemp.text = "${data.temp?.min?.toInt().toString()}°"

            //invisible layout
            if (unit == "metric") {
                binding.txtWind.text = "${data.windSpeed?.toInt().toString()}m/s"
            } else if (unit=="imperial"){
                binding.txtWind.text = "${data.windSpeed?.toInt().toString()}mile/h"
            }
            binding.txtPressure.text = "${data.pressure.toString()} hPa"

            binding.txtHumidity.text = "${data.humidity.toString()}%"
            binding.txtPrecip.text = "${data.pop?.times(100)?.toInt().toString()}%"


        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewholder {
        val view = DaysItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return viewholder(view)
    }

    override fun onBindViewHolder(holder: viewholder, position: Int) {
        holder.bind(items[position])
        holder.itemView.setOnClickListener {
            if (holder.binding.hiddenLayout.visibility == View.VISIBLE) {
                TransitionManager.beginDelayedTransition(holder.binding.cardView, AutoTransition())
                holder.binding.hiddenLayout.visibility = View.GONE
            } else {
                TransitionManager.beginDelayedTransition(holder.binding.cardView, AutoTransition())
                holder.binding.hiddenLayout.visibility = View.VISIBLE
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}