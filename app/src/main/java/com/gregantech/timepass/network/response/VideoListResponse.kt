package com.gregantech.timepass.network.response

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class VideoListResponse(
    val is_last: Boolean,
    val status: String,
    var video: List<Video> = arrayListOf()
) : Parcelable