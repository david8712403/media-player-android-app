package com.davidchen.mediaplayer.data

class VideoInfo {
    val videourl: String = ""
    val title: String = ""
    var duration: Int = 0
    lateinit var captionResult: CaptionResult

    fun getVideoId(): String {
        return videourl.substring(videourl.length - 11, videourl.length)
    }

    class CaptionResult {
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