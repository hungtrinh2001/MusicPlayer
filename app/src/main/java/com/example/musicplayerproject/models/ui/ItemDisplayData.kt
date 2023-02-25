package com.example.musicplayerproject.models.ui

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.example.musicplayerproject.R
import com.example.musicplayerproject.activities.PlayerActivity
import com.example.musicplayerproject.activities.PlaylistScreenActivity
import com.example.musicplayerproject.models.data.*
import okhttp3.Call
import org.json.JSONObject
import java.io.IOException

class ItemDisplayData : java.io.Serializable{
    enum class ITEM_TYPE
    {
        SONG,
        PLAYLIST,
        ARTIS,
        VIDEO,
        BANNER
    }

    lateinit var type: ITEM_TYPE
    lateinit var encodeId: String
    lateinit var title: String
    var alias: String = ""
    lateinit var thumbnail: String
    var artistName = ""

    constructor()

    constructor(song: Song)
    {
        this.type = ITEM_TYPE.SONG
        this.encodeId = song.encodeId
        this.title = song.title
        this.alias = song.alias
        this.thumbnail = song.thumbnail
        this.artistName = song.artistsNames

    }

    constructor(playlist: Playlist)
    {
        this.type = ITEM_TYPE.PLAYLIST
        this.title = playlist.title
        this.encodeId = playlist.encodeId
        this.thumbnail = playlist.thumbnail
        this.artistName = playlist.artistsNames

    }

    constructor(video: Video)
    {
        this.type = ITEM_TYPE.VIDEO
        this.title = video.title
        this.encodeId = video.encodeId
        this.thumbnail = video.thumbnail
        this.artistName = video.artistNames
    }

    constructor(type: ITEM_TYPE, encodeId: String, title: String, alias: String, thumbnail:String)
    {
        this.type = type
        this.encodeId = encodeId
        this.title = title
        this.alias = alias
        this.thumbnail = thumbnail
    }

    fun onItemClicked(context: Context)
    {
        when (this.type) {
            ITEM_TYPE.SONG -> {
                val item = this
                val switchToPlayerSceneIntent = Intent(context, PlayerActivity::class.java)
                ZingAPI.getInstance(context).getSongByID(item.encodeId, object : ZingAPI.OnRequestCompleteListener{
                    override fun onSuccess(call: Call, response: String) {
                        val jsonResponseBody = JSONObject(response).getJSONObject("data")
                        val song = Song()
                        song.encodeId = item.encodeId
                        song.title = item.title
                        song.thumbnail = item.thumbnail
                        song.artistsNames = item.artistName
                        song.linkQuality128 = jsonResponseBody.getString("128")
                        song.linkQuality320 = jsonResponseBody.getString("320")
                        switchToPlayerSceneIntent.putExtra(context.getString(R.string.ITEM_TO_PLAY), song)
                        ContextCompat.startActivity(context, switchToPlayerSceneIntent, null)
                    }

                    override fun onError(call: Call, e: IOException) {

                    }
                })
            }

            ITEM_TYPE.VIDEO -> {
                val item = this
                val switchToPlayerScene = Intent(context, PlayerActivity::class.java)

                ZingAPI.getInstance(context).getLyric(item.encodeId, object : ZingAPI.OnRequestCompleteListener{
                    override fun onSuccess(call: Call, response: String) {
                        val data = JSONObject(response).getJSONObject("data")
                        val lyric = SongLyric.parseData(data)
                        val vid = Video(item.encodeId, item.title, item.artistName, item.thumbnail, lyric.streamingURL)
                        switchToPlayerScene.putExtra(context.getString(R.string.ITEM_TO_PLAY), vid)
                        ContextCompat.startActivity(context, switchToPlayerScene, null)
                    }

                    override fun onError(call: Call, e: IOException) {

                    }

                })

            }
            ITEM_TYPE.PLAYLIST -> {
                var switchToPlaylistScene = Intent(context, PlaylistScreenActivity::class.java)
                switchToPlaylistScene.putExtra(context.getString(R.string.PLAYLIST_TO_DISPLAY), this)
                ContextCompat.startActivity(context, switchToPlaylistScene, null)
            }
            else -> {}
        }
    }

}