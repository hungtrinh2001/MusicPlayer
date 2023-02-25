package com.example.musicplayerproject

import android.app.Service
import android.content.Intent
import android.content.SharedPreferences
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log

class MusicService : Service() {

    //val editor: SharedPreferences.Editor? = preferences.edit()
    var actionPlaying: ActionPlaying? =null
    var mediaPlayer: MediaPlayer? = null
    var binder: IBinder =   MyBinder()
    var durationTest = 0
    inner class MyBinder : Binder() {
        fun getService() : MusicService {
            return this@MusicService
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val preferences: SharedPreferences = getSharedPreferences(Communication.PREF_FILE, MODE_PRIVATE)
        val url = intent?.getStringExtra("Song_URL")

        when (preferences.getString(Communication.CONTROL, "null")) {
            Communication.CONTROL_PLAY -> {
                actionPlaying?.playPause()
            }
            Communication.CONTROL_PAUSE -> {
                actionPlaying?.playPause()
            }
            Communication.CONTROL_NEW -> {
                playMedia(url.toString())
            }
            Communication.CONTROL_NEXT -> {
                actionPlaying?.playNext()
            }
            Communication.CONTROL_PREV -> {
                actionPlaying?.playPrev()
            }
            Communication.CONTROL_RESUME -> {
                playMedia(url.toString())
                Log.v("Music", "$durationTest")
                seekTo(durationTest)
            }
        }

        return START_NOT_STICKY
    }

    override fun onBind(p0: Intent?): IBinder {
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.v("Music", "Service Destroyed3")
        durationTest = mediaPlayer!!.currentPosition
        val preferences: SharedPreferences = getSharedPreferences(Communication.PREF_FILE, MODE_PRIVATE)
        val editor: SharedPreferences.Editor? = preferences.edit()
        editor?.putString("durationtest", durationTest.toString())
        editor?.apply()
        return true
    }

    override fun onRebind(intent: Intent?) {
        super.onRebind(intent)
        mediaPlayer!!.seekTo(durationTest)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.v("Music", "Service Destroyed2")
        mediaPlayer?.stop()
        mediaPlayer?.release()
    }

    private fun playMedia(url: String) {
        val preferences: SharedPreferences = getSharedPreferences("MusicPlayerPref", MODE_PRIVATE)
        val editor: SharedPreferences.Editor? = preferences.edit()
        if (mediaPlayer!=null) {
            mediaPlayer!!.stop()
            mediaPlayer!!.release()
        }
        createMediaPlayerUsingURL(url)
        editor?.putString(Communication.CONTROL, Communication.CONTROL_PLAY)
        editor?.apply()
    }

    private fun createMediaPlayerUsingURL(url: String) {
        mediaPlayer = MediaPlayer().apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
            }
            setDataSource(url)
            if (url.contains("/storage/")) {
                prepare()
            } else {
                prepareAsync()
            }
        }

    }

    fun start(){
        mediaPlayer!!.start()
    }
    fun isPlaying(): Boolean{
        return mediaPlayer!!.isPlaying
    }
    fun pause(){
        mediaPlayer!!.pause()
    }
    fun stop() {
        mediaPlayer!!.stop()
    }
    fun release(){
        mediaPlayer!!.release()
    }
    fun reset(){
        mediaPlayer!!.reset()
    }
    fun getDuration(): Int{
        return mediaPlayer!!.duration
    }
    fun getCurrentPosition(): Int {
        return mediaPlayer!!.currentPosition
    }
    fun seekTo(position: Int){
        mediaPlayer!!.seekTo(position)
    }

    fun setup(actionPlaying: ActionPlaying) {
        this.actionPlaying = actionPlaying
    }

    fun repeat() {
        mediaPlayer!!.isLooping = true
    }

    fun noRepeat() {
        mediaPlayer!!.isLooping = false
    }

    fun isLooping(): Boolean {
        return mediaPlayer!!.isLooping
    }
}