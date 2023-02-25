package com.example.musicplayerproject.models.data

import org.json.JSONObject

class Video : java.io.Serializable {
    lateinit var encodeId: String
    lateinit var title: String
    lateinit var artistNames: String
    lateinit var thumbnail: String
    lateinit var streamingLink: String

    constructor()

    constructor(
        encodeId: String,
        title: String,
        artistNames: String,
        thumbnail: String,
        streamingLink: String
    ) {
        this.encodeId = encodeId
        this.title = title
        this.artistNames = artistNames
        this.thumbnail = thumbnail
        this.streamingLink = streamingLink
    }

    constructor(encodeId: String, title: String, artistNames: String, streamingLink: String) {
        this.encodeId = encodeId
        this.title = title
        this.artistNames = artistNames
        this.streamingLink = streamingLink
    }


    companion object
    {
        fun parseVideoViaJsonObject(videoJSONObject: JSONObject): Video
        {
            var vid = Video()
            vid.encodeId = videoJSONObject.getString("encodeId")
            vid.title = videoJSONObject.getString("title")
            vid.artistNames = videoJSONObject.getString("artistsNames")
            vid.thumbnail = videoJSONObject.getString("thumbnail")
            return vid
        }
    }



}