package com.davidchen.mediaplayer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.davidchen.mediaplayer.databinding.ActivityMainBinding
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import timber.log.Timber
import java.io.IOException

class MainActivity : AppCompatActivity() {

    lateinit var v: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        v = ActivityMainBinding.inflate(layoutInflater)
        setContentView(v.root)

        Timber.plant(Timber.DebugTree())

        lifecycle.addObserver(v.youtubeView)
        v.youtubeView.addYouTubePlayerListener(object : YouTubePlayerListener {
            override fun onApiChange(youTubePlayer: YouTubePlayer) {
                TODO("Not yet implemented")
            }

            override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
                TODO("Not yet implemented")
            }

            override fun onError(youTubePlayer: YouTubePlayer, error: PlayerConstants.PlayerError) {
                TODO("Not yet implemented")
            }

            override fun onPlaybackQualityChange(youTubePlayer: YouTubePlayer, playbackQuality: PlayerConstants.PlaybackQuality) {
                TODO("Not yet implemented")
            }

            override fun onPlaybackRateChange(youTubePlayer: YouTubePlayer, playbackRate: PlayerConstants.PlaybackRate) {
                TODO("Not yet implemented")
            }

            override fun onReady(youTubePlayer: YouTubePlayer) {
                TODO("Not yet implemented")
            }

            override fun onStateChange(youTubePlayer: YouTubePlayer, state: PlayerConstants.PlayerState) {
                TODO("Not yet implemented")
            }

            override fun onVideoDuration(youTubePlayer: YouTubePlayer, duration: Float) {
                TODO("Not yet implemented")
            }

            override fun onVideoId(youTubePlayer: YouTubePlayer, videoId: String) {
                TODO("Not yet implemented")
            }

            override fun onVideoLoadedFraction(youTubePlayer: YouTubePlayer, loadedFraction: Float) {
                TODO("Not yet implemented")
            }

        })

        sendRequest()
        ProgressDialogUtil.showProgressDialog(this, "loading...")
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount == 0 ) {
            finish()
            if (ProgressDialogUtil.mAlertDialog?.isShowing == false) {
                ProgressDialogUtil.mAlertDialog = null
            }
        }
    }

    private fun sendRequest() {
        val guestKey = "44f6cfed-b251-4952-b6ab-34de1a599ae4"
        val videoID = "5edfb3b04486bc1b20c2851a"
        val mode = 1

        val httpUrl = HttpUrl.Builder()
            .scheme("https")
            .host("api.italkutalk.com")
            .addPathSegment("api")
            .addPathSegment("video")
            .addPathSegment("detail")

        val body = JSONObject()
            .put("guestKey", guestKey)
            .put("videoID", videoID)
            .put("mode", mode)
            .toString()
            .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        val request = Request.Builder()
            .url(httpUrl.build())
            .addHeader("Content-Type", "application/json")
            .post(body)
            .build()

        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                AlertDialogUtil.showAlertDialog(this@MainActivity, e.message.toString())
            }

            override fun onResponse(call: Call, response: Response) {
                ProgressDialogUtil.dismiss()
                response.body?.let {
                    Timber.d("response: ${it.string()}")
                }
            }

        })
    }
}