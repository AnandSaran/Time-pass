package com.gregantech.timepass.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LiveUserListResponse(
	@Json(name = "List")
	val list: List<ListItem?>? = null
)

@JsonClass(generateAdapter = true)
data class ListItem(
	@Json(name = "startDateTime")
	val startDateTime: String? = null,
	@Json(name = "streamId")
	val streamId: String? = null,
	@Json(name = "userBio")
	val userBio: String? = null,
	@Json(name = "profileImage")
	val profileImage: String? = null,
	@Json(name = "userName")
	val userName: String? = null,
	@Json(name = "userId")
	val userId: String? = null,
	@Json(name = "liveStatus")
	val liveStatus: String? = null
)
