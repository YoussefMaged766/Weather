package com.example.weather.utils

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import com.example.weather.R
import com.example.weather.ui.MainActivity
import com.example.weather.ui.SplashScreenActivity


class MyService() : Service() {
    lateinit var notificationManager: NotificationManager
    lateinit var notificationChannel: NotificationChannel
    lateinit var notification: Notification
    private val channelId = "i.apps.notifications"
    private val description = "Test notification"
     lateinit var sharedPreferences: SharedPreferences
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        onTaskRemoved(intent)
        sharedPreferences =  getSharedPreferences("PREFERENCE_NAME",Context.MODE_PRIVATE)
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val intent1 = Intent(this, SplashScreenActivity::class.java)
        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(this, 0, intent1, 0)
        intent1.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        notificationChannel =
            NotificationChannel(channelId, description, NotificationManager.IMPORTANCE_DEFAULT)
        notificationChannel.enableLights(true)
        notificationChannel.enableVibration(false)
        notificationManager.createNotificationChannel(notificationChannel)



        val stateName  =sharedPreferences.getString("stateName" , "")
        val temp = sharedPreferences.getInt("temp",0)
        val weather = sharedPreferences.getString("weather" , "")

        notification = Notification.Builder(this, channelId)
            .setSmallIcon(R.drawable.small_logo)
            .setContentTitle("Today in $stateName")
            .setContentText("$temp° $weather   See full forecast")
            .setContentIntent(pendingIntent).build()

        notification.flags = notification.flags or Notification.FLAG_AUTO_CANCEL
        NotificationManagerCompat.from(this).notify(1,notification)
//        startForeground(1,notification)
//        notificationManager.notify(1,notification)
        Log.e("onStartCommand: ", "build")
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {



        throw  UnsupportedOperationException( "Not yet implemented" )
    }



}