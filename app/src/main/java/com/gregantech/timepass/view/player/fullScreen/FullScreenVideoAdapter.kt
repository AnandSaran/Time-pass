package com.gregantech.timepass.view.player.fullScreen

import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.ui.PlayerView
import com.gregantech.timepass.R
import kotlinx.android.synthetic.main.include_fullscreen_vid_options.view.*
import kotlinx.android.synthetic.main.item_full_screen_video.view.*

class FullScreenVideoAdapter(
    private val videoList: ArrayList<TikTokModel>?,
    val callback: (TikTokModel, String) -> Unit
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    fun addMore(newVideoList: ArrayList<TikTokModel>) {
        val oldIndex = (videoList?.size ?: 1) - 1
        videoList?.addAll(newVideoList)
        notifyItemRangeInserted(oldIndex, (videoList?.size ?: 1) - 1)
    }

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

    inner class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val playerView: PlayerView = itemView.findViewById(R.id.fullScreenPlayer)
        val progressView: FrameLayout = itemView.findViewById(R.id.progressView)
        var pagePos = 0

        private val onClick = View.OnClickListener { view ->
            when (view) {
                itemView.includedMoreOption.ivLikeVid -> {
                    videoList?.get(PlayerProvider.getAbsolutePosition(adapterPosition))?.let {
                        val resId =
                            if (it.isLiked) R.drawable.ic_un_like else R.drawable.ic_like_green
                        itemView.includedMoreOption.ivLikeVid.setImageResource(resId)
                        it.isLiked = !it.isLiked
                        callback.invoke(it, "like")
                    }
                }
                itemView.includedMoreOption.ivComment -> {
                    callback.invoke(itemView.tag as TikTokModel, "comment")
                }
                itemView.includedMoreOption.ivShare -> {
                    callback.invoke(itemView.tag as TikTokModel, "share")
                }
                itemView.includedMoreOption.ivDownload -> {
                    callback.invoke(itemView.tag as TikTokModel, "download")
                }
            }
        }

        init {
            with(itemView.includedMoreOption) {
                arrayOf(ivLikeVid, ivComment, ivShare, ivDownload).forEach {
                    it.setOnClickListener(onClick)
                }
            }
        }


        fun bind(tikTokModel: TikTokModel?) {
            pagePos++
            Log.d("FullScreenX", "PagePos $pagePos")
            itemView.tag = tikTokModel
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

        private fun initListeners(position: Int) {

        }
    }


}