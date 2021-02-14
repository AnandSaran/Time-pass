package com.gregantech.timepass.network.api

import com.gregantech.timepass.network.request.SearchUsersRequest
import com.gregantech.timepass.network.request.UserFollowListRequest
import com.gregantech.timepass.network.response.CategoryResponse
import com.gregantech.timepass.network.response.userlist.UserListResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface UserListService {

    @POST("followingList.php")
    suspend fun getUserFollowing(
        @Body userFollowListRequest: UserFollowListRequest
    ): Response<UserListResponse>

    @POST("followersList.php")
    suspend fun getUserFollower(
        @Body userFollowListRequest: UserFollowListRequest
    ): Response<UserListResponse>

    @POST("userSearchList.php")
    suspend fun getUserList(
        @Body userFollowListRequest: SearchUsersRequest
    ): Response<UserListResponse>
}