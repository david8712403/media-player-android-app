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
    private var selectPosition: Int? = null
    var items = ArrayList<View>()
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
        val context = holder.itemView.context
        val selectColor = context.getColor(android.R.color.darker_gray)
        val deselectColor = context.getColor(android.R.color.transparent)
        items.add(holder.itemView)
        holder.tvCaption.text = caption[position].content
        holder.tvCaptionNum.text = position.plus(1).toString()
        if (position == selectPosition) {
            holder.tvCaption.typeface = Typeface.DEFAULT_BOLD
            holder.itemView.setBackgroundColor(selectColor)
        } else {
            holder.tvCaption.typeface = Typeface.DEFAULT
            holder.itemView.setBackgroundColor(deselectColor)
        }
        holder.itemView.setOnClickListener {
            callback?.onClick(position, caption[position].time)
        }
    }

    override fun getItemCount(): Int {
        return caption.size
    }

    fun selectItem(position: Int?) {
        selectPosition = position
    }

    interface Callback {
        fun onClick(position: Int, time: Int)
    }
}