package com.example.weather.ui

import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.weather.R
import com.example.weather.databinding.ActivityMainBinding
import com.example.weather.utils.Status
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    lateinit var viewModel: MainViewModel
    lateinit var binding: ActivityMainBinding

      var locationListener =LocationListener{
          showDetailes(it.latitude,it.longitude)
     }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
        getLocation()


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
        private const val PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 100
    }

   private fun initialize() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory()
        )[MainViewModel::class.java]
    }

   private fun showDetailes(latitude :Double ,longitude:Double ) {
        lifecycleScope.launch {
            viewModel.getLocationDetails(latitude, longitude).observe(this@MainActivity, Observer {
                it.let {
                    when(it.status){
                        Status.SUCCESS->{
                            binding.txtCountry.text = it.data?.location?.country
                            binding.txtRegion.text = it.data?.location?.region
                            binding.txtDay.text = it.data?.location?.localtime
                            binding.lottie.setAnimation(R.raw.location)
                            binding.lottie.playAnimation()
                            binding.actionBar.toolbar.title = it.data?.location?.name
                            binding.cardDetails.setBackgroundResource(R.drawable.custom_card)
                            Glide.with(applicationContext).load("http:"+it.data?.current?.condition?.icon).into(binding.weatherIcon)
                            binding.txtWeather.text = it.data?.current?.condition?.text
                            binding.txtTemp.text = it.data?.current?.tempC.toString()
                            setUpText()
                        }

                        Status.LOADING->{}

                        Status.ERROR->{
                            Toast.makeText(this@MainActivity , it.message.toString(),Toast.LENGTH_SHORT).show()
                            Log.e( "error: ", it.message.toString())
                        }
                    }
                }
            })
        }
    }

    fun setUpText(){
        val paint = binding.txtTemp.paint
        val width = paint.measureText(binding.txtTemp.text.toString())
        val textShader: Shader = LinearGradient(0f, 0f, width, binding.txtTemp.textSize, intArrayOf(
            Color.parseColor("#AECDFF"),
            Color.parseColor("#FDB54E"),
            Color.parseColor("#fff")
        ), null, Shader.TileMode.REPEAT)

        binding.txtTemp.paint.shader = textShader
    }
}