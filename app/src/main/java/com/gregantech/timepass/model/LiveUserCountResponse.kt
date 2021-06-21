package com.gregantech.timepass.model

import com.google.gson.annotations.SerializedName

data class LiveUserCountResponse(

    @field:SerializedName("totalHLSWatchersCount")
    val totalHLSWatchersCount: Int? = null,

    @field:SerializedName("totalWebRTCWatchersCount")
    val totalWebRTCWatchersCount: Int? = null,

    @field:SerializedName("totalRTMPWatchersCount")
    val totalRTMPWatchersCount: Int? = null
)
