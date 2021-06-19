package com.gregantech.timepass.network.api

import com.gregantech.timepass.model.BroadcastResponse
import com.gregantech.timepass.network.request.BroadCastRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface BroadCastService {

    @POST("user_live_api.php")
    suspend fun updateBroadcastStatus(@Body brodCastRequest: BroadCastRequest): Response<BroadcastResponse>

}