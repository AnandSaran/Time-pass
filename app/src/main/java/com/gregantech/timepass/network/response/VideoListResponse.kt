package com.gregantech.timepass.network.response

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class VideoListResponse(
    val is_last: Boolean = false, //Modified for search
    val status: String? = null, //Modified for search
    var video: List<Video> = arrayListOf()
) : Parcelable