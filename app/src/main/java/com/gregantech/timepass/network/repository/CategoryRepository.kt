package com.gregantech.timepass.network.repository

import com.gregantech.timepass.base.TimePassBaseRepository
import com.gregantech.timepass.network.RetrofitClient
import com.gregantech.timepass.network.api.CategoryService

class CategoryRepository: TimePassBaseRepository(){
    private val categoryService = RetrofitClient.retrofit.create(CategoryService::class.java)

    suspend fun fetchCategory() = getResult {
        categoryService.getCategory()
    }

}