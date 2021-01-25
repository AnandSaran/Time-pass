package com.gregantech.timepass.network.request

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class UserFollowRequest(
    val userID: String,
    val isFollowed: Boolean,
    val followerId: String
) : Parcelable