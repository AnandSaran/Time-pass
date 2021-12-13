package com.gregantech.timepass.model

import com.google.gson.annotations.SerializedName

data class LiveUserListRequest(

    @field:SerializedName("userID")
    val userID: String? = null
)
