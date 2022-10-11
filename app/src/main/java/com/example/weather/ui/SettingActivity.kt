package com.example.weather.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.RadioGroup
import com.example.weather.R
import com.example.weather.databinding.ActivitySettingBinding

class SettingActivity : AppCompatActivity() {
    lateinit var binding: ActivitySettingBinding
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.actionBar.toolbar.title = "Setting"
        sharedPreferences = getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)

        if (!sharedPreferences.contains("Temp")){
            binding.radioC.isChecked = true
        }


        if (sharedPreferences.getString("Temp" ,binding.radioC.text.toString() ).equals(binding.radioC.text.toString())){
            binding.radioC.isChecked = true
        } else  binding.radioF.isChecked = true

        binding.btnApply.setOnClickListener {
            if (binding.radioC.isChecked){
                sharedPreferences.edit().putString("Temp" , binding.radioC.text.toString()).apply()
                startActivity(Intent(applicationContext , MainActivity::class.java))
            } else {
                sharedPreferences.edit().putString("Temp" , binding.radioF.text.toString()).apply()
                startActivity(Intent(applicationContext , MainActivity::class.java))
            }
        }
    }
}