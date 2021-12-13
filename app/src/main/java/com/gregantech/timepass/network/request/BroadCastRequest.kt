package com.gregantech.timepass.network.request

import com.google.gson.annotations.SerializedName

data class BroadCastRequest(
    @field:SerializedName("streamId")
    val streamId: String? = null,
    @field:SerializedName("userId")
    val userId: String? = null,
    @field:SerializedName("liveStatus")
    val liveStatus: Boolean? = null
)
