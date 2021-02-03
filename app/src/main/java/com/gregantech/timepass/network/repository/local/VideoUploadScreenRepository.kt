package com.gregantech.timepass.network.repository.local

import androidx.core.net.toUri
import com.gregantech.timepass.network.repository.convertor.ProfileConverterFactory
import com.gregantech.timepass.util.sharedpreference.SharedPreferenceHelper
import java.io.File

class VideoUploadScreenRepository(
    private val profileConverterFactory: ProfileConverterFactory,
    private val sharedPref: SharedPreferenceHelper
) {
    suspend fun getCompressedImage(filePath: String): File {
     return   profileConverterFactory.getCompressedImage(filePath.toUri())
    }

    fun getUserId(): String {
        return sharedPref.getUserId()
    }
}