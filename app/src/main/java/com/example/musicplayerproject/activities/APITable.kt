package com.example.musicplayerproject.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.example.musicplayerproject.R
import com.example.musicplayerproject.models.data.*
import okhttp3.Call
import org.json.JSONObject
import java.io.IOException

class APITable : AppCompatActivity() {

    private lateinit var txtName: EditText
    private  lateinit var txtID: EditText
    private  lateinit var txtResult: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_apitable)
        txtID = findViewById(R.id.txtSongID)
        txtName = findViewById(R.id.txtNameSomething)
        txtResult = findViewById(R.id.txtResult)
    }

    fun search(view: View)  {
        ZingAPI.getInstance(this).search(txtName.text.toString(), object : ZingAPI.OnRequestCompleteListener{
            override fun onSuccess(call: Call, response: String) {
                Log.i("SignInActivity", response)
                updateTextResult(response)
                var data = JSONObject(response)
                data = data.getJSONObject("data")
                var songs = data.getJSONArray("songs")
                var listSong = mutableListOf<Song>()
                for( i in 0..songs.length()-1)
                {
                    var s = Song()
                    var song = songs.getJSONObject(i)
                    s.encodeId = song.getString("encodeId")
                    s.title = song.getString("title")
                    s.thumbnail = song.getString("thumbnail")
                    s.artists = ArrayList<Artist>()
                    var artists = song.getJSONArray("artists")
                    for (j in 0..artists.length() -1)
                    {
                        var artist = Artist()
                        var art = artists.getJSONObject(i)
                        artist.id = art.getString("id")
                        artist.name = art.getString("name")
                        artist.thumbnail = art.getString("thumbnail")
                        s.artists.add(artist)
                    }
                    listSong.add(s)
                }

                var artists = data.getJSONArray("artists")
                var listArtist = mutableListOf<Artist>()
                for (i in 0..artists.length()-1)
                {
                    var artist = Artist()
                    var artistObject = artists.getJSONObject(i)

                    artist.id = artistObject.getString("id")
                    artist.name = artistObject.getString("name")
                    artist.thumbnail = artistObject.getString("thumbnail")
                    artist.playListId = artistObject.getString("playlistId")
                    listArtist.add(artist)
                }

                var videos = data.getJSONArray("videos")
                var listVideo = mutableListOf<Video>()
                for (i in 0..videos.length() - 1)
                {
                    var vid = Video()
                    var videoObject = videos.getJSONObject(i)
                    vid.encodeId = videoObject.getString("encodeId")
                    vid.title = videoObject.getString("title")
                    vid.artistNames = videoObject.getString("artistNames")
                    vid.thumbnail = videoObject.getString("thumbnail")
                    listVideo.add(vid)
                }

            }

            override fun onError(call: Call, e: IOException) {

            }
        })

    }

    fun getSongByID(view: View){
        ZingAPI.getInstance(this).getSongByID(txtID.text.toString(), object : ZingAPI.OnRequestCompleteListener{
            override fun onSuccess(call: Call, response: String) {
                Log.i("SignInActivity", response)
                updateTextResult(response)
            }

            override fun onError(call: Call, e: IOException) {

            }
        })
    }

    fun getLyric(view: View) {
        ZingAPI.getInstance(this).getLyric(txtID.text.toString(), object : ZingAPI.OnRequestCompleteListener{
            override fun onSuccess(call: Call, response: String) {
                Log.i("SignInActivity", response)
                updateTextResult(response)
                var data = JSONObject(response).getJSONObject("data")
                var lr = SongLyric.parseData(data)
            }

            override fun onError(call: Call, e: IOException) {

            }
        })

    }

    fun getVideo(view: View) {
        ZingAPI.getInstance(this).getVideo(txtID.text.toString(), object : ZingAPI.OnRequestCompleteListener{
            override fun onSuccess(call: Call, response: String) {
                Log.i("SignInActivity", response)
                updateTextResult(response)
                var data = JSONObject(response)
                data = data.getJSONObject("data")
                var streamingVideoLink = data.getJSONObject("streamingUrl")


            }

            override fun onError(call: Call, e: IOException) {

            }
        })

    }

    fun getArtist(view: View) {
        ZingAPI.getInstance(this).getArtist(txtName.text.toString(), object : ZingAPI.OnRequestCompleteListener{
            override fun onSuccess(call: Call, response: String) {
                Log.i("SignInActivity", response)
                updateTextResult(response)
            }

            override fun onError(call: Call, e: IOException) {

            }
        })
    }

    fun getTop100(view: View) {
        ZingAPI.getInstance(this).getTop100(object : ZingAPI.OnRequestCompleteListener{
            override fun onSuccess(call: Call, response: String) {
                Log.i("SignInActivity", response)
                updateTextResult(response)
            }

            override fun onError(call: Call, e: IOException) {

            }
        })
    }

    fun getHome(view: View) {
        ZingAPI.getInstance(this).getHome(object : ZingAPI.OnRequestCompleteListener{
            override fun onSuccess(call: Call, response: String) {
                Log.i("SignInActivity", response)
                updateTextResult(response)
            }

            override fun onError(call: Call, e: IOException) {

            }
        })
    }

    fun getPlaylist(view: View)
    {
        ZingAPI.getInstance(this).getPlaylist (txtID.text.toString(), object : ZingAPI.OnRequestCompleteListener{
            override fun onSuccess(call: Call, response: String) {
                Log.i("SignInActivity", response)
                updateTextResult(response)
            }

            override fun onError(call: Call, e: IOException) {

            }

        })
    }

    fun updateTextResult(respon: String)
    {
        runOnUiThread {
            txtResult.text = respon

        }
    }
}