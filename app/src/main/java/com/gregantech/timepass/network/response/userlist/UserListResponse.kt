package com.gregantech.timepass.network.response.userlist

data class UserListResponse(
    val List: List<Following> = arrayListOf()
)