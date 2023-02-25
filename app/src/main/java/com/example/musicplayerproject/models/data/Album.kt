package com.example.musicplayerproject.models.data

import org.json.JSONObject

class Album : java.io.Serializable {
    lateinit var encodeId: String
    lateinit var title: String
    lateinit var thumbnail: String
    lateinit var artists: List<Artist>

    constructor()

    companion object
    {
        fun parseAlbumViaJsonObject(albumJSONObject: JSONObject): Album
        {
            var album = Album()
            return album
        }
    }
}