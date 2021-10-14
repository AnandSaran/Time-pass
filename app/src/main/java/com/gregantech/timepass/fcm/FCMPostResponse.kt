package com.gregantech.timepass.fcm

import com.squareup.moshi.Json


data class FCMPostResponse(
	@Json(name = "videoComments")
	val videoComments: String? = null,
	@Json(name = "image")
	val image: String? = null,
	@Json(name = "video_name")
	val videoName: String? = null,
	@Json(name = "videoLikes")
	val videoLikes: String? = null,
	@Json(name = "videoDateTime")
	val videoDateTime: String? = null,
	@Json(name = "isLiked")
	val isLiked: Boolean? = null,
	@Json(name = "userName")
	val userName: String? = null,
	@Json(name = "followerId")
	val followerId: String? = null,
	@Json(name = "videoTitle")
	val videoTitle: String? = null,
	@Json(name = "isFollowed")
	val isFollowed: Boolean? = null,
	@Json(name = "isImage")
	val isImage: Boolean? = null,
	@Json(name = "userImage")
	val userImage: String? = null,
	@Json(name = "videoDescription")
	val videoDescription: String? = null,
	@Json(name = "Id")
	val Id: String? = null
)
