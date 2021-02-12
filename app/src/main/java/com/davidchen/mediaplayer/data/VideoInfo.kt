package com.davidchen.mediaplayer.data

import java.io.Serializable

class VideoInfo : Serializable {
    val videourl: String = ""
    val title: String = ""
    var duration: Int = 0
    lateinit var captionResult: CaptionResult

    fun getVideoId(): String {
        return videourl.substring(videourl.length - 11, videourl.length)
    }

    class CaptionResult : Serializable {
        val state: Int = 0
        lateinit var results: Array<Result>

        class Result {
            val language: String = ""
            lateinit var captions: Array<Caption>

            class Caption {
                val time: Int = 0
                val content: String = ""
            }
        }
    }
}