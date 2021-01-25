package com.gregantech.timepass.network.repository

import com.gregantech.timepass.base.TimePassBaseRepository
import com.gregantech.timepass.network.RetrofitClient
import com.gregantech.timepass.network.api.ProfileService
import com.gregantech.timepass.network.request.OtherProfileRequest
import okhttp3.MediaType
import okhttp3.RequestBody
import java.io.File


class ProfileRepository : TimePassBaseRepository() {
    private val profileService = RetrofitClient.retrofit.create(ProfileService::class.java)

    suspend fun updateProfile(
        userName: String,
        emailID: String,
        profileImage: File?,
        userID: String
    ) = getResult {
        if (profileImage == null) {
            profileService.updateProfile(
                generateMultiPartString(userName),
                generateMultiPartString(emailID),
                generateMultiPartString(userID)
            )
        } else {
            profileService.updateProfile(
                generateMultiPartString(userName),
                generateMultiPartString(emailID),
                generateMultiPartString(userID),
                generateMultiPartImage(profileImage)
            )
        }
    }

    private fun generateMultiPartImage(file: File): RequestBody {
        return RequestBody.create(MediaType.parse("image/jpeg"), file)
    }

    private fun generateMultiPartString(content: String): RequestBody {
        return RequestBody.create(MediaType.parse("text/plain"), content)
    }

    suspend fun getOtherProfile(
        otherProfileRequest: OtherProfileRequest
    ) = getResult {
        profileService.getOtherProfile(otherProfileRequest)
    }

}