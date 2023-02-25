package com.example.musicplayerproject.adapters

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayerproject.OnSearchItemClickListener
import com.example.musicplayerproject.R
import com.example.musicplayerproject.activities.PlayerActivity
import com.example.musicplayerproject.models.data.Playlist
import com.example.musicplayerproject.models.data.Song
import com.squareup.picasso.Picasso


class LibraryAdapter (val context: Context) : RecyclerView.Adapter<LibraryAdapter.ViewHolder>() {
    private var displayList = mutableListOf<Song>()
    private lateinit var recyclerView: RecyclerView

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibraryAdapter.ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.library_items_layout, parent, false))
    }

    override fun onBindViewHolder(holder: LibraryAdapter.ViewHolder, position: Int) {
        holder.bind(position)
        holder.setItemClickListener(object : OnSearchItemClickListener {
            override fun onClick(view: View, position: Int) {
                Log.v("Library", displayList[position].encodeId)
                Log.v("Library", displayList[position].title)
                Log.v("Library", displayList[position].thumbnail)
                Log.v("Library", displayList[position].artistsNames)
                Log.v("Library", displayList[position].streamingLink)

                var defaultPlaylist = Playlist()
                defaultPlaylist.title = "In memory"
                defaultPlaylist.listSong.addAll(displayList)

                var switchToPlayerScene = Intent(this@LibraryAdapter.context, PlayerActivity::class.java)
                switchToPlayerScene.putExtra(this@LibraryAdapter.context.getString(R.string.ITEM_TO_PLAY), defaultPlaylist)
                switchToPlayerScene.putExtra("libraryPos", position)
                this@LibraryAdapter.context.startActivity(switchToPlayerScene)
            }
        })
    }

    override fun getItemCount(): Int {
        return displayList.size
    }

    fun addLibrary(library: MutableList<Song>) {
        displayList.clear()
        displayList.addAll(library)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private val title: TextView = itemView.findViewById(R.id.librarySongTitle)
        private val artist: TextView = itemView.findViewById(R.id.libraryArtistName)
        private val thumbnail: ImageView = itemView.findViewById(R.id.thumbnail)

        private lateinit var itemClickListener: OnSearchItemClickListener

        fun setItemClickListener(itemClickListener: OnSearchItemClickListener) {
            this.itemClickListener = itemClickListener
        }

        fun bind(pos: Int) {
            title.text = displayList[pos].title
            artist.text = displayList[pos].artistsNames
            /*val uri: Uri = Uri.parse(displayList[pos].thumbnail)
            Log.v("Music", displayList[pos].thumbnail)
            Log.v("Music", uri.toString())
            val bitmapTest = BitmapFactory.decodeFile(uri.path)
            val emptyBitmap =
                Bitmap.createBitmap(bitmapTest.width, bitmapTest.height, bitmapTest.config)*/

            Picasso.get().load(displayList[pos].thumbnail).into(thumbnail)


            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            if (v != null) {
                itemClickListener.onClick(v, bindingAdapterPosition)
            }
        }
    }
}