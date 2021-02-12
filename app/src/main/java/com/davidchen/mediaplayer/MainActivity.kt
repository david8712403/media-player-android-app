package com.davidchen.mediaplayer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.davidchen.mediaplayer.data.RawVideoDetail
import com.davidchen.mediaplayer.data.VideoInfo
import com.davidchen.mediaplayer.databinding.ActivityMainBinding
import com.google.gson.Gson
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import timber.log.Timber
import java.io.IOException
import kotlin.math.roundToInt


class MainActivity : AppCompatActivity() {

    lateinit var v: ActivityMainBinding
    lateinit var adapter: CaptionAdapter
    lateinit var mYouTubePlayer: YouTubePlayer
    lateinit var caption: Array<VideoInfo.CaptionResult.Result.Caption>

    private var curTime: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        v = ActivityMainBinding.inflate(layoutInflater)
        setContentView(v.root)

        Timber.plant(Timber.DebugTree())

        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        v.rvCaption.layoutManager = linearLayoutManager

        lifecycle.addObserver(v.youtubeView)
        v.youtubeView.addYouTubePlayerListener(object : YouTubePlayerListener {
            override fun onApiChange(youTubePlayer: YouTubePlayer) {

            }

            override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
                if (curTime != second.roundToInt()) {
                    curTime = second.roundToInt()
                    for (c in caption) {
                        if (c.time == second.roundToInt()) {
                            val position = caption.indexOf(c)
//                            Timber.d("scroll to $position")
                            scrollToPosition(v.rvCaption, position)
                        }
                    }
                }
            }

            override fun onError(youTubePlayer: YouTubePlayer, error: PlayerConstants.PlayerError) {
                Timber.d("error:${error.name}")
            }

            override fun onPlaybackQualityChange(
                youTubePlayer: YouTubePlayer,
                playbackQuality: PlayerConstants.PlaybackQuality
            ) {

            }

            override fun onPlaybackRateChange(
                youTubePlayer: YouTubePlayer,
                playbackRate: PlayerConstants.PlaybackRate
            ) {

            }

            override fun onReady(youTubePlayer: YouTubePlayer) {
                Timber.d("ready")
                sendVideoDetailRequest()
                mYouTubePlayer = youTubePlayer
            }

            override fun onStateChange(
                youTubePlayer: YouTubePlayer,
                state: PlayerConstants.PlayerState
            ) {

            }

            override fun onVideoDuration(youTubePlayer: YouTubePlayer, duration: Float) {
                Timber.d("duration: $duration")
            }

            override fun onVideoId(youTubePlayer: YouTubePlayer, videoId: String) {
                Timber.d("id:$videoId")
            }

            override fun onVideoLoadedFraction(
                youTubePlayer: YouTubePlayer,
                loadedFraction: Float
            ) {
            }

        })

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

    private fun scrollToPosition(recyclerView: RecyclerView, position: Int) {
        (recyclerView.adapter as CaptionAdapter).selectItem(position)
        recyclerView.smoothScrollToPosition(position)
    }

    private fun sendVideoDetailRequest() {
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
                response.body?.let {
                    val rawVideoDetail = Gson().fromJson(it.string(), RawVideoDetail::class.java)
                    val videoInfo = rawVideoDetail.result.videoInfo
                    caption = videoInfo
                        .captionResult
                        .results[0]
                        .captions
                    runOnUiThread {
                        adapter = CaptionAdapter(caption)
                        v.rvCaption.adapter = adapter
                        // onClick callback回傳該字幕索引值及影片時間
                        adapter.callback = object : CaptionAdapter.Callback {
                            override fun onClick(position: Int, time: Int) {
                                scrollToPosition(v.rvCaption, position)
                                mYouTubePlayer.seekTo(time.toFloat())
                            }
                        }
                        mYouTubePlayer.loadVideo(videoInfo.getVideoId(), 0f)
                    }
                }
                ProgressDialogUtil.dismiss()
            }

        })
    }
}