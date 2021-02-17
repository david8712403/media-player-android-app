# 多媒體播放App
> 北科大MMS實驗室 - Android新生教育訓練homework實作紀錄
> (非專業技術文章，僅作為筆記，方便往後個人查閱及思路整理，排版不佳還請見諒)

## App功能分析
> 2021/2/11
1. 播放Youtube影片
2. 播放時會自動將字幕列表移動到相對應的字幕，用灰色標註，並數字標註字幕編號
3. 點擊字幕，影片自動切換至對應時間軸播放

## 字幕內容API串接
> 2021/2/11
### 取得字幕內容API
使用API `https://api.italkutalk.com/api/video/detail` 取得字幕內容。

#### Http request body, header
`request body`內容如下：
```
{
    "guestKey":"44fxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxe4",
    "videoID": "5edfb3b04486bc1b20c2851a",
    "mode": 1
}
```
加入`header`：
```
Content-Type: application/json
```

#### Response body資料
`response body`中的字幕資料會有該行字幕時間及內容，格式如下：
```
"results": [
    {
        "language": "en",
        "captions": [
            {
                "time": 0,
                "content": "A woman gets on a bus with her baby",
            },
            {
                "time": 3,
                "content": "The driver looks at the baby and says",
            },
            ...
        ]
    }
]
```
#### 撰寫發送http request副程式
`sendVideoDetailRequest()`
```
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

    OkHttpClient().newCall(request).enqueue(object : Callback { 
        ...
    })
}
```

