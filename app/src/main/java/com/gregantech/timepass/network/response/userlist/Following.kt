package com.gregantech.timepass.network.response.userlist

import com.gregantech.timepass.network.jsonadapter.NullToEmptyString
import com.gregantech.timepass.util.constant.EMPTY_STRING

data class Following(
    @NullToEmptyString
    val userId: String = EMPTY_STRING,
    @NullToEmptyString
    val profileImage: String = EMPTY_STRING,
    @NullToEmptyString
    val userName: String = EMPTY_STRING
)