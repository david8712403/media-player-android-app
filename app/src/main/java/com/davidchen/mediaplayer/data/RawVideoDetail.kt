package com.davidchen.mediaplayer.data

class RawVideoDetail {
    lateinit var result: Result

    class Result {
        lateinit var videoInfo: VideoInfo
    }
}