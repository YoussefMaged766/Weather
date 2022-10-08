package com.example.weather.ui


import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.weather.R
import com.example.weather.adapter.HourAdapter
import com.example.weather.databinding.ActivityMainBinding
import com.example.weather.models.CurrentResponse
import com.example.weather.utils.Status
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {
    lateinit var viewModel: MainViewModel
    lateinit var binding: ActivityMainBinding
    lateinit var adapter: HourAdapter
    private var isopen = false
    var addresses: ArrayList<Address> = ArrayList()
    lateinit var cityName: String
    lateinit var stateName: String
    lateinit var countryName: String


    var locationListener = LocationListener {
        showDetailes(it.latitude, it.longitude)

        try {
            val geocoder = Geocoder(this, Locale.getDefault())
            addresses = geocoder.getFromLocation(it.latitude, it.longitude, 1) as ArrayList<Address>
            countryName = addresses.get(0).countryName
            stateName = addresses[0].adminArea
            cityName = addresses[0].subAdminArea

        } catch (e: Exception) {
            Log.d("onCreate1: ", e.message.toString())
        }
        Log.e("adress: ", addresses.toString())
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
        getLocation()


//         stateName= addresses[0].getAddressLine(1)
//         countryName= addresses[0].getAddressLine(2)


//        bottomSheetBehavior = BottomSheetBehavior.from(bottomsheet)


        val bottomView = findViewById<View>(R.id.linearLayout2)
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomView)
//        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        bottomSheetBehavior.peekHeight = 200


        bottomSheetBehavior.setBottomSheetCallback(object : BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
//                Log.d("onStateChanged: $",newState.toString())
                // React to state change
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // React to dragging events

                Log.d("onSlide: ", "$slideOffset")

                binding.ml.transitionToState((slideOffset * 2).toInt())


            }
        })

    }


    private fun getLocation() {

        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager?



        try {
            locationManager!!.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                0L,
                0f,
                locationListener
            )
        } catch (ex: SecurityException) {
            Toast.makeText(applicationContext, "Error in the recording!", Toast.LENGTH_SHORT)
                .show()
        }

        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSION_REQUEST_ACCESS_FINE_LOCATION
            )
            return
        }
        locationManager!!.requestLocationUpdates(
            LocationManager.NETWORK_PROVIDER,
            0L,
            0f,
            locationListener
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_ACCESS_FINE_LOCATION) {
            when (grantResults[0]) {
                PackageManager.PERMISSION_GRANTED -> getLocation()
                PackageManager.PERMISSION_DENIED -> Toast.makeText(
                    applicationContext,
                    "Permission Denied",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    companion object {
        const val PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 100
    }

    private fun initialize() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]


    }

    private fun showDetailes(latitude: Double, longitude: Double) {
        lifecycleScope.launch {
            viewModel.getLocationDetails(latitude, longitude,"metric").collect {
                it.let {
                    when (it.status) {
                        Status.SUCCESS -> {
                            binding.txtCountry.text = "$countryName, $stateName"
                            binding.actionBar.toolbar.title = cityName
                            binding.txtWeather.text = it.data?.current?.weather?.get(0)?.description
                            binding.txtTemp.text = it.data?.current?.temp?.toInt().toString()
                            binding.txtFeelLike.text =
                                "Feels like ${it.data?.current?.feelsLike?.toInt()}°"
                            binding.txtDegree.visibility = View.VISIBLE
                            dateFormat()
                        }

                        Status.LOADING -> {}

                        Status.ERROR -> {
                            Toast.makeText(
                                this@MainActivity,
                                it.message.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.e("weather: ", it.message.toString())
                        }
                    }
                }
            }

            viewModel.getLocationDetails(latitude, longitude,"metric")
                .collect {
                    it.let {
                        when (it.status) {
                            Status.SUCCESS -> {
                                adapter =
                                    HourAdapter(it.data?.hourly!!)
                                binding.recyclerHours.adapter = adapter

                                convertDate(it.data.current?.sunrise!!, it.data.current.sunset!!)


                                binding.txtPrecip.text = "${it.data?.current?.visibility}"
                                binding.txtHumidity.text =
                                    "${it.data?.current?.humidity}%"

                                binding.txtWind.text =
                                    "${it.data?.current?.windSpeed?.toInt().toString()} km/h"

                                binding.txtPressure.text =
                                    "${it.data?.current?.pressure?.toString()} hPa"


                            }
                            Status.LOADING -> {
                                Log.e("showDetailes: ", it.data.toString())
                            }
                            Status.ERROR -> {
                                Toast.makeText(
                                    applicationContext,
                                    it.message.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()
                                Log.e("weather: ", it.message.toString())
//                            Log.e( "showDetailes: ",it.message.toString() )

                            }
                        }
                    }
                }
        }
    }

    private fun dateFormat() {
        val simpleDateFormat = SimpleDateFormat("EE, dd MMM ", Locale.US)
        val dateTime: String =
            simpleDateFormat.format(Calendar.getInstance().timeInMillis).toString()
        binding.txtDay.text = dateTime

    }

    fun convertDate(dateRise: Long, dateSet: Long) {

        val sunRise: String =
            SimpleDateFormat(" H:mm aa", Locale.getDefault()).format(dateRise*1000)
        val sunSet: String =
            SimpleDateFormat(" h:mm aa", Locale.US).format(dateSet*1000).toString()

        binding.txtSunSet.text = sunSet
        binding.txtSunRise.text = sunRise
    }




}