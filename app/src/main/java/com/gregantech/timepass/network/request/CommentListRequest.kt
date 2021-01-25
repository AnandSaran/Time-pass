package com.gregantech.timepass.network.request

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class CommentListRequest(
    val userID: String,
    val videoId: String
) : Parcelable