package com.gregantech.timepass.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 *  model for Rail item type 1 inside rail
 */
@Parcelize
class CommentTypeOneModel(
    override val contentId: String,
    override val title: String = "",
    val subtitle: String = "",
    override val image: String = "",
    override val time: String = "",
    override val commentTypeEnum: CommentTypeEnum = CommentTypeEnum.TYPE_COMMENT_ITEM_ONE
) : Parcelable, CommentBaseItemModel()