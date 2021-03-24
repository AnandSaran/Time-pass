package com.gregantech.timepass.network.api

import com.gregantech.timepass.model.AppConfigResponse
import retrofit2.Response
import retrofit2.http.GET

interface AppConfigService {

    @GET("appConfig_api.php")
    suspend fun getAppConfig(): Response<AppConfigResponse>

}