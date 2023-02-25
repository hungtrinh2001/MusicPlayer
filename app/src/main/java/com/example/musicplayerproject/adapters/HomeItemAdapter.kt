package com.example.musicplayerproject.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayerproject.R
import com.example.musicplayerproject.activities.PlayerActivity
import com.example.musicplayerproject.activities.PlaylistScreenActivity
import com.example.musicplayerproject.models.data.Song
import com.example.musicplayerproject.models.data.SongLyric
import com.example.musicplayerproject.models.data.Video
import com.example.musicplayerproject.models.data.ZingAPI
import com.example.musicplayerproject.models.ui.ItemDisplayData
import com.squareup.picasso.Picasso
import okhttp3.Call
import org.json.JSONObject
import java.io.IOException

class HomeItemAdapter(
    val context: Context,
    val layoutToInflater: Int,
    var listItemDisplayData: List<ItemDisplayData>,
    val itemClickListener: ItemClickListener


): RecyclerView.Adapter<HomeItemAdapter.ViewHolder>() {

    var sliderContentName = -1

    companion object
    {
        val MARGIN: Int = 5
        fun createHomeItemAdapter(context: Context, layoutToInflater: Int, listItemDisplayData: List<ItemDisplayData>): HomeItemAdapter
        {

            var adapter = HomeItemAdapter(context, layoutToInflater, listItemDisplayData, object : ItemClickListener{
                override fun onItemClicked(position: Int) {
                    if (listItemDisplayData[position].type == ItemDisplayData.ITEM_TYPE.SONG)
                    {
                        var item = listItemDisplayData[position]
                        var switchToPlayerSceneIntent = Intent(context, PlayerActivity::class.java)
                        ZingAPI.getInstance(context).getSongByID(item.encodeId, object : ZingAPI.OnRequestCompleteListener{
                            override fun onSuccess(call: Call, response: String) {
                                var jsonResponseBody = JSONObject(response).getJSONObject("data")
                                var song = Song()
                                song.encodeId = item.encodeId
                                song.title = item.title
                                song.thumbnail = item.thumbnail
                                song.artistsNames = item.artistName
                                song.linkQuality128 = jsonResponseBody.getString("128")
                                song.linkQuality320 = jsonResponseBody.getString("320")
                                switchToPlayerSceneIntent.putExtra("SONG_EXTRA", song)
                                ContextCompat.startActivity(context, switchToPlayerSceneIntent, null)
                            }

                            override fun onError(call: Call, e: IOException) {

                            }
                        })
                    }
                    else if(listItemDisplayData[position].type == ItemDisplayData.ITEM_TYPE.VIDEO)
                    {
                        var item = listItemDisplayData[position]
                        var switchToPlayerSceneIntent = Intent(context, PlayerActivity::class.java)
                        ZingAPI.getInstance(context).getLyric(item.encodeId, object : ZingAPI.OnRequestCompleteListener{
                            override fun onSuccess(call: Call, response: String) {
                                var data = JSONObject(response).getJSONObject("data")
                                var lyric = SongLyric.parseData(data)
                                var vid = Video(item.encodeId, item.title, item.artistName, item.thumbnail, lyric.streamingURL)
                                switchToPlayerSceneIntent.putExtra(context.getString(R.string.ITEM_TO_PLAY), vid)
                                ContextCompat.startActivity(context, switchToPlayerSceneIntent, null)
                            }

                            override fun onError(call: Call, e: IOException) {

                            }

                        })
                    }
                    else if (listItemDisplayData[position].type == ItemDisplayData.ITEM_TYPE.PLAYLIST)
                    {
                        var switchToPlaylistScene = Intent(context, PlaylistScreenActivity::class.java)
                        switchToPlaylistScene.putExtra(context.getString(R.string.PLAYLIST_TO_DISPLAY), listItemDisplayData[position])
                        ContextCompat.startActivity(context, switchToPlaylistScene, null)
                    }
                }
            })
            return adapter
        }
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
    {
        var imgThumb = itemView.findViewById<ImageView>(R.id.item_home_poster_img_thumbnail)
        var title = itemView.findViewById<TextView>(R.id.item_home_poster_txt_title)
        var detail = itemView.findViewById<TextView>(R.id.item_home_poster_txt_detail)
        fun bind(position: Int)
        {
            Picasso.get().load(listItemDisplayData[position].thumbnail).fit().into(imgThumb)
            title.text = listItemDisplayData[position].title
            when (listItemDisplayData[position].type) {
                ItemDisplayData.ITEM_TYPE.SONG -> detail.text = "Song - " + listItemDisplayData[position].artistName
                ItemDisplayData.ITEM_TYPE.VIDEO -> detail.text = "Video - " + listItemDisplayData[position].artistName
                ItemDisplayData.ITEM_TYPE.PLAYLIST -> detail.text = "Playlist - " + listItemDisplayData[position].artistName
            }

            itemView.setOnClickListener {
                itemClickListener.onItemClicked(position)
            }
            itemView.findViewById<Button>(R.id.button_delete_item).setOnClickListener{
                itemClickListener.onDeleteButtonClicked(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(layoutToInflater, parent, false)
        val layoutParams = view.findViewById<ConstraintLayout>(R.id.item_home_poster_layout).layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.setMargins(MARGIN, MARGIN, MARGIN, MARGIN)

        if (sliderContentName == R.id.recycle_view_recent)
        {
            view.findViewById<Button>(R.id.button_delete_item).visibility = View.VISIBLE
        }
        else
        {
            view.findViewById<Button>(R.id.button_delete_item).visibility = View.INVISIBLE
        }

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount() = listItemDisplayData.size

    interface ItemClickListener {
        fun onItemClicked(position: Int)
        fun onDeleteButtonClicked(position: Int){}
    }
}