package com.example.musicplayerproject.models.data

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject

class SongLyric : java.io.Serializable {
    class Word
    {
        var startTime = 0f
        var endTime = 0f
        var data = ""
    }

    var sentences: MutableList<MutableList<Word>> = mutableListOf<MutableList<Word>>()

    var streamingURL = ""

    companion object
    {
        fun parseData(data: JSONObject): SongLyric
        {
            val songLyric = SongLyric()
            if (data.toString().contains("streamingUrl")) {
                songLyric.streamingURL = data.getString("streamingUrl")
            }

            try {
                val gson = Gson()
                val sentenceObject = data.getJSONArray("sentences")
                for (i in 0 until sentenceObject.length())
                {
                    val listWordType = object: TypeToken<MutableList<Word>>() {}.type
                    var s = sentenceObject.toString()
                    val listWord : MutableList<Word> = gson.fromJson(sentenceObject.getJSONObject(i).getJSONArray("words").toString(), listWordType)
                    songLyric.sentences.add(listWord)
                }
            }
            catch (e: Exception)
            {
                return  songLyric
            }

            return songLyric

        }
    }


}