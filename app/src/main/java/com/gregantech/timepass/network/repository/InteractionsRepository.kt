package com.gregantech.timepass.network.repository

import com.gregantech.timepass.base.TimePassBaseRepository
import com.gregantech.timepass.model.InteractionRequest
import com.gregantech.timepass.network.RetrofitClient
import com.gregantech.timepass.network.api.InteractionsService

class InteractionsRepository : TimePassBaseRepository() {
    private val interactionsRepository =
        RetrofitClient.retrofit.create(InteractionsService::class.java)

    suspend fun getActivityState(interactionRequest: InteractionRequest) = getResult {
        interactionsRepository.getActivityState(interactionRequest)
    }

    suspend fun getInteractionList(interactionRequest: InteractionRequest) = getResult {
        interactionsRepository.getInteractionList(interactionRequest)
    }

    suspend fun updateState(interactionRequest: InteractionRequest) = getResult {
        interactionsRepository.updateState(interactionRequest)
    }
}