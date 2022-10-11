package com.example.weather.ui

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.PorterDuff
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.weather.R
import com.example.weather.adapter.DaysAdapter
import com.example.weather.databinding.ActivityDaysBinding
import com.example.weather.utils.Status
import kotlinx.coroutines.launch
import java.util.*

class DaysActivity : AppCompatActivity() {

    lateinit var binding: ActivityDaysBinding
    lateinit var viewModel: MainViewModel
    lateinit var adapter: DaysAdapter
    var addresses: ArrayList<Address> = ArrayList()
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDaysBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        sharedPreferences = getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
        binding.actionBar.toolbar.setTitleTextColor(Color.BLACK)
        lifecycleScope.launch {
            showDetails()
        }

    }

    suspend fun showDetails() {
        val lat = intent.extras!!.getDouble("lat")
        val lon = intent.extras!!.getDouble("lon")
       if (sharedPreferences.getString("Temp" , "Celsius").equals("Celsius")){
           changeApiTemp("metric" , lat,lon)
       } else if (sharedPreferences.getString("Temp" , "Celsius").equals("Fahrenheit")){
           changeApiTemp("imperial" , lat, lon)
       }


    }
    suspend fun changeApiTemp(unit:String , lat:Double , lon:Double){
        viewModel.getLocationDetails(lat, lon, unit).collect { resource ->

            when (resource.status) {
                Status.SUCCESS -> {
                    resource.data.let {
                        adapter = DaysAdapter(it?.daily!! , unit)
                        binding.recyclerDays.adapter = adapter
                        binding.lottie.visibility = View.GONE

                    }
                }
                Status.LOADING -> {
                    binding.lottie.playAnimation()
                    binding.lottie.visibility = View.VISIBLE

                }
                Status.ERROR -> {
                    binding.lottie.visibility = View.GONE
                }
            }
            getAddress(lat, lon)
        }
    }

    fun getAddress(latitude: Double, longitude: Double) {
        val geocoder = Geocoder(this, Locale.getDefault())
        addresses = geocoder.getFromLocation(latitude, longitude, 1) as ArrayList<Address>
        val countery = addresses[0].countryName
        val city = addresses[0].subAdminArea
        binding.actionBar.toolbar.title = "$countery,$city"
    }
}