package com.gregantech.timepass.viewholder.rail

import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gregantech.timepass.R
import com.gregantech.timepass.adapter.handler.rail.RailItemClickHandler
import com.gregantech.timepass.databinding.ItemRailTypeTwoBinding
import com.gregantech.timepass.model.RailItemTypeTwoModel
import com.gregantech.timepass.util.extension.loadDrawable
import com.gregantech.timepass.util.extension.visible

/**
 * Created by anand
 */
class RailItemTypeTwoViewHolder(
    var railBinding: ItemRailTypeTwoBinding
) :
    RecyclerView.ViewHolder(railBinding.root) {
    lateinit var railItem: RailItemTypeTwoModel
    fun bind(
        railItem: RailItemTypeTwoModel,
        railItemClickHandler: RailItemClickHandler
    ) {
        itemView.tag = this
        this.railItem = railItem
        railBinding.apply {
            tvTitle.text = railItem.title
            tvTotalLike.text = railItem.totalLike.toString()
            ivLike.loadDrawable(getLikeDrawable(railItem.isLiked))
            setVideoPlayer(railItem.video)
            vvPlayer.setOnClickListener {
                if (railItemClickHandler.isInitializedForPosterClicking()) {
                    railItemClickHandler.clickPoster(railItem)
                }
            }

            ivLike.setOnClickListener {
                onClickLike(railItemClickHandler, railItem)
            }

            tvTotalLike.setOnClickListener {
                onClickLike(railItemClickHandler, railItem)
            }

            ivShare.setOnClickListener {
                onClickShare(railItemClickHandler, railItem)
            }
            ivDownload.setOnClickListener {
                onClickDownload(railItemClickHandler, railItem)
            }
            executePendingBindings()
        }
    }

    private fun setVideoPlayer(video: String) {
        railBinding.vvPlayer.setVideoPath(video)
        railBinding.vvPlayer.setOnPreparedListener { mp ->
            onPlayerPrepared(mp)
        }
    }

    private fun onPlayerPrepared(mp: MediaPlayer) {
        showProgressBar(false)
        mp.start()
      /*  val videoRatio = mp.videoWidth.toFloat() / mp.videoHeight.toFloat()
        val screenRatio =
            railBinding.vvPlayer.width.toFloat() / railBinding.vvPlayer.height.toFloat()
        val scale = videoRatio / screenRatio
        if (scale >= 1f) {
            railBinding.vvPlayer.scaleX = scale
        } else {
            railBinding.vvPlayer.scaleY = (1f / scale)
        }*/
    }

    private fun showProgressBar(isShow: Boolean = true) {
        railBinding.layProgressBar.visible(isShow)
    }

    private fun onClickLike(
        railItemClickHandler: RailItemClickHandler,
        railItem: RailItemTypeTwoModel
    ) {
        if (railItemClickHandler.isInitializedForLikeClicking()) {
            railItemClickHandler.clickLike(railItem)
        }
    }


    private fun onClickDownload(
        railItemClickHandler: RailItemClickHandler,
        railItem: RailItemTypeTwoModel
    ) {
        if (railItemClickHandler.isInitializedForDownloadClicking()) {
            railItemClickHandler.clickDownload(railItem)
        }
    }

    private fun onClickShare(
        railItemClickHandler: RailItemClickHandler,
        railItem: RailItemTypeTwoModel
    ) {
        if (railItemClickHandler.isInitializedForShareClicking()) {
            railItemClickHandler.clickShare(railItem)
        }
    }

    private fun getLikeDrawable(isLiked: Boolean): Int {
        return if (isLiked) {
            R.drawable.ic_like
        } else {
            R.drawable.ic_un_like
        }

    }

    companion object {
        fun from(
            parent: ViewGroup
        ): RailItemTypeTwoViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemRailTypeTwoBinding.inflate(layoutInflater, parent, false)
            return RailItemTypeTwoViewHolder(binding)
        }
    }
}