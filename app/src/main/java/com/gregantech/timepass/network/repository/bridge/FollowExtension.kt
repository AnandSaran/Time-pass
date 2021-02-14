package com.gregantech.timepass.network.repository.bridge

import com.gregantech.timepass.model.RailBaseItemModel
import com.gregantech.timepass.model.RailItemTypeFourModel
import com.gregantech.timepass.network.response.userlist.Following

fun List<Following>.toRailItemTypeFourModelList(): ArrayList<RailBaseItemModel> {
    return ArrayList(this.map { following ->
        RailItemTypeFourModel(
            contentId = following.userId,
            title = following.userName,
            image = following.profileImage
        )
    })
}