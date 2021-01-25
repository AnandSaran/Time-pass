package com.gregantech.timepass.network.repository.bridge

import com.gregantech.timepass.model.CommentTypeOneModel
import com.gregantech.timepass.model.RailBaseItemModel
import com.gregantech.timepass.model.RailItemTypeOneModel
import com.gregantech.timepass.model.RailItemTypeTwoModel
import com.gregantech.timepass.network.response.Category
import com.gregantech.timepass.network.response.Video
import com.gregantech.timepass.network.response.comments.Comment

fun List<Comment>.toCommentItemTypeOneModelList(): ArrayList<CommentTypeOneModel> {
    return ArrayList(this.map { comment ->
        comment.commentTypeOneModel()
    })
}

fun Comment.commentTypeOneModel(): CommentTypeOneModel {
    return CommentTypeOneModel(
        contentId = Id,
        title = commentuserName,
        image = commentprofileImage,
        subtitle = comments,
        time = DateTime
    )
}