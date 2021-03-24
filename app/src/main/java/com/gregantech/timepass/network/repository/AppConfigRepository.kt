package com.gregantech.timepass.network.repository

import com.gregantech.timepass.base.TimePassBaseRepository
import com.gregantech.timepass.network.RetrofitClient
import com.gregantech.timepass.network.api.AppConfigService

class AppConfigRepository : TimePassBaseRepository() {
    private val appConfigRepository = RetrofitClient.retrofit.create(AppConfigService::class.java)

    suspend fun getAppConfig() = getResult {
        appConfigRepository.getAppConfig()
    }

}