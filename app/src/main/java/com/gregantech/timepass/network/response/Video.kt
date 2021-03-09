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
    val Id: String = EMPTY_STRING,
    @NullToEmptyString
    val videoName: String = EMPTY_STRING,
    @NullToEmptyString
    val videoTitle: String = EMPTY_STRING,
    @NullToEmptyString
    val videoDescription: String = EMPTY_STRING,
    @NullToEmptyString
    val image: String = EMPTY_STRING,
    @NullToEmptyString
    val followerId: String = EMPTY_STRING,
    val videoLikes: Int = 0,
    val videoComments: Int = 0,
    val isFollowed: Boolean = false,
    val isLiked: Boolean = false,
    val isImage: Boolean? = false,
    var viewType: Int = 0,
    @NullToEmptyString
    val videoImage: String = EMPTY_STRING,
    @NullToEmptyString
    val userName: String = EMPTY_STRING,
    @NullToEmptyString
    val userImage: String = EMPTY_STRING
) : Parcelable