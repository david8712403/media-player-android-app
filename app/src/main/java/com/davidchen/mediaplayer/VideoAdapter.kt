package com.davidchen.mediaplayer

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.davidchen.mediaplayer.data.RawVideoList

class VideoAdapter(private val videos: Array<RawVideoList.Result.Video>):
    RecyclerView.Adapter<VideoAdapter.ViewHolder>() {

    lateinit var v: View
    var callback: Callback? = null

    class ViewHolder(v: View): RecyclerView.ViewHolder(v) {
        val ivImg = v.findViewById<ImageView>(R.id.iv_image)
        val tvTitle = v.findViewById<TextView>(R.id.tv_title)
        val tvDuration = v.findViewById<TextView>(R.id.tv_duration)
        val tvCollection = v.findViewById<TextView>(R.id.tv_collection)
        val tvViewer = v.findViewById<TextView>(R.id.tv_viewer)
        val tvPublishedAt = v.findViewById<TextView>(R.id.tv_published_at)
        val pg_loading = v.findViewById<ProgressBar>(R.id.pb_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        v = LayoutInflater.from(parent.context).inflate(R.layout.item_video, parent, false)
        return VideoAdapter.ViewHolder(v)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val video = videos[position]
        val context = holder.itemView.context

        // video thumbnail
        Glide.with(context)
            .load(video.videoInfo.thumbnails)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    holder.ivImg.setImageDrawable(
                        context.getDrawable(android.R.drawable.stat_notify_error)
                    )
                    holder.pg_loading.visibility = View.INVISIBLE
                    return true
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    holder.ivImg.setImageDrawable(resource)
                    holder.pg_loading.visibility = View.INVISIBLE
                    return true
                }

            })
            .into(holder.ivImg)

        // video texts
        holder.tvTitle.text = video.videoInfo.titleSimple
        holder.tvCollection.text = "${video.collection}${context.getString(R.string.collection_num)}"
        holder.tvViewer.text = "${video.viewer}${context.getString(R.string.viewer_num)}"
        holder.tvDuration.text = video.videoInfo.duration
        holder.tvPublishedAt.text = video.videoInfo.getPublishTime()

        holder.itemView.setOnClickListener {
            callback?.onClick(video.videoID)
        }
    }

    override fun getItemCount(): Int {
        return videos.size
    }

    interface Callback {
        fun onClick(videoId: String)
    }
}