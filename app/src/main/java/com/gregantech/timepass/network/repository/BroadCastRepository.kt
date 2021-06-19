package com.gregantech.timepass.network.repository

import com.gregantech.timepass.base.TimePassBaseRepository
import com.gregantech.timepass.network.RetrofitClient
import com.gregantech.timepass.network.api.BroadCastService
import com.gregantech.timepass.network.request.BroadCastRequest

class BroadCastRepository : TimePassBaseRepository() {
    private val broadCastService =
        RetrofitClient.retrofit.create(BroadCastService::class.java)

    suspend fun updateStatus(broadCastRequest: BroadCastRequest) = getResult {
        broadCastService.updateBroadcastStatus(broadCastRequest)
    }

}