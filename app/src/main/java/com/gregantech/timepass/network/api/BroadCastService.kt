package com.gregantech.timepass.network.api

import com.gregantech.timepass.model.BroadcastResponse
import com.gregantech.timepass.model.LiveUserCountResponse
import com.gregantech.timepass.model.LiveUserListRequest
import com.gregantech.timepass.model.LiveUserListResponse
import com.gregantech.timepass.network.request.BroadCastRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface BroadCastService {

    @POST("user_live_api.php")
    suspend fun updateBroadcastStatus(@Body brodCastRequest: BroadCastRequest): Response<BroadcastResponse>

    @POST("userLiveList.php")
    suspend fun getLiveUsers(@Body liveUserListRequest: LiveUserListRequest): Response<LiveUserListResponse>

    @GET("http://148.66.129.86:5080/LiveApp/rest/v2/broadcasts/{id}/broadcast-statistics")
    suspend fun getLiveUsersCount(@Path("id") id: String): Response<LiveUserCountResponse>

}