package com.gregantech.timepass.adapter.rail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.Player
import com.gregantech.timepass.R
import com.gregantech.timepass.adapter.handler.rail.RailItemClickHandler
import com.gregantech.timepass.databinding.ItemInstagramBinding
import com.gregantech.timepass.model.RailBaseItemModel
import com.gregantech.timepass.model.RailItemTypeTwoModel
import com.gregantech.timepass.util.PlayerStateCallback
import com.gregantech.timepass.util.PlayerViewAdapter
import com.gregantech.timepass.util.extension.loadDrawable
import com.gregantech.timepass.util.extension.loadUrl
import com.gregantech.timepass.util.extension.loadUrlCircle
import com.gregantech.timepass.util.extension.visible
import java.util.*

/**
 * A custom adapter to use with the RecyclerView widget.
 */
class InstagramAdapter(
    private var modelList: ArrayList<RailBaseItemModel>,
    private val railItemClickHandler: RailItemClickHandler
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), PlayerStateCallback {

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): VideoPlayerViewHolder {

        val layoutInflater = LayoutInflater.from(viewGroup.context)
        val binding = ItemInstagramBinding.inflate(layoutInflater, viewGroup, false)

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

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        val position = holder.adapterPosition
        PlayerViewAdapter.releaseRecycledPlayers(position)
        super.onViewRecycled(holder)
    }

    override fun getItemCount(): Int {
        return modelList.size
    }

    private fun getItem(position: Int): RailItemTypeTwoModel {
        return modelList[position] as RailItemTypeTwoModel
    }


    inner class VideoPlayerViewHolder(private val binding: ItemInstagramBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(model: RailItemTypeTwoModel) {
            binding.apply {
                dataModel = model
                callback = this@InstagramAdapter
                index = adapterPosition
                setupOnClick(model)
                setupFollow(model.isShowFollow)
                setupProfile(model)
                setupMediaContent(model)
                executePendingBindings()
            }
        }

        private fun setupFollow(isShowFollow: Boolean) {
            binding.ivFollow.visible(isShowFollow)
        }

        private fun setupProfile(model: RailItemTypeTwoModel) {
            binding.clProfile.visible(model.isShowProfile)
            if (model.isShowProfile) {
                binding.ivUserProfile.loadUrlCircle(
                    model.userImage,
                    R.drawable.place_holder_profile
                )
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

            binding.ivComment.setOnClickListener {
                onClickComment(railItemClickHandler, railItem)
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

            binding.clProfile.setOnClickListener {
                onClickProfile(railItemClickHandler, railItem)
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

        private fun onClickComment(
            railItemClickHandler: RailItemClickHandler,
            railItem: RailItemTypeTwoModel
        ) {
            railItemClickHandler.clickComment(railItem)
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

        private fun setupMediaContent(model: RailItemTypeTwoModel) {
            val isImage = model.isImage
            if (isImage != null && isImage) {
                binding.clPlayer.visible(false)
                binding.ivPoster.visible(true)
                binding.ivPoster.loadUrl(model.image)
            } else {
                binding.ivPoster.visible(false)
                binding.clPlayer.visible(true)
            }
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

    private fun onClickProfile(
        railItemClickHandler: RailItemClickHandler,
        railItem: RailItemTypeTwoModel
    ) {
        if (railItemClickHandler.isInitializedForProfileClicking()) {
            railItemClickHandler.clickProfile(railItem)
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
            R.drawable.ic_follow_black
        }

    }

    private fun getLikeDrawable(isLiked: Boolean): Int {
        return if (isLiked) {
            R.drawable.ic_like
        } else {
            R.drawable.ic_un_like_black
        }

    }
}