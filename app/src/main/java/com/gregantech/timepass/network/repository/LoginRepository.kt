package com.gregantech.timepass.network.repository

import com.gregantech.timepass.base.TimePassBaseRepository
import com.gregantech.timepass.network.RetrofitClient
import com.gregantech.timepass.network.api.LoginService
import com.gregantech.timepass.network.request.LoginRequest

class LoginRepository: TimePassBaseRepository(){
    private val loginService = RetrofitClient.retrofit.create(LoginService::class.java)

    suspend fun login(loginRequest: LoginRequest) = getResult {
        loginService.login(loginRequest)
    }

}