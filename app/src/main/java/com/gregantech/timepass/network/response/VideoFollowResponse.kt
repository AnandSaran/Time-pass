package com.gregantech.timepass.network.response

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class VideoFollowResponse(
    val isFollowed: Boolean,
    val message: String,
    val status: String
) : Parcelable