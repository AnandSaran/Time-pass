package com.gregantech.timepass.network.repository.bridge

import com.gregantech.timepass.model.RailBaseItemModel
import com.gregantech.timepass.model.RailItemTypeOneModel
import com.gregantech.timepass.model.RailItemTypeThreeModel
import com.gregantech.timepass.model.RailItemTypeTwoModel
import com.gregantech.timepass.network.response.Category
import com.gregantech.timepass.network.response.Video
import com.gregantech.timepass.util.sharedpreference.SharedPreferenceHelper

fun List<Category>.toRailItemTypeOneModelList(): ArrayList<RailBaseItemModel> {
    return ArrayList(this.map { category ->
        RailItemTypeOneModel(
            contentId = category.catID,
            title = category.catName,
            image = category.catImage
        )
    })
}

fun List<Video>.toRailItemTypeTwoModelList(
    isShowFollow: Boolean = true,
    isShowProfile: Boolean = false
): ArrayList<RailBaseItemModel> {
    return ArrayList(this.map { video ->
        video.toRailItemTypeTwoModel(isShowFollow, isShowProfile)
    })
}

fun Video.toRailItemTypeTwoModel(
    isShowFollow: Boolean = true,
    isShowProfile: Boolean = true
): RailItemTypeTwoModel {
    return RailItemTypeTwoModel(
        contentId = Id,
        title = videoTitle,
        subtitle = videoDescription,
        video = videoName,
        image = image,
        isImage = isImage,
        totalLike = videoLikes,
        totalComment = videoComments,
        followerId = followerId,
        isFollowed = isFollowed,
        isLiked = isLiked,
        isShowFollow = generateIsToShowFollow(isShowFollow, followerId),
        isShowProfile = isShowProfile,
        userName = userName,
        userImage = userImage,
        viewType = viewType
    )
}

fun List<Video>.toRailItemTypeThreeModelList(): ArrayList<RailBaseItemModel> {
    return ArrayList(this.map { video ->
        video.toRailItemTypeThreeModel()
    })
}

fun Video.toRailItemTypeThreeModel(): RailItemTypeThreeModel {
    return RailItemTypeThreeModel(
        contentId = Id,
        title = videoTitle,
        subtitle = videoDescription,
        videoImage = videoImage,
        image = image,
        isImage = isImage ?: false
    )
}

private fun generateIsToShowFollow(showFollow: Boolean, followerId: String): Boolean {
    return if (showFollow) {
        !SharedPreferenceHelper.isPostUserAreSameUser(followerId)
    } else {
        false
    }
}