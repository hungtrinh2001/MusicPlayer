package com.example.musicplayerproject

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import com.example.musicplayerproject.ApplicationClass.Companion.ACTION_NEXT
import com.example.musicplayerproject.ApplicationClass.Companion.ACTION_PLAY
import com.example.musicplayerproject.ApplicationClass.Companion.ACTION_PREVIOUS

class NotificationReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val preferences: SharedPreferences = context!!.getSharedPreferences("MusicPlayerPref",
            Service.MODE_PRIVATE
        )
        val editor: SharedPreferences.Editor? = preferences.edit()
        val actionName = intent?.action
        val serviceIntent = Intent(context, MusicService::class.java)
        if(actionName!=null) {
            when(actionName){
                ACTION_PLAY -> {
                    context.startService(serviceIntent)
                }
                ACTION_NEXT -> {
                    serviceIntent.putExtra("Song_URL", "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3")
                    editor?.putString(Communication.URL, "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3")
                    editor?.putString(Communication.CONTROL, Communication.CONTROL_NEXT)
                    editor?.apply()
                    context.startService(serviceIntent)
                }
                ACTION_PREVIOUS -> {
                    serviceIntent.putExtra("Song_URL", "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3")
                    editor?.putString(Communication.URL, "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3")
                    editor?.putString(Communication.CONTROL, Communication.CONTROL_PREV)
                    editor?.apply()
                    context.startService(serviceIntent)
                }
            }
        }
    }
}