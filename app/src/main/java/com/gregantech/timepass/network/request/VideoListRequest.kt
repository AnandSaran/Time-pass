package com.gregantech.timepass.network.request

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class VideoListRequest(
    val userID: String,
    val categoryId: String,
    val pageNo: Int=1
) : Parcelable