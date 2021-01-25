package com.gregantech.timepass.network.repository.local

import android.net.Uri
import com.gregantech.timepass.network.repository.convertor.ProfileConverterFactory
import com.gregantech.timepass.util.sharedpreference.SharedPreferenceHelper
import java.io.File

class ProfileScreenRepository(
    private val profileConverterFactory: ProfileConverterFactory,
    private val sharedPref: SharedPreferenceHelper
) {
    suspend fun getCompressedImage(filePath: Uri?): File? {
        return if (filePath == null) {
            null
        } else {
            profileConverterFactory.getCompressedImage(filePath)
        }
    }

    fun getUserId(): String {
        return sharedPref.getUserId()
    }
}