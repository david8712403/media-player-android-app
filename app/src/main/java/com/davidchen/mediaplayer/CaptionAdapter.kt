package com.davidchen.mediaplayer

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.davidchen.mediaplayer.data.VideoInfo

class CaptionAdapter(private val caption: Array<VideoInfo.CaptionResult.Result.Caption>):
    RecyclerView.Adapter<CaptionAdapter.ViewHolder>(){

    lateinit var v: View
    var items = ArrayList<ViewHolder>()
    var callback: Callback? = null

    class ViewHolder(v: View): RecyclerView.ViewHolder(v) {
        val tvCaption = v.findViewById<TextView>(R.id.tv_caption)
        val tvCaptionNum = v.findViewById<TextView>(R.id.tv_caption_no)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        v = LayoutInflater.from(parent.context).inflate(R.layout.item_caption, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: CaptionAdapter.ViewHolder, position: Int) {
        items.add(holder)
        holder.tvCaption.text = caption[position].content
        holder.tvCaptionNum.text = position.plus(1).toString()
        holder.itemView.setOnClickListener {
            callback?.onClick(position, caption[position].time)
        }
    }

    override fun getItemCount(): Int {
        return caption.size
    }

    fun selectItem(position: Int?) {
        for (i in items) {
            i.itemView.setBackgroundColor(v.context.getColor(android.R.color.transparent))
            i.tvCaption.typeface = Typeface.DEFAULT
        }
        if (position != null) {
            items[position].itemView.setBackgroundColor(v.context.getColor(android.R.color.darker_gray))
            items[position].tvCaption.typeface = Typeface.DEFAULT_BOLD
        }
    }

    interface Callback {
        fun onClick(position: Int, time: Int)
    }
}