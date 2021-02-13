package com.gregantech.timepass.adapter.rail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.Player
import com.gregantech.timepass.R
import com.gregantech.timepass.adapter.handler.rail.RailItemClickHandler
import com.gregantech.timepass.databinding.ItemTikTokBinding
import com.gregantech.timepass.model.RailBaseItemModel
import com.gregantech.timepass.model.RailItemTypeTwoModel
import com.gregantech.timepass.util.NewPlayerViewAdapter
import com.gregantech.timepass.util.PlayerStateCallback
import com.gregantech.timepass.util.extension.loadDrawable

/**
 * Created by anand
 * Recycle view adapter of Rail.
 */
class TikTokAdapter(
    private var modelList: ArrayList<RailBaseItemModel>,
    private val railItemClickHandler: RailItemClickHandler,
    private val playerAdapter: NewPlayerViewAdapter
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), PlayerStateCallback {

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): VideoPlayerViewHolder {

        val layoutInflater = LayoutInflater.from(viewGroup.context)
        val binding = ItemTikTokBinding.inflate(layoutInflater, viewGroup, false)

        val lp = RecyclerView.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        binding.root.layoutParams = lp
        return VideoPlayerViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {

        //Here you can fill your row view
        if (holder is VideoPlayerViewHolder) {
            val model = getItem(position)

            // send data to view holder
            holder.onBind(model)
        }
    }

    override fun getItemCount(): Int {
        return modelList.size
    }

    private fun getItem(position: Int): RailItemTypeTwoModel {
        return modelList[position] as RailItemTypeTwoModel
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        val position = holder.adapterPosition
        playerAdapter.releaseRecycledPlayers(position)
        super.onViewRecycled(holder)
    }

    inner class VideoPlayerViewHolder(
        private val binding: ItemTikTokBinding
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(model: RailItemTypeTwoModel) {
            binding.apply {
                dataModel = model
                callback = this@TikTokAdapter
                index = adapterPosition
                setupOnClick(model)
                executePendingBindings()
            }
        }

        private fun setupOnClick(railItem: RailItemTypeTwoModel) {
            binding.root.setOnClickListener {
                if (railItemClickHandler.isInitializedForPosterClicking()) {
                    railItemClickHandler.clickPoster(railItem)
                }
            }

            binding.ivFollow.setOnClickListener {
                onClickFollow(railItemClickHandler, railItem)
            }

            binding.ivLike.setOnClickListener {
                onClickLike(railItemClickHandler, railItem)
            }

            binding.tvTotalLike.setOnClickListener {
                onClickLike(railItemClickHandler, railItem)
            }

            binding.ivShare.setOnClickListener {
                onClickShare(railItemClickHandler, railItem)
            }
            binding.ivDownload.setOnClickListener {
                onClickDownload(railItemClickHandler, railItem)
            }
        }

        private fun onClickFollow(
            railItemClickHandler: RailItemClickHandler,
            railItem: RailItemTypeTwoModel
        ) {
            if (railItemClickHandler.isInitializedForFollowClicking()) {
                changeFollowIcon(railItem.isFollowed)
                railItemClickHandler.clickFollow(railItem)
            }
        }

        private fun onClickLike(
            railItemClickHandler: RailItemClickHandler,
            railItem: RailItemTypeTwoModel
        ) {
            if (railItemClickHandler.isInitializedForLikeClicking()) {
                changeLikeIcon(railItem.isLiked)
                changeTotalLike(railItem)
                railItemClickHandler.clickLike(railItem)
            }
        }

        private fun changeFollowIcon(isFollowed: Boolean) {
            binding.ivFollow.loadDrawable(getFollowDrawable(!isFollowed))
        }

        private fun changeLikeIcon(isLiked: Boolean) {
            binding.ivLike.loadDrawable(getLikeDrawable(!isLiked))
        }

        private fun changeTotalLike(railItem: RailItemTypeTwoModel) {
            binding.tvTotalLike.text = generateTotalLike(railItem)
        }
    }

    private fun generateTotalLike(railItem: RailItemTypeTwoModel): String {
        return when (railItem.isLiked) {
            true -> {
                --railItem.totalLike
            }
            false -> {
                ++railItem.totalLike
            }
        }.toString()

    }

    override fun onVideoDurationRetrieved(duration: Long, player: Player) {
    }

    override fun onVideoBuffering(player: Player) {
    }

    override fun onStartedPlaying(player: Player) {

    }

    override fun onFinishedPlaying(player: Player) {
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

    private fun getFollowDrawable(isFollowed: Boolean): Int {
        return if (isFollowed) {
            R.drawable.ic_followed
        } else {
            R.drawable.ic_follow
        }

    }

    private fun getLikeDrawable(isLiked: Boolean): Int {
        return if (isLiked) {
            R.drawable.ic_like
        } else {
            R.drawable.ic_un_like
        }

    }
}