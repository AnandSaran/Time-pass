package com.gregantech.timepass.network.repository

import com.gregantech.timepass.base.TimePassBaseRepository
import com.gregantech.timepass.network.RetrofitClient
import com.gregantech.timepass.network.api.UserListService
import com.gregantech.timepass.network.request.SearchUsersRequest
import com.gregantech.timepass.network.request.UserFollowListRequest

class UserListRepository : TimePassBaseRepository() {
    private val userListService = RetrofitClient.retrofit.create(UserListService::class.java)

    suspend fun getUserFollowing(userFollowListRequest: UserFollowListRequest) = getResult {
        userListService.getUserFollowing(userFollowListRequest)
    }

    suspend fun getUserFollower(userFollowListRequest: UserFollowListRequest) = getResult {
        userListService.getUserFollower(userFollowListRequest)
    }

    suspend fun getSearchUserList(searchUsersRequest: SearchUsersRequest) = getResult {
        userListService.getUserList(searchUsersRequest)
    }
}