package com.gregantech.timepass.network.response

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class VideoLikeResponse(
    val isLiked: Boolean,
    val message: String,
    val status: String
) : Parcelable