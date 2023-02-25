package com.example.musicplayerproject.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayerproject.OnSearchItemClickListener
import com.example.musicplayerproject.R
import com.example.musicplayerproject.SearchInterface
import com.example.musicplayerproject.models.ui.ItemDisplayData
import com.squareup.picasso.Picasso


class SearchAdapter(
    val context: Context

) : RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    private var displayList = mutableListOf<ItemDisplayData>()

    private lateinit var recyclerView: RecyclerView
    private var searchInf: SearchInterface? = null

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.search_items_layout, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)

        holder.setItemClickListener(object : OnSearchItemClickListener {
            override fun onClick(view: View, position: Int) {
                displayList[position].onItemClicked(context)
            }
        })
    }

    override fun getItemCount(): Int {
        return displayList.size
    }

    fun setup(inf: SearchInterface) {
        this.searchInf = inf
    }

    fun addRecent(recent: MutableList<ItemDisplayData>) {
        displayList.clear()
        displayList.addAll(recent)
    }

    fun clearSongs() {
        displayList.clear()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private val title: TextView = itemView.findViewById(R.id.searchSongTitle)
        private val artist: TextView = itemView.findViewById(R.id.searchArtistName)
        private val thumbnail: ImageView = itemView.findViewById(R.id.thumbnail)
        private val deleteEntry: ImageButton = itemView.findViewById(R.id.deleteEntry)

        private lateinit var itemClickListener: OnSearchItemClickListener

        fun setItemClickListener(itemClickListener: OnSearchItemClickListener) {
            this.itemClickListener = itemClickListener
        }

        fun bind(pos: Int) {
            title.text = displayList[pos].title
            artist.text = displayList[pos].artistName
            Picasso.get().load(displayList[pos].thumbnail).into(thumbnail)

            deleteEntry.setOnClickListener{
                displayList.removeAt(pos)
                searchInf?.deleteEntry(pos)
                this@SearchAdapter.notifyDataSetChanged()
            }
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            if (v != null) {
                itemClickListener.onClick(v, bindingAdapterPosition)
            }
        }
    }
}