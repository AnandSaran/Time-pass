package com.gregantech.timepass.network.request

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class UserPostDeleteRequest(
    val userID: String,
    val postId: String,
    val isDelete: Boolean = true
) : Parcelable