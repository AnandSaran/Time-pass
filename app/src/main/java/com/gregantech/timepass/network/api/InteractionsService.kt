package com.gregantech.timepass.network.api

import com.gregantech.timepass.model.InteractionsResponse
import retrofit2.Response
import retrofit2.http.GET

interface InteractionsService {

    @GET("userActivityNotification.php")
    suspend fun getActivityState(): Response<InteractionsResponse>

}