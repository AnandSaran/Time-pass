package com.gregantech.timepass.network.api

import com.gregantech.timepass.model.InteractionListResponse
import com.gregantech.timepass.model.InteractionRequest
import com.gregantech.timepass.model.InteractionStatusResponse
import com.gregantech.timepass.model.InteractionsResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface InteractionsService {

    @POST("userActivityNotification.php")
    suspend fun getActivityState(@Body interactionRequest: InteractionRequest): Response<InteractionsResponse>

    @POST("userActivityList.php")
    suspend fun getInteractionList(@Body interactionRequest: InteractionRequest): Response<InteractionListResponse>

    @POST("userActivitySeen.php")
    suspend fun updateState(@Body interactionRequest: InteractionRequest): Response<InteractionStatusResponse>

}