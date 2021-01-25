package com.gregantech.timepass.network.response.comments

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class CommentListResponse(
    val comments: List<Comment> = arrayListOf()
): Parcelable