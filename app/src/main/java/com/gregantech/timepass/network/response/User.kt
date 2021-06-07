package com.gregantech.timepass.network.response

import android.os.Parcelable
import com.gregantech.timepass.network.jsonadapter.NullToEmptyString
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class User(
    @NullToEmptyString
    val emailID: String = "",
    @NullToEmptyString
    val mobileNo: String = "",
    @NullToEmptyString
    val profileImage: String = "",
    @NullToEmptyString
    val userID: String = "",
    @NullToEmptyString
    val userName: String = "",
    @NullToEmptyString
    val fcmToken: String = "",
    @NullToEmptyString
    val followers: String = "",
    @NullToEmptyString
    val following: String = "",
    @NullToEmptyString
    val posts: String = "",
    var isFollowed: Boolean? = false,
    @NullToEmptyString
    val bio: String = "",
    @NullToEmptyString
    val youtube: String = "",
    var isLiveEnabled: Boolean = false
) : Parcelable