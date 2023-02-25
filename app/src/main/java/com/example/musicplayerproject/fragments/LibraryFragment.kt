package com.example.musicplayerproject.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentUris
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayerproject.R
import com.example.musicplayerproject.adapters.LibraryAdapter
import com.example.musicplayerproject.models.SpeechToText
import com.example.musicplayerproject.models.data.Song


class LibraryFragment : Fragment() {
    private lateinit var recyclerAdapter: LibraryAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchButton: ImageButton
    private lateinit var editText: EditText
    private lateinit var clearText: ImageButton
    private lateinit var voiceSearch: ImageButton
    private lateinit var buttonName: Button
    private lateinit var buttonArtist: Button

    private var isRecording = false
    private var songs = mutableListOf<Song>()
    private val sArtworkUri = Uri.parse("content://media/external/audio/albumart")
    private var mode = 0    //0 = by name, 1 = by artist

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_library, container, false)
        viewFinder(view)
        recyclerAdapter = LibraryAdapter(context!!)
        recyclerView.adapter = recyclerAdapter
        recyclerView.layoutManager = LinearLayoutManager(getView()?.context)

        getSongs("")

        searchButton.setOnClickListener{
            if (editText.text.toString() == "") {
                Toast.makeText(container?.context, "No search entries yet!", Toast.LENGTH_SHORT).show()
            } else {
                getSongs(editText.text.toString())
            }
        }

        clearText.setOnClickListener {
            editText.setText("")
            getSongs("")
        }

        voiceSearch.setOnClickListener {
            onVoiceSearching(voiceSearch)
        }
        filterSetup()
        return view
    }

    private fun viewFinder(view: View) {
        recyclerView = view.findViewById(R.id.library_list)
        searchButton = view.findViewById(R.id.search_library_button)
        editText = view.findViewById(R.id.edit_library)
        clearText = view.findViewById(R.id.deleteEditTextLibrary)
        voiceSearch = view.findViewById(R.id.voiceSearchLibrary)
        buttonName = view.findViewById(R.id.buttonName)
        buttonArtist = view.findViewById(R.id.buttonArtist)
    }

    private fun filterSetup() {
        buttonArtist.setTextColor(Color.GRAY)
        buttonName.setOnClickListener {
            buttonName.setTextColor(Color.WHITE)
            buttonArtist.setTextColor(Color.GRAY)
            mode = 0
        }
        buttonArtist.setOnClickListener {
            buttonName.setTextColor(Color.GRAY)
            buttonArtist.setTextColor(Color.WHITE)
            mode = 1
        }
    }

    fun onVoiceSearching(view: View)
    {


        var s2t = SpeechToText.getInstances(context!!, editText)

        if (ContextCompat.checkSelfPermission(context!!,
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(activity!!,
                Array<String>(1) { Manifest.permission.RECORD_AUDIO},
                200);
        }
        else if (!isRecording)
        {
            isRecording = true

            s2t.startListening()
        }
        else
        {
            isRecording = false
            s2t.stopListening()
            getSongs(editText.text.toString().trimEnd())
        }
    }

    @SuppressLint("Recycle")
    private fun getSongs(searchQuery: String) {
        songs.clear()
        val contentResolver: ContentResolver? = context?.contentResolver
        val musicUri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val musicCursor: Cursor? = contentResolver?.query(musicUri, null, null, null, null)
        if (musicCursor != null && musicCursor.moveToFirst()) {
            //get columns
            val titleColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val idColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media._ID)
            val albumID = musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)
            val artistColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            val songLink = musicCursor.getColumnIndex(MediaStore.Audio.Media.DATA)
            val duration = musicCursor.getColumnIndex(MediaStore.Audio.Media.DURATION)
            //add songs to list
            do {
                val thisId = musicCursor.getLong(idColumn)
                var thisTitle = musicCursor.getString(titleColumn)
                var thisArtist = musicCursor.getString(artistColumn)
                if (thisArtist.equals("<unknown>")) {
                    thisArtist = "Unknown Artists"
                }

                if (thisTitle.contains("MusicPlayerProject")) {
                    var temp = thisTitle.substringAfter("_")
                    thisTitle = temp.substringBefore("_")
                    thisArtist = temp.substringAfter("_by_").substringBefore("-")
                    Log.v("Library", temp)
                }
                val thisSongLink = Uri.parse(musicCursor.getString(songLink))
                val some = musicCursor.getLong(albumID)
                val uri = ContentUris.withAppendedId(sArtworkUri, some)
                val thisDuration = musicCursor.getInt(duration)
                when (mode) {
                    0 -> if (thisDuration >= 10000 && (thisTitle.contains(searchQuery, true) || searchQuery == "")) {
                        songs.add(
                            Song(
                                thisId, thisTitle, thisArtist, uri.toString(),
                                thisSongLink.toString()
                            )
                        )
                    }
                    1 -> if (thisDuration >= 10000 && (thisArtist.contains(searchQuery, true) || searchQuery == "")) {
                        songs.add(
                            Song(
                                thisId, thisTitle, thisArtist, uri.toString(),
                                thisSongLink.toString()
                            )
                        )
                    }
                }
            } while (musicCursor.moveToNext())
        }
        assert(musicCursor != null)
        musicCursor!!.close()
        for (i in 0 until songs.size - 1) {
            for (j in i + 1 until songs.size) {
                if (songs[i].title.lowercase() > songs[j].title.lowercase()) {
                    val temp: Song = songs[j]
                    songs[j] = songs[i]
                    songs[i] = temp
                }
            }
        }
        recyclerAdapter.addLibrary(songs)
        recyclerAdapter.notifyDataSetChanged()
        Toast.makeText(this.context, songs.size.toString() + " Songs Found!!!", Toast.LENGTH_SHORT).show()
    }
}