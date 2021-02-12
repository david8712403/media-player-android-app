package com.davidchen.mediaplayer.data

import java.io.Serializable

class RawVideoList : Serializable {

    lateinit var result: Result

    class Result : Serializable {
        lateinit var list: Array<Video>

        class Video : Serializable {
            val videoID: String = ""
            val userID: String = ""
            var viewer: Int = 0
            var collection: Int = 0
            lateinit var videoInfo: VideoInfo

            class VideoInfo : Serializable {
                val videourl: String = ""
                val title: String = ""
                val titleSimple: String = ""
                val thumbnails: String = ""
                val description: String = ""
                val duration: String = ""
            }
        }
    }
}