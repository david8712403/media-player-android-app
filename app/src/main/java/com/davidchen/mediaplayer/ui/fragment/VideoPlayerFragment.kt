package com.davidchen.mediaplayer.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.davidchen.mediaplayer.BuildConfig.italkutalk_guest_key
import com.davidchen.mediaplayer.CaptionAdapter
import com.davidchen.mediaplayer.MainActivity
import com.davidchen.mediaplayer.R
import com.davidchen.mediaplayer.data.RawVideoDetail
import com.davidchen.mediaplayer.data.VideoInfo
import com.davidchen.mediaplayer.databinding.FragmentVideoPlayerBinding
import com.davidchen.mediaplayer.util.AlertDialogUtil
import com.davidchen.mediaplayer.util.MyProgressDialog
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

private const val VIDEO_ID = "videoId"

/**
 * A simple [Fragment] subclass.
 * Use the [VideoPlayerFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class VideoPlayerFragment : Fragment() {
    lateinit var caption: Array<VideoInfo.CaptionResult.Result.Caption>
    lateinit var captionMap: MutableMap<Int, VideoInfo.CaptionResult.Result.Caption>
    lateinit var videoId: String
    lateinit var v: FragmentVideoPlayerBinding
    lateinit var adapter: CaptionAdapter
    lateinit var mYouTubePlayer: YouTubePlayer

    private var curTime: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            videoId = it.getString(VIDEO_ID).toString()
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_video_player, container, false)
        v = FragmentVideoPlayerBinding.bind(view)

        val linearLayoutManager = LinearLayoutManager(this.context)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        v.rvCaption.layoutManager = linearLayoutManager
        lifecycle.addObserver(v.youtubeView)

        v.youtubeView.addYouTubePlayerListener(object : YouTubePlayerListener {
            override fun onApiChange(youTubePlayer: YouTubePlayer) {

            }

            override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
                val min = second.div(60).roundToInt()
                val sec = second.rem(60).roundToInt()
                v.youtubeSeekbar.videoCurrentTimeTextView.text = String.format("%d:%02d", min, sec)
                v.youtubeSeekbar.seekBar.progress = second.roundToInt()

                if (curTime != second.roundToInt()) {
                    curTime = second.roundToInt()
                    val c: VideoInfo.CaptionResult.Result.Caption? = captionMap[curTime]
                    if (c != null) {
                        val position = caption.indexOf(c)
//                            Timber.d("scroll to $position")
                        scrollToPosition(v.rvCaption, position)
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
                v.youtubeView.getPlayerUiController().showSeekBar(false)
                v.youtubeSeekbar.showBufferingProgress = true
            }

            override fun onStateChange(
                    youTubePlayer: YouTubePlayer,
                    state: PlayerConstants.PlayerState
            ) {

            }

            override fun onVideoDuration(youTubePlayer: YouTubePlayer, duration: Float) {
                v.youtubeSeekbar.seekBar.max = duration.roundToInt()
                val min = duration.div(60).roundToInt()
                val sec = duration.rem(60).roundToInt()
                v.youtubeSeekbar.videoDurationTextView.text = String.format("%d:%02d", min, sec)

                v.youtubeSeekbar.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                        if (curTime != progress) {
                            curTime = progress
                            val c: VideoInfo.CaptionResult.Result.Caption? = captionMap[curTime]
                            if (c != null) {
                                val position = caption.indexOf(c)
//                            Timber.d("scroll to $position")
                                scrollToPosition(v.rvCaption, position)
                            }
                        }
                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar?) {
                        mYouTubePlayer.pause()
                    }

                    override fun onStopTrackingTouch(seekBar: SeekBar?) {
                        seekBar?.progress?.toFloat()?.let { mYouTubePlayer.seekTo(it) }
                        mYouTubePlayer.play()
                    }

                })
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

        return v.root
    }

    private fun scrollToPosition(recyclerView: RecyclerView, position: Int) {
        activity?.runOnUiThread {
            recyclerView.smoothScrollToPosition(position)
            adapter.selectItem(position)
            adapter.notifyDataSetChanged()
        }
    }

    private fun sendVideoDetailRequest() {
        val guestKey = italkutalk_guest_key
        val videoID = videoId
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

        (activity as MainActivity).mProgressDialog.show("loading...")

        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                (activity as MainActivity).mProgressDialog.dismiss()
                AlertDialogUtil.showAlertDialog(requireContext(), e.message.toString())
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.let {
                    val rawVideoDetail = Gson().fromJson(it.string(), RawVideoDetail::class.java)
                    val videoInfo = rawVideoDetail.result.videoInfo
                    caption = videoInfo
                            .captionResult
                            .results[0]
                            .captions
                    captionMap = mutableMapOf()
                    for (c in caption) {
                        captionMap[c.time] = c
                    }
                    activity?.runOnUiThread {
                        adapter = CaptionAdapter(caption)
                        v.rvCaption.adapter = adapter
                        // onClick callback回傳該字幕索引值及影片時間
                        adapter.callback = object : CaptionAdapter.Callback {
                            override fun onClick(position: Int, time: Int) {
                                Timber.d("click item:$position")
                                scrollToPosition(v.rvCaption, position)
                                mYouTubePlayer.seekTo(time.toFloat())
                            }
                        }
                        mYouTubePlayer.loadVideo(videoInfo.getVideoId(), 0f)
                    }
                }
                (activity as MainActivity).mProgressDialog.dismiss()
            }

        })
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param videoId Parameter 1.
         * @return A new instance of fragment VideoFragment.
         */
        @JvmStatic
        fun newInstance(videoId: String) =
            VideoPlayerFragment().apply {
                arguments = Bundle().apply {
                    putString(VIDEO_ID, videoId)
                }
            }
    }
}