## Third party library study
> 2021/2/11
### Youtube SDK
官方的Youtube SDK sample code及文件已經許久未更新（最久將近5年）專案也不是使用我熟知的`gradle`專案自動化建構工具，加上有找到熱心網友將Android Youtube Player([PierfrancescoSoffritti/android-youtube-player](https://github.com/PierfrancescoSoffritti/android-youtube-player))函式庫開源讓大家使用，Github`Stargazers`也達到2k，果斷選擇了這個作為這次專案使用的Youtube player library。

#### Gradle引入函式庫
```
implementation 'com.pierfrancescosoffritti.androidyoutubeplayer:core:10.0.5'
```
#### 加入youtube player view
```
<com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
    android:id="@+id/youtube_view"
    android:layout_width="match_parent"
    android:layout_height="wrap_content" />
```
#### 加入`YoutubePlayerListener`
```
lifecycle.addObserver(v.youtubeView)
v.youtubeView.addYouTubePlayerListener(object : YouTubePlayerListener {
    override fun onApiChange(youTubePlayer: YouTubePlayer) { ... }

    override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) { ... }

    override fun onError(youTubePlayer: YouTubePlayer, error: PlayerConstants.PlayerError) { ... }

    override fun onPlaybackQualityChange(
            youTubePlayer: YouTubePlayer,
            playbackQuality: PlayerConstants.PlaybackQuality
    ) { ... }

    override fun onPlaybackRateChange(
            youTubePlayer: YouTubePlayer,
            playbackRate: PlayerConstants.PlaybackRate
    ) { ... }

    override fun onReady(youTubePlayer: YouTubePlayer) { ... }

    override fun onStateChange(
            youTubePlayer: YouTubePlayer,
            state: PlayerConstants.PlayerState
    ) { ... }

    override fun onVideoDuration(youTubePlayer: YouTubePlayer, duration: Float) { ... }

    override fun onVideoId(youTubePlayer: YouTubePlayer, videoId: String) { ... }

    override fun onVideoLoadedFraction(
            youTubePlayer: YouTubePlayer,
            loadedFraction: Float
    ) { ... }
})
```

#### 初始化YouTubePlayer物件
當`YouTubePlayer` call `onReady`，將instance存入fragment，方便後續取用：
```
override fun onReady(youTubePlayer: YouTubePlayer) {
    sendVideoDetailRequest()
    mYouTubePlayer = youTubePlayer
}
```

#### 每秒確認是否需要更新字幕
此項目在`YouTubePlayerListener`callback function的`onVideoDuration`中處理：
```
override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
    if (curTime != second.roundToInt()) {
        curTime = second.roundToInt()
        for (c in caption) {
            if (c.time == second.roundToInt()) {
                // 捲動至指定字幕item
            }
        }
    }
}
```
經實驗，`onCurrentSecond`約200ms會被呼叫一次，為了避免太頻繁查詢字幕時間及捲動至指定item，將上一秒鐘時間記錄在`curTime`。



## 實作字幕`RecyclerView`及`Apdapter`
> 2021/2/12

### item UI Layout
```
androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="80dp"
    android:focusable="true"
    android:clickable="true"
    android:foreground="?attr/selectableItemBackground" >

    <TextView
        android:id="@+id/tv_caption"
        ... />

    <TextView
        android:id="@+id/tv_caption_no"
        ... />

</androidx.constraintlayout.widget.ConstraintLayout>
```

### `CaptionAdapter`

#### 字幕item的點擊事件

需要實作item點擊事件，在`CaptionAdapter`中新增`Callback`介面，包含`onClick`，並傳回該item中字幕的時間軸秒數。
```
interface Callback {
    fun onClick(position: Int, time: Int)
}
```

當item被點擊時，呼叫`onClick`並回傳時間。
```
override fun onBindViewHolder(holder: CaptionAdapter.ViewHolder, position: Int) {
    ...
    holder.itemView.setOnClickListener {
        callback?.onClick(position, caption[position].time)
    }
}
```

#### 字幕item播放中更換背景顏色
播放到指定字幕時，需將字幕反白的功能，在`Adapter class`中新增`selectPosition`，用來記錄應該要反白的字幕。
再透過執行`onBindViewHolder`時判斷是否是被選擇的字幕。
```
private var selectPosition: Int? = null

override fun onBindViewHolder(holder: CaptionAdapter.ViewHolder, position: Int) {
    ...
    if (position == selectPosition) {
        holder.tvCaption.typeface = Typeface.DEFAULT_BOLD
        holder.itemView.setBackgroundColor(selectColor)
    } else {
        holder.tvCaption.typeface = Typeface.DEFAULT
        holder.itemView.setBackgroundColor(deselectColor)
    }
    ...
}

```


#### 滾動至選擇字幕

實作`scrollToPosition`，將字幕滑動至指定位置並反白指定字幕。
呼叫自訂`adapter`中的`selectItem()`函式，並呼叫`notifyDataSetChanged`更新`RecyclerView`。
```
private fun scrollToPosition(recyclerView: RecyclerView, position: Int) {
    activity?.runOnUiThread {
        recyclerView.smoothScrollToPosition(position)
        adapter.selectItem(position)
        adapter.notifyDataSetChanged()
    }
}
```

## 影片列表及搜尋功能實作 (bonus)
> 2021/2/13

### 影片列表API

#### 列表API
使用API `https://api.italkutalk.com/api/video/list` 取得影片列表資料

#### keyword搜尋影片
在`request body`中加入加入`keyword`欄位
```
{
    "guestKey":"44fxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxe4",
    "keyword": "search"
}
```

### 影片列表

#### item UI Layout

```
<LinearLayout 
    ...>

    <androidx.constraintlayout.widget.ConstraintLayout
        ... >

        <ProgressBar
            android:id="@+id/pb_image"
            ... />

        <ImageView
            android:id="@+id/iv_image"
            ... />

        <TextView
            android:id="@+id/tv_duration"
            ... />

    </androidx.constraintlayout.widget.ConstraintLayout>

    <androidx.constraintlayout.widget.ConstraintLayout
        ... >

        <TextView
            android:id="@+id/tv_title"
            ... />

        <TextView
            android:id="@+id/tv_collection"
            ... />

        <TextView
            android:id="@+id/tv_viewer"
            ... />

        <TextView
            android:id="@+id/tv_published_at"
            ... />

    </androidx.constraintlayout.widget.ConstraintLayout>
</LinearLayout>
```
![](https://i.imgur.com/JYFC0RE.png)
