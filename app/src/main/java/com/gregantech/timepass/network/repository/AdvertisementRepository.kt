package com.gregantech.timepass.network.repository

import com.gregantech.timepass.base.TimePassBaseRepository
import com.gregantech.timepass.network.RetrofitClient
import com.gregantech.timepass.network.api.AdvertisementService

class AdvertisementRepository : TimePassBaseRepository() {
    private val advertisementService =
        RetrofitClient.retrofit.create(AdvertisementService::class.java)

    suspend fun getAdStatus() = getResult {
        advertisementService.getAdStatus()
    }

}