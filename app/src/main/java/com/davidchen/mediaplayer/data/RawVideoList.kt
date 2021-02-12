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
                val publishedAt: String = ""

                fun getPublishTime(): String {
                    return publishedAt.split("T")[0]
                }

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
            }
        }
    }
}