package com.gregantech.timepass.model

import com.squareup.moshi.Json

data class InteractionListResponse(
    @Json(name = "Post")
    val Post: List<PostItem?>? = null
)

data class PostItem(
    @Json(name = "isImage")
    val isImage: Boolean? = null,
    @Json(name = "image")
    val image: String? = null,
    @Json(name = "User")
    val user: UserItem? = null,
    @Json(name = "videoName")
    val videoName: String? = null,
    @Json(name = "videoImage")
    val videoImage: String? = null,
    @Json(name = "postTitle")
    val postTitle: String? = null,
    @Json(name = "postId")
    val postId: String? = null,
    @Json(name = "activityType")
    val activityType: String? = null,
    @Json(name = "ativityTitle")
    val ativityTitle: String? = null,
    @Json(name = "timestamp")
    val timestamp: String? = null
)

data class UserItem(
    @Json(name = "activityUserId")
    val activityUserId: String? = null,
    @Json(name = "activityUserName")
    val activityUserName: String? = null,
    @Json(name = "activityProfileImage")
    val activityProfileImage: String? = null
)

