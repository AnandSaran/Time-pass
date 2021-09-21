package com.gregantech.timepass.fcm

import com.google.gson.annotations.SerializedName

data class FCMDataModel(
	@field:SerializedName("message")
	val message: String? = null,
	@field:SerializedName("user")
	val user: User? = null,
	@field:SerializedName("liveStatus")
	val liveStatus: Boolean? = null,
	@field:SerializedName("status")
	val status: String? = null
)

data class User(
	@field:SerializedName("streamID")
	val streamID: String? = null,
	@field:SerializedName("userID")
	val userID: String? = null
)
