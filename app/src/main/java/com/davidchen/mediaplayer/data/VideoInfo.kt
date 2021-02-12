package com.davidchen.mediaplayer.data

import java.io.Serializable

class VideoInfo : Serializable {
    val videourl: String = ""
    val title: String = ""
    var duration: Int = 0
    lateinit var captionResult: CaptionResult

    fun getVideoId(): String {
        val str = videourl.split("v=")
        var id = ""
        id = if (str.size > 1) {
            str[1]
        }else {
            str[0]
        }
        val ampersandPosition = id.indexOf('&')
        if (ampersandPosition != -1) {
            id = id.substring(0, ampersandPosition)
        }
        if (id.length > 11) {
            id = id.substring(id.length - 11, id.length)
        }
        return id
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