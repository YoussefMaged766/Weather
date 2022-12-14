package com.example.weather.ui


import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.weather.R
import com.example.weather.adapter.HourAdapter
import com.example.weather.databinding.ActivityMainBinding
import com.example.weather.utils.MyService
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
    private var isopen = false
    var addresses: ArrayList<Address> = ArrayList()
    lateinit var cityName: String
    lateinit var stateName: String
    lateinit var countryName: String
    private lateinit var sharedPreferences: SharedPreferences

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


        setSupportActionBar(binding.actionBar.toolbar)


        binding.actionBar.toolbar.setTitleTextColor(Color.WHITE)
        val bottomView = findViewById<View>(R.id.linearLayout2)
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomView)
//        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        bottomSheetBehavior.peekHeight = 200



        bottomSheetBehavior.setBottomSheetCallback(object : BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
//                Log.d("onStateChanged: $",newState.toString())
                // React to state change

                if (newState == BottomSheetBehavior.STATE_EXPANDED)
                    binding.ml.transitionToEnd()
                else binding.ml.transitionToStart()
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // React to dragging events

                Log.d("onSlide: ", "$slideOffset")


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
        sharedPreferences = getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)

    }

    private fun showDetailes(latitude: Double, longitude: Double) {
        lifecycleScope.launch {
            if (sharedPreferences.getString("Temp","Celsius").equals("Celsius")){
               changeApiTemp("metric" , latitude,longitude)
                }  else if (sharedPreferences.getString("Temp","Celsius").equals("Fahrenheit")){
                changeApiTemp("imperial" , latitude,longitude)
                }
            }
        binding.txtNext.setOnClickListener {
                val intent = Intent(applicationContext, DaysActivity::class.java)
                intent.putExtra("lat", latitude)
                intent.putExtra("lon", longitude)
                startActivity(intent)

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
            SimpleDateFormat(" H:mm aa", Locale.getDefault()).format(dateRise * 1000)
        val sunSet: String =
            SimpleDateFormat(" h:mm aa", Locale.US).format(dateSet * 1000).toString()

        binding.txtSunSet.text = sunSet
        binding.txtSunRise.text = sunRise
    }


    override fun onStop() {
        super.onStop()
        startService(Intent(applicationContext, MyService::class.java))
        createNotification()
    }

    override fun onResume() {
        stopService(Intent(applicationContext, MyService::class.java))
        super.onResume()
    }

    override fun onStart() {
        super.onStart()
        stopService(Intent(applicationContext, MyService::class.java))
    }

    override fun onDestroy() {
        super.onDestroy()
        startService(Intent(applicationContext, MyService::class.java))
        Log.e("onDestroy: ", "Destroy")
    }

    fun createNotification() {
        val myIntent = Intent(applicationContext, MyService::class.java)
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val pendingIntent = PendingIntent.getService(this, 0, myIntent, 0)
        val calendar = Calendar.getInstance()
        calendar[Calendar.SECOND] = 0
        calendar[Calendar.MINUTE] = 0
        calendar[Calendar.HOUR] = 0
        calendar[Calendar.AM_PM] = Calendar.AM
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            (1000 * 60 * 60 * 24).toLong(),
            pendingIntent
        )
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.setting -> {
              startActivity(Intent(applicationContext, SettingActivity::class.java))
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }
    suspend fun changeApiTemp(unit:String , latitude :Double , longitude:Double){
        viewModel.getLocationDetails(latitude, longitude, unit).collect {
            it.let {
                when (it.status) {
                    Status.SUCCESS -> {
                        binding.txtCountry.text = "$countryName, $stateName"
                        binding.actionBar.toolbar.title = cityName
                        if (unit == "metric"){
                            binding.txtTemp.text = it.data?.current?.temp?.toInt().toString()
                        } else{
                            binding.txtTemp.text = it.data?.current?.temp?.toInt().toString()
                            binding.txtDegree.text = "??F"
                        }
                        binding.txtWeather.text = it.data?.current?.weather?.get(0)?.description

                        binding.txtFeelLike.text =
                            "Feels like ${it.data?.current?.feelsLike?.toInt()}??"
                        binding.txtDegree.visibility = View.VISIBLE
                        dateFormat()

                        adapter =
                            HourAdapter(it.data?.hourly!!)
                        binding.recyclerHours.adapter = adapter
                        convertDate(it.data.current?.sunrise!!, it.data.current.sunset!!)

                        binding.txtPrecip.text = "${it.data?.hourly[0].pop?.times(100)}%"
                        binding.txtHumidity.text =
                            "${it.data?.current?.humidity}%"

                        if (unit=="metric"){
                            binding.txtWind.text =
                                "${it.data?.current?.windSpeed?.toInt().toString()} m/s"
                        } else{
                            binding.txtWind.text =
                                "${it.data?.current?.windSpeed?.toInt().toString()} mile/h"
                        }



                        binding.txtPressure.text =
                            "${it.data?.current?.pressure?.toString()} hPa"

                        binding.ml.visibility = View.VISIBLE
                        binding.linearLayout2.visibility = View.VISIBLE
                        binding.lottie.visibility = View.GONE

                    }

                    Status.LOADING -> {
                        binding.lottie.playAnimation()
                        binding.lottie.visibility = View.VISIBLE
                        binding.linearLayout2.visibility = View.INVISIBLE
                    }

                    Status.ERROR -> {
                        Toast.makeText(
                            this@MainActivity,
                            it.message.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.e("weather: ", it.message.toString())
                        binding.ml.visibility = View.VISIBLE
                        binding.linearLayout2.visibility = View.VISIBLE
                        binding.lottie.visibility = View.GONE
                    }
                }
            }

            sharedPreferences.edit().putString("stateName", stateName).apply()
            it.data?.current?.temp?.toInt()
                ?.let { it1 -> sharedPreferences.edit().putInt("temp", it1).apply() }

            sharedPreferences.edit()
                .putString("weather", it.data?.current?.weather?.get(0)?.description).apply()

        }
    }


}