package com.example.weather.ui

import android.graphics.Color
import android.graphics.PorterDuff
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDaysBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        lifecycleScope.launch {
            showDetails()
//            binding.actionBar.toolbar.navigationIcon
//                ?.setColorFilter(Color.BLACK, PorterDuff.Mode.MULTIPLY)
//            actionBar?.setDisplayHomeAsUpEnabled(true)
            binding.actionBar.toolbar.setTitleTextColor(Color.BLACK)
        }

    }

    suspend fun showDetails() {
        val lat = intent.extras!!.getDouble("lat")
        val lon = intent.extras!!.getDouble("lon")
        viewModel.getLocationDetails(lat, lon, "metric").collect { resource ->

            when (resource.status) {
                Status.SUCCESS -> {
                    resource.data.let {
                        adapter = DaysAdapter(it?.daily!!)
                        binding.recyclerDays.adapter = adapter

                    }
                }
                Status.LOADING -> {}
                Status.ERROR -> {}
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