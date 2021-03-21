package com.gregantech.timepass.network.api

import com.gregantech.timepass.network.request.OtherProfileRequest
import com.gregantech.timepass.network.response.LoginResponse
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ProfileService {

    @Multipart
    @POST("profile_api.php")
    suspend fun updateProfile(
        @Part("userName") userName: RequestBody,
        @Part("emailID") emailID: RequestBody,
        @Part("userID") userID: RequestBody,
        @Part("bio") bio: RequestBody,
        @Part("youtube") youtube: RequestBody,
        @Part("profileImage\"; filename=\"profileImage.png") profileImage: RequestBody
    ): Response<LoginResponse>

    @Multipart
    @POST("profile_api.php")
    suspend fun updateProfile(
        @Part("userName") userName: RequestBody,
        @Part("emailID") emailID: RequestBody,
        @Part("userID") userID: RequestBody,
        @Part("bio") bio: RequestBody,
        @Part("youtube") youtube: RequestBody
    ): Response<LoginResponse>

    @POST("othersProfile_api.php")
    suspend fun getOtherProfile(
        @Body otherProfileRequest: OtherProfileRequest
    ): Response<LoginResponse>


}