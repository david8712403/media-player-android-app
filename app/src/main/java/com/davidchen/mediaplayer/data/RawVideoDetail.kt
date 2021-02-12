package com.davidchen.mediaplayer.data

import java.io.Serializable

class RawVideoDetail : Serializable {
    lateinit var result: Result

    class Result : Serializable {
        lateinit var videoInfo: VideoInfo
    }
}