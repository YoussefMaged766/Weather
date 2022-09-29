package com.example.weather.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.weather.databinding.HourItemBinding
import com.example.weather.models.HourItem
import java.text.SimpleDateFormat
import java.util.*

class HourAdapter(var items: List<HourItem>) :RecyclerView.Adapter<HourAdapter.viewholder>() {

   inner class viewholder(var binding:HourItemBinding) : ViewHolder(binding.root){

       fun bind(data:HourItem){

           binding.txtDegree.text = data.tempC?.toInt().toString()
           Glide.with(binding.root).load("http:"+data.condition?.icon).into(binding.imgIcon)
           binding.txtWeather.text =data.condition?.text
           dateFormat(binding.txtDate,data.time!!)
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