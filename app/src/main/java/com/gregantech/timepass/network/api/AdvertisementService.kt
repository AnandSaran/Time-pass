package com.gregantech.timepass.network.api

import com.gregantech.timepass.model.AdvertisementResponse
import retrofit2.Response
import retrofit2.http.GET

interface AdvertisementService {

    @GET("adMob_api.php")
    suspend fun getAdStatus(): Response<AdvertisementResponse>

}