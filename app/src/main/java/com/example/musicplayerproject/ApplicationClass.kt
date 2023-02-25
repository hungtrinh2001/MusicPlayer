package com.example.musicplayerproject

import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.SharedPreferences
import android.os.Build

class ApplicationClass: Application() {
    companion object{
        const val CHANNEL_ID_1 = "channel1"
        const val ACTION_PREVIOUS = "actionprevious"
        const val ACTION_NEXT = "actionnext"
        const val ACTION_PLAY = "actionplay"
    }
    override fun onCreate() {
        super.onCreate()
        //Initialize SharedPreference file for this app
        val preferences: SharedPreferences = getSharedPreferences(Communication.PREF_FILE, MODE_PRIVATE)
        val editor: SharedPreferences.Editor? = preferences.edit()
        editor?.putString(Communication.URL, "null")
        editor?.putString(Communication.CONTROL, "null")
        editor?.apply()
        createNotificationChannel()
    }


    private fun createNotificationChannel() {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {
            val channel1 = NotificationChannel(CHANNEL_ID_1, "Music Player - Notification", NotificationManager.IMPORTANCE_LOW)

            channel1.description = "See channel name"
            //Supposed to show playback in lock screen, but still not working yet
            channel1.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            val notificationManager: NotificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel1)
        }
    }
}