package com.example.musicplayerproject.models.data

import org.json.JSONObject

class Song : java.io.Serializable {
    lateinit var encodeId: String
    lateinit var title: String
    var alias: String = ""
    var artistsNames: String = ""
    lateinit var artists: MutableList<Artist>
    lateinit var albums: MutableList<Album>
    lateinit var thumbnail: String
    lateinit var linkQuality128: String
    lateinit var linkQuality320: String
    lateinit var lyric: SongLyric
    lateinit var streamingLink: String

    constructor()

    constructor(songID: Long, songTitle: String, songArtist: String, thumbNail: String, link: String) {
        encodeId = songID.toString()
        title = songTitle
        artistsNames = songArtist
        thumbnail = thumbNail
        streamingLink = link
    }

    companion object
    {
        fun parseSongViaJsonObject(songJSONObject: JSONObject): Song
        {
            var song = Song()
            song.encodeId = songJSONObject.getString("encodeId")
            song.title = songJSONObject.getString("title")
            song.thumbnail = songJSONObject.getString("thumbnail")
            song.artists = ArrayList()
            song.artistsNames = songJSONObject.getString("artistsNames")
            var artists = songJSONObject.getJSONArray("artists")
            for (j in 0 until artists.length())
            {
                var artist = Artist()
                var art = artists.getJSONObject(j)
                Artist.parseArtistViaJsonObject(art)
                song.artists.add(artist)
            }
            return song
        }
    }

}