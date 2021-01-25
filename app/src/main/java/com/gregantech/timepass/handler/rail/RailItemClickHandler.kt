package com.gregantech.timepass.adapter.handler.rail

import com.gregantech.timepass.model.RailBaseItemModel

class RailItemClickHandler {
    lateinit var clickPoster: (railItem: RailBaseItemModel) -> Unit
    lateinit var clickLike: (railItem: RailBaseItemModel) -> Unit
    lateinit var clickComment: (railItem: RailBaseItemModel) -> Unit
    lateinit var clickFollow: (railItem: RailBaseItemModel) -> Unit
    lateinit var clickShare: (railItem: RailBaseItemModel) -> Unit
    lateinit var clickDownload: (railItem: RailBaseItemModel) -> Unit
    lateinit var clickProfile: (railItem: RailBaseItemModel) -> Unit

    fun isInitializedForPosterClicking(): Boolean {
        return ::clickPoster.isInitialized
    }

    fun isInitializedForLikeClicking(): Boolean {
        return ::clickLike.isInitialized
    }

    fun isInitializedForCommentClicking(): Boolean {
        return ::clickComment.isInitialized
    }

    fun isInitializedForFollowClicking(): Boolean {
        return ::clickFollow.isInitialized
    }

    fun isInitializedForShareClicking(): Boolean {
        return ::clickShare.isInitialized
    }

    fun isInitializedForDownloadClicking(): Boolean {
        return ::clickDownload.isInitialized
    }

    fun isInitializedForProfileClicking(): Boolean {
        return ::clickProfile.isInitialized
    }
}