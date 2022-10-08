package com.example.weather.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.weather.databinding.HourItemBinding
import com.example.weather.models.HourItem
import com.example.weather.models.HourlyItem
import com.example.weather.utils.Constants
import java.text.SimpleDateFormat
import java.util.*

class HourAdapter(var items: List<HourlyItem>) :RecyclerView.Adapter<HourAdapter.viewholder>() {

   inner class viewholder(var binding:HourItemBinding) : ViewHolder(binding.root){

       fun bind(data:HourlyItem){

           binding.txtDegree.text = data.temp?.toInt().toString()
           Glide.with(binding.root).load(Constants.IMG_LINK + data.weather!![0]?.icon+".png").into(binding.imgIcon)
           binding.txtWeather.text = data.weather[0]!!.main
           binding.txtDate.text = SimpleDateFormat("h aa" ,Locale.US).format(data.dt!!* 1000)
       }
   }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewholder {
        val binding = HourItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return viewholder(binding)
    }

    override fun onBindViewHolder(holder: viewholder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

     fun dateFormat(view: TextView , time: String){
         val formatLong = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US)
         view.text = SimpleDateFormat("h aa", Locale.US).format(formatLong.parse(time))

    }


}