package com.example.musicplayerproject.models.data

import android.content.Context
import android.os.AsyncTask
import android.os.Build
import android.util.Log
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.annotation.RequiresApi
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import org.json.JSONObject
import java.io.IOException
import java.security.InvalidKeyException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.security.SignatureException
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec


class ZingAPI: Callback {

    class Builder(): AsyncTask<Void, Void, String>()
    {
        override fun doInBackground(vararg p0: Void?): String {

            return ""
        }

        override fun onProgressUpdate(vararg values: Void?) {
            super.onProgressUpdate(*values)
        }

    }

    interface OnRequestCompleteListener{
        fun onSuccess(call: Call, response: String)
        fun onError(call: Call, e: IOException)
    }

    private lateinit var version: String
    private lateinit var url: String
    private lateinit var secretKey: String
    private lateinit var apiKey: String
    private lateinit var ctime: String
    private lateinit var cookieGot:String

    private var onRequestCompleteListener : OnRequestCompleteListener? =null
    private lateinit var response: String

    private constructor(version: String, url: String, secretKey:String, apiKey:String, ctime:String) {
        this.version = version
        this.url = url
        this.secretKey = secretKey
        this.apiKey = apiKey
        this.ctime = ctime
    }

    private lateinit var context: Context

    @RequiresApi(Build.VERSION_CODES.O)
    val nowInUtc = OffsetDateTime.now(ZoneOffset.UTC)


    private object Holder {
        val instance = ZingAPI(
        "1.6.34", // VERSION
        "https://zingmp3.vn", // URL
        "2aa2d1c561e809b267f3638c4a307aab", // SECRET_KEY
        "88265e23d4284f25963e6eedac8fbfa3", // API_KEY
        (Math.floor((Calendar.getInstance(TimeZone.getTimeZone("UTC")).timeInMillis).toDouble() / 1000)).toString() // CTIME

    )
    }


    companion object
    {
        public fun getInstance(context: Context): ZingAPI
        {
            Holder.instance.context = context

            return Holder.instance
        }


    }

    fun getSongByID(songID: String, callback: OnRequestCompleteListener)
    {
        this.onRequestCompleteListener = callback
        val webViewClient: WebViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String?) {
                super.onPageFinished(view, url)
                cookieGot = CookieManager.getInstance().getCookie(view.getUrl())
                Log.i("Cookie", cookieGot)
                var destination = "/api/v2/song/get/streaming"
                var params = mutableMapOf<String, String>()
                params.put("id", songID)
                params.put("sig", hashParams(destination, songID)!!)
                request(destination, params)
            }
        }
        var webv = WebView(context)
        webv.webViewClient = webViewClient
        webv.loadUrl("https://zingmp3.vn")
    }

    fun search(name: String, callback: OnRequestCompleteListener)
    {
        this.onRequestCompleteListener = callback
        val webViewClient: WebViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String?) {
                super.onPageFinished(view, url)
                cookieGot = CookieManager.getInstance().getCookie(view.getUrl())
                Log.i("Cookie", cookieGot)
                var destination = "/api/v2/search/multi"
                var params = mutableMapOf<String, String>()
                params.put("q", name)
                params.put("sig", hashParamsNoID(destination)!!)
                request(destination, params)
            }
        }
        var webv = WebView(context)
        webv.webViewClient = webViewClient
        webv.loadUrl("https://zingmp3.vn")
    }

    fun getHome(callback: OnRequestCompleteListener)
    {
        this.onRequestCompleteListener = callback
        val webViewClient: WebViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String?) {
                super.onPageFinished(view, url)
                cookieGot = CookieManager.getInstance().getCookie(view.getUrl())
                Log.i("Cookie", cookieGot)
                var destination = "/api/v2/page/get/home"
                var params = mutableMapOf<String, String>()
                params.put("page", "1")
                params.put("segmentId", "-1")
                params.put("count", "30")
                params.put("sig", hashParamsHome(destination)!!)
                request(destination, params)
            }
        }
        var webv = WebView(context)
        webv.webViewClient = webViewClient
        webv.loadUrl("https://zingmp3.vn")
    }

    fun getTop100(callback: OnRequestCompleteListener)
    {
        this.onRequestCompleteListener = callback
        val webViewClient: WebViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String?) {
                super.onPageFinished(view, url)
                cookieGot = CookieManager.getInstance().getCookie(view.getUrl())
                Log.i("Cookie", cookieGot)
                var destination = "/api/v2/page/get/top-100"
                var params = mutableMapOf<String, String>()
                params.put("sig", hashParamsNoID(destination)!!)
                request(destination, params)
            }
        }
        var webv = WebView(context)
        webv.webViewClient = webViewClient
        webv.loadUrl("https://zingmp3.vn")
    }

    fun getArtist(name: String, callback: OnRequestCompleteListener)
    {
        this.onRequestCompleteListener = callback
        val webViewClient: WebViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String?) {
                super.onPageFinished(view, url)
                cookieGot = CookieManager.getInstance().getCookie(view.getUrl())
                Log.i("Cookie", cookieGot)
                var destination = "/api/v2/page/get/artist"
                var params = mutableMapOf<String, String>()
                params.put("alias", name)
                params.put("sig", hashParamsNoID(destination)!!)
                request(destination, params)
            }
        }
        var webv = WebView(context)
        webv.webViewClient = webViewClient
        webv.loadUrl("https://zingmp3.vn")
    }

    fun getLyric(songID: String, callback: OnRequestCompleteListener)
    {
        this.onRequestCompleteListener = callback
        val webViewClient: WebViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String?) {
                super.onPageFinished(view, url)
                cookieGot = CookieManager.getInstance().getCookie(view.getUrl())
                Log.i("Cookie", cookieGot)
                var destination = "/api/v2/lyric/get/lyric"
                var params = mutableMapOf<String, String>()
                params.put("id", songID)
                params.put("sig", hashParams(destination, songID)!!)
                request(destination, params)
            }
        }
        var webv = WebView(context)
        webv.webViewClient = webViewClient
        webv.loadUrl("https://zingmp3.vn")
    }

    fun getVideo(videoID: String, callback: OnRequestCompleteListener)
    {
        this.onRequestCompleteListener = callback
        val webViewClient: WebViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String?) {
                super.onPageFinished(view, url)
                cookieGot = CookieManager.getInstance().getCookie(view.getUrl())
                Log.i("Cookie", cookieGot)
                var destination = "/api/v2/page/get/video"
                var params = mutableMapOf<String, String>()
                params.put("id", videoID)
                params.put("sig", hashParams(destination, videoID)!!)
                request(destination, params)
            }
        }
        var webv = WebView(context)
        webv.webViewClient = webViewClient
        webv.loadUrl("https://zingmp3.vn")
    }

    fun getPlaylist(playlistID: String, callback: OnRequestCompleteListener)
    {
        this.onRequestCompleteListener = callback
        val webViewClient: WebViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String?) {
                super.onPageFinished(view, url)
                cookieGot = CookieManager.getInstance().getCookie(view.getUrl())
                Log.i("Cookie", cookieGot)
                var destination = "/api/v2/page/get/playlist"
                var params = mutableMapOf<String, String>()
                params.put("id", playlistID)
                params.put("sig", hashParams(destination, playlistID)!!)
                request(destination, params)
            }
        }
        var webv = WebView(context)
        webv.webViewClient = webViewClient
        webv.loadUrl("https://zingmp3.vn")
    }


    private fun hashMac256(input: String): String {
        return MessageDigest
            .getInstance("SHA-256")
            .digest(input.toByteArray())
            .fold("") { str, it -> str + "%02x".format(it) }
    }

    private val HMAC_SHA512 = "HmacSHA512"

    private fun toHexString(bytes: ByteArray): String? {
        val formatter = Formatter()
        for (b in bytes) {
            formatter.format("%02x", b)
        }
        return formatter.toString()
    }

    @Throws(SignatureException::class, NoSuchAlgorithmException::class, InvalidKeyException::class)
    fun hashMac512(data: String, key: String): String? {
        val secretKeySpec = SecretKeySpec(key.toByteArray(), HMAC_SHA512)
        val mac: Mac = Mac.getInstance(HMAC_SHA512)
        mac.init(secretKeySpec)
        return toHexString(mac.doFinal(data.toByteArray()))
    }

    fun hashParams(path: String, id: String):String?
    {
        return hashMac512(
            path + hashMac256("ctime=${this.ctime}id=${id}version=${this.version}"),
            secretKey
        )
    }

    fun hashParamsNoID(path: String): String?
    {
        return hashMac512(
            path + hashMac256("ctime=${this.ctime}version=${this.version}"),
            secretKey
        )
    }

    fun hashParamsHome(path: String): String?
    {
        return this.hashMac512(
            path + this.hashMac256("count=30ctime=${this.ctime}page=1version=${this.version}"),
            this.secretKey
        )
    }

    fun request(destination:String, params: Map<String, String>)
    {
        var client = OkHttpClient()

        var requestURL = (this.url + destination).toHttpUrlOrNull()?.newBuilder()
        for (entry in params.entries.iterator()) {
            print("${entry.key} : ${entry.value}")
            requestURL?.addQueryParameter(entry.key, entry.value)
        }
        requestURL?.addQueryParameter("ctime", ctime)
        requestURL?.addQueryParameter("version", version)
        requestURL?.addQueryParameter("apiKey", apiKey)

        var fullURL = requestURL?.build().toString()

        Log.i("Url Request: ", fullURL)

        var rq = Request.Builder()
                .url(fullURL)
                .header("Cookie", cookieGot)
                .get()
                .build()


        client.newCall(rq!!).enqueue(this)

    }

    override fun onFailure(call: Call, e: IOException) {
        Toast.makeText(context, "Request api fail", Toast.LENGTH_LONG).show()
        onRequestCompleteListener?.onError(call, e)

    }
    override fun onResponse(call: Call, response: Response) {
        val myResponse = response.body!!.string()
        Log.i("Response Zing:", myResponse.toString())
        val json = JSONObject(myResponse)
        onRequestCompleteListener?.onSuccess(call, myResponse)

    }
}