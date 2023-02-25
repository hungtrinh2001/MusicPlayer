package com.example.musicplayerproject.fragments

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayerproject.R
import com.example.musicplayerproject.adapters.HomeItemAdapter
import com.example.musicplayerproject.databinding.FragmentHome2Binding
import com.example.musicplayerproject.models.data.Song
import com.example.musicplayerproject.models.data.ZingAPI
import com.example.musicplayerproject.models.ui.ItemDisplayData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_home2.view.*
import kotlinx.android.synthetic.main.loading.*
import okhttp3.Call
import org.json.JSONObject
import java.io.IOException
import java.time.LocalTime
import java.util.*

class HomeFragment : Fragment() {

    private lateinit var fragmentHomeBinding: FragmentHome2Binding
    lateinit var listNewReleaseVpop: MutableList<Song>
    lateinit var listNewReleaseOther: MutableList<Song>
    lateinit var listSongRecent: MutableMap<String, ItemDisplayData>
    lateinit var dicPlaylist: MutableMap<String, MutableList<ItemDisplayData>>

    private val loadingFragment = LoadingScreen()
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var myObj: LocalTime
    private var currentTime: Int? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myObj = LocalTime.now()
        currentTime = myObj.hour
        Log.v("Music", "$currentTime")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        fragmentHomeBinding = FragmentHome2Binding.inflate(inflater, container, false)

        val fm = this@HomeFragment.activity?.supportFragmentManager
        val transaction = fm?.beginTransaction()
        transaction?.add(fragmentHomeBinding.constraintView.id, loadingFragment)
        transaction?.commit()

        when (currentTime) {
            in (18..23) -> {
                fragmentHomeBinding.timeBar.text = "Good Evening"
            }
            in (0..6) -> {
                fragmentHomeBinding.timeBar.text = "Good Night"
            }
            in (7..11) -> {
                fragmentHomeBinding.timeBar.text = "Good Morning"
            }
            else -> {
                fragmentHomeBinding.timeBar.text = "Good Afternoon"
            }
        }


        listNewReleaseOther = mutableListOf()
        listNewReleaseVpop = mutableListOf()
        listSongRecent = mutableMapOf()
        dicPlaylist = mutableMapOf()

        firebaseDatabase = Firebase.database
        firebaseAuth = FirebaseAuth.getInstance()

        ZingAPI.getInstance(this.context!!).getHome(object : ZingAPI.OnRequestCompleteListener {
            override fun onSuccess(call: Call, response: String) {
                val data = JSONObject(response).getJSONObject("data")
                val itemsObject = data.getJSONArray("items")
                for(i in 0 until itemsObject.length())
                {
                    val itemObject = itemsObject.getJSONObject(i)
                    if (itemObject.getString("sectionType").equals("new-release"))
                    {
                        val vpop = itemObject.getJSONObject("items").getJSONArray("vPop")
                        val other = itemObject.getJSONObject("items").getJSONArray("others")
                        for(j in 0 until vpop.length())
                        {
                            val song = Song.parseSongViaJsonObject(vpop.getJSONObject(j))
                            listNewReleaseVpop.add(song)
                        }

                        for(j in 0 until other.length())
                        {
                            val song = Song.parseSongViaJsonObject(other.getJSONObject(j))
                            listNewReleaseOther.add(song)
                        }
                    }
                    if (itemObject.getString("sectionType").equals("playlist"))
                    {
                        val listPlaylistObject = itemObject.getJSONArray("items")
                        val listPlaylist = mutableListOf<ItemDisplayData>()
                        for(j in 0 until listPlaylistObject.length())
                        {
                            val playlistItemObject = listPlaylistObject.getJSONObject(j)
                            val playlistID = playlistItemObject.getString("encodeId")
                            val playlistTitle = playlistItemObject.getString("title")
                            val playlistThumb = playlistItemObject.getString("thumbnail")
                            val sortDescription = playlistItemObject.getString("sortDescription")
                            listPlaylist.add(ItemDisplayData(ItemDisplayData.ITEM_TYPE.PLAYLIST, playlistID, playlistTitle,sortDescription, playlistThumb))
                        }
                        dicPlaylist[itemObject.getString("sectionId")] = listPlaylist
                    }
                }
                activity!!.runOnUiThread {
                    displayAllItemHome()
                }
            }

            override fun onError(call: Call, e: IOException) {

            }
        })

        return fragmentHomeBinding.root
    }

    fun displayAllItemHome()
    {
        val newReleaseVpopRecycleView = fragmentHomeBinding.recycleViewNewReleaseVpop
        val listItemNewReleaseVpop = listNewReleaseVpop.map { ItemDisplayData(it) }
        val sliderVpopAdapter = HomeItemAdapter.createHomeItemAdapter(this.context!!, R.layout.item_home_poster, listItemNewReleaseVpop)
        newReleaseVpopRecycleView.adapter = sliderVpopAdapter
        newReleaseVpopRecycleView.adapter!!.notifyDataSetChanged()

        val newReleaseOtherRecycleView = fragmentHomeBinding.recycleViewNewReleaseOther
        val listItemNewReleaseOther = listNewReleaseOther.map { ItemDisplayData(it) }
        val sliderOtherAdapter = HomeItemAdapter.createHomeItemAdapter(this.context!!, R.layout.item_home_poster, listItemNewReleaseOther)
        newReleaseOtherRecycleView.adapter = sliderOtherAdapter
        newReleaseOtherRecycleView.adapter!!.notifyDataSetChanged()

        for (entry in dicPlaylist)
        {
            var rv = RecyclerView(this.context!!)
            when (entry.key) {
                "hAutoTheme1" -> {
                    rv = fragmentHomeBinding.recycleViewMidWeekEnergy

                }
                "h100" -> {
                    rv = fragmentHomeBinding.recycleViewTopOneHundreds
                }
                "hXone" -> {
                    rv = fragmentHomeBinding.recycleViewXone
                }
            }
            val adapter = HomeItemAdapter.createHomeItemAdapter(this.context!!, R.layout.item_home_poster, entry.value)
            rv.adapter = adapter
            rv.adapter!!.notifyDataSetChanged()

        }

        val recentRecycleView = fragmentHomeBinding.recycleViewRecent
        val sliderRecentAdapter = HomeItemAdapter(this.context!!, R.layout.item_home_poster, listSongRecent.values.toList(), object :HomeItemAdapter.ItemClickListener{
            override fun onItemClicked(position: Int) {
                var list = listSongRecent.values.toList()
                list[position].onItemClicked(context!!)

            }

            override fun onDeleteButtonClicked(position: Int) {
                super.onDeleteButtonClicked(position)
                firebaseDatabase.reference.child("History").child(firebaseAuth.currentUser!!.uid).child(listSongRecent.keys.toList()[position]).removeValue()
            }

        })
        sliderRecentAdapter.sliderContentName = R.id.recycle_view_recent
        recentRecycleView.adapter = sliderRecentAdapter
        recentRecycleView.adapter!!.notifyDataSetChanged()

        firebaseDatabase.reference.child("History").child(firebaseAuth.currentUser!!.uid).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                listSongRecent.clear()
                val childs = snapshot.children

                for (snap in childs)
                {
                    listSongRecent[snap.key!!] = snap.getValue(ItemDisplayData::class.java)!!
                }

                sliderRecentAdapter.listItemDisplayData = listSongRecent.values.toList()
                sliderRecentAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        val fm = this@HomeFragment.activity?.supportFragmentManager
        val transaction = fm?.beginTransaction()
        transaction?.remove(loadingFragment)
        transaction?.commit()
    }
}