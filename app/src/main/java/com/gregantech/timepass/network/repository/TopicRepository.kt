package com.gregantech.timepass.network.repository

import com.gregantech.timepass.base.TimePassBaseRepository
import com.gregantech.timepass.network.RetrofitClient
import com.gregantech.timepass.network.api.TopicService

class TopicRepository : TimePassBaseRepository() {

    private val topicService = RetrofitClient.retrofit.create(TopicService::class.java)

    suspend fun getTopics() = getResult {
        topicService.getTopics()
    }

}