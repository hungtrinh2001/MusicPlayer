package com.example.musicplayerproject.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.musicplayerproject.R
import com.example.musicplayerproject.adapters.SearchAdapter
import com.example.musicplayerproject.databinding.ActivityPlaylistScreenBinding
import com.example.musicplayerproject.models.data.Playlist
import com.example.musicplayerproject.models.data.ZingAPI
import com.example.musicplayerproject.models.data.ZingAPI.OnRequestCompleteListener
import com.example.musicplayerproject.models.ui.ItemDisplayData
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.squareup.picasso.Picasso
import okhttp3.Call
import org.json.JSONObject
import java.io.IOException

class PlaylistScreenActivity : AppCompatActivity() {

    private lateinit var playlistScreenBinding: ActivityPlaylistScreenBinding
    private lateinit var adapter: SearchAdapter
    private lateinit var playlistDisplay: ItemDisplayData
    private lateinit var playlistContainedInPlaylist: MutableList<ItemDisplayData>

    private lateinit var playlistImage: ImageView
    private lateinit var playlistName: TextView
    private lateinit var playlistArtists: TextView
    private lateinit var playlistPlayall: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        playlistScreenBinding = ActivityPlaylistScreenBinding.inflate(layoutInflater)

        setContentView(playlistScreenBinding.root)
        supportActionBar?.hide()
        viewFinder()

        playlistDisplay = intent.getSerializableExtra(getString(R.string.PLAYLIST_TO_DISPLAY)) as ItemDisplayData

        ZingAPI.getInstance(this).getPlaylist(playlistDisplay.encodeId, object : OnRequestCompleteListener{
            override fun onSuccess(call: Call, response: String) {
                val data = JSONObject(response).getJSONObject("data")
                val playlist = Playlist.parseData(data)

                playlistContainedInPlaylist = (playlist.listSong.map { ItemDisplayData(it) }).toMutableList()
                runOnUiThread{
                    Picasso.get().load(playlistDisplay.thumbnail).into(playlistImage)
                    playlistName.text = playlistDisplay.title
                    playlistName.isSelected = true
                    playlistArtists.text = playlistDisplay.artistName
                    playlistArtists.isSelected = true
                    Log.v("Playlist", playlistDisplay.title)
                    Log.v("Playlist", playlistDisplay.artistName)
                    Log.v("Playlist", playlist.listSong.size.toString())
                    displayAllSong(playlist)
                }
            }

            override fun onError(call: Call, e: IOException) {

            }
        })
    }

    private fun viewFinder() {
        playlistImage = findViewById(R.id.playlistImage)
        playlistName = findViewById(R.id.playlistName)
        playlistArtists = findViewById(R.id.playlistArtists)
        playlistPlayall = findViewById(R.id.playlistPlayall)
    }

    private fun displayAllSong(playlist: Playlist) {
        adapter = SearchAdapter(this)
        adapter.addRecent(playlistContainedInPlaylist)
        playlistScreenBinding.recycleViewSongContain.adapter = adapter
        adapter.notifyDataSetChanged()

        //Glitching
        playlistPlayall.setOnClickListener {
            val switchToPlayerScene = Intent(this@PlaylistScreenActivity, PlayerActivity::class.java)
            for (i in 0 until playlist.listSong.size) {
                ZingAPI.getInstance(this@PlaylistScreenActivity).getSongByID(playlist.listSong[i].encodeId, object : OnRequestCompleteListener {
                    override fun onSuccess(call: Call, response: String) {
                        var jsonResponseBody = JSONObject(response).getJSONObject("data")
                        playlist.listSong[i].linkQuality128 = jsonResponseBody.getString("128")
                        Log.v("Playlist", playlist.listSong[i].linkQuality128)

                    }

                    override fun onError(call: Call, e: IOException) {

                    }
                })
            }
            switchToPlayerScene.putExtra(this@PlaylistScreenActivity.getString(R.string.ITEM_TO_PLAY), playlist)
            startActivity(switchToPlayerScene)
        }
    }
}