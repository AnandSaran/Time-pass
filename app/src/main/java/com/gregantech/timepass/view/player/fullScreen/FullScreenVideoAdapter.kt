package com.gregantech.timepass.view.player.fullScreen

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.ui.PlayerView
import com.gregantech.timepass.R
import kotlinx.android.synthetic.main.item_full_screen_video.view.*

class FullScreenVideoAdapter(val videoList: ArrayList<TikTokModel>?) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

//    var videoList: ArrayList<TikTokModel>? = null
//        set(value) {
//            field = value
//            notifyItemRangeInserted(0, (value?.size ?: 1) - 1)
//        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return VideoViewHolder(inflater.inflate(R.layout.item_full_screen_video, parent, false))
    }

    override fun getItemViewType(position: Int): Int {
        return videoList?.get(position)?.viewType ?: 0
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        when (holder) {
            is VideoViewHolder -> {
                holder.progressView.removeAllViews()
                with(holder.playerView) {
                    player?.muted = true
                    player = null
                }
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is VideoViewHolder -> holder.bind(videoList?.get(position))
        }
    }

    override fun getItemCount() = videoList?.size ?: 0

    class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val playerView: PlayerView = itemView.findViewById(R.id.fullScreenPlayer)
        val progressView = itemView.findViewById<FrameLayout>(R.id.progressView)


        fun bind(tikTokModel: TikTokModel?) {
            itemView.tvDesc.text = tikTokModel?.desc
            itemView.tvName.apply {
                text = tikTokModel?.title ?: ""
                if (tikTokModel?.title?.length ?: 0 > 10) {
                    isSingleLine = true
                    marqueeRepeatLimit = -1
                    ellipsize = TextUtils.TruncateAt.MARQUEE
                    isSelected = true
                }
            }

        }
    }
}