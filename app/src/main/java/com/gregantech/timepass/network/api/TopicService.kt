package com.gregantech.timepass.network.api

import com.gregantech.timepass.view.topic.model.TopicResponse
import retrofit2.Response
import retrofit2.http.GET

interface TopicService {

    @GET("newTopic_api.php")
    suspend fun getTopics(): Response<TopicResponse>

}