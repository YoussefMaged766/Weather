package com.example.weather.ui




import android.content.pm.PackageManager
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.weather.R
import com.example.weather.adapter.HourAdapter
import com.example.weather.databinding.ActivityMainBinding
import com.example.weather.utils.Status
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {
    lateinit var viewModel: MainViewModel
    lateinit var binding: ActivityMainBinding
    lateinit var adapter: HourAdapter



    var locationListener = LocationListener {
        showDetailes(it.latitude, it.longitude)

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
        getLocation()





//        bottomSheetBehavior = BottomSheetBehavior.from(bottomsheet)


        val bottomView = findViewById<View>(R.id.linearLayout2)
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomView)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        bottomSheetBehavior.peekHeight = 200
//ليهم لزمه دلوقتى؟
//        val colayout = findViewById<View>(R.id.constraintLayout2) as CoordinatorLayout
//        val bottomSheet = colayout.findViewById<View>(R.id.linearLayout2)
//        val behavior: BottomSheetBehavior<*> = BottomSheetBehavior.from(bottomSheet)
//        behavior.setBottomSheetCallback(object : BottomSheetCallback() {
//            override fun onStateChanged(bottomSheet: View, newState: Int) {
//                // React to state change
//            }
//
//            override fun onSlide(bottomSheet: View, slideOffset: Float) {
//                // React to dragging events
//            }
//        })

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
            viewModel.getLocationDetails(latitude, longitude).observe(this@MainActivity, Observer {
                it.let {
                    when (it.status) {
                        Status.SUCCESS -> {
                            binding.txtCountry.text =
                                "${it.data?.location?.country}, ${it.data?.location?.region}"
                            binding.actionBar.toolbar.title = it.data?.location?.name
                            binding.txtWeather.text = it.data?.current?.condition?.text
                            binding.txtTemp.text = it.data?.current?.tempC?.toInt().toString()
                            binding.txtFeelLike.text =
                                "Feels like ${it.data?.current?.feelslikeC?.toInt()}°"
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
                            Log.e("error: ", it.message.toString())
                        }
                    }
                }
            })

            viewModel.getForecastDetails(latitude, longitude, 7)
                .observe(this@MainActivity, Observer {
                    it.let {
                        when (it.status) {
                            Status.SUCCESS -> {
                                adapter =
                                    HourAdapter(it.data?.forecast?.forecastday?.get(0)?.hour!!)
                                binding.recyclerHours.adapter = adapter

                                binding.txtSunRise.text =
                                    it.data.forecast.forecastday[0].astro?.sunrise
                                binding.txtSunSet.text =
                                    it.data.forecast.forecastday[0].astro?.sunset
                                binding.txtPrecip.text =
                                    "${it.data.forecast.forecastday[0].day?.totalprecipMm?.toInt()}%"
                                binding.txtHumidity.text =
                                    "${it.data.forecast.forecastday[0].hour?.get(0)?.humidity}%"

                                binding.txtWind.text =
                                    "${
                                        it.data.forecast.forecastday[0].hour?.get(0)?.windKph?.toInt()
                                            .toString()
                                    } km/h"

                                binding.txtPressure.text =
                                    "${
                                        it.data.forecast.forecastday[0].hour?.get(0)?.pressureMb?.toInt()
                                            .toString()
                                    } hPa"


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
//                            Log.e( "showDetailes: ",it.message.toString() )

                            }
                        }
                    }
                })
        }
    }

    private fun dateFormat() {
        val simpleDateFormat = SimpleDateFormat("EE, dd MMM ", Locale.US)
        val dateTime: String =
            simpleDateFormat.format(Calendar.getInstance().timeInMillis).toString()
        binding.txtDay.text = dateTime

    }


}