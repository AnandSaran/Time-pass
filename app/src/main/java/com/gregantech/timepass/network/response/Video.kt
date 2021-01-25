package com.gregantech.timepass.network.response

import android.os.Parcelable
import com.gregantech.timepass.network.jsonadapter.NullToEmptyString
import com.gregantech.timepass.util.constant.EMPTY_STRING
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class Video(
    @NullToEmptyString
    val Id: String = "",
    @NullToEmptyString
    val videoName: String = "",
    @NullToEmptyString
    val videoTitle: String = "",
    @NullToEmptyString
    val videoDescription: String = "",
    @NullToEmptyString
    val videoThumbnail: String = "",
    @NullToEmptyString
    val followerId: String = "",
    val videoLikes: Int = 0,
    val videoComments: Int = 0,
    val isFollowed: Boolean = false,
    val isLiked: Boolean = false,
    @NullToEmptyString
    val userName: String = EMPTY_STRING,
    @NullToEmptyString
    val userImage: String = EMPTY_STRING
) : Parcelable