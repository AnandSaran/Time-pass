package com.gregantech.timepass.network.api

import com.gregantech.timepass.network.request.LoginRequest
import com.gregantech.timepass.network.response.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginService {

    @POST("login_api.php")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

}