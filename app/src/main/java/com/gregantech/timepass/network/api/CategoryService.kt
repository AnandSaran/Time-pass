package com.gregantech.timepass.network.api

import com.gregantech.timepass.network.response.CategoryResponse
import retrofit2.Response
import retrofit2.http.POST

interface CategoryService {

    @POST("videosCategory_api.php")
    suspend fun getCategory(): Response<CategoryResponse>

}