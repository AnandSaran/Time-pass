package com.gregantech.timepass.network.repository.local

import android.net.Uri
import com.gregantech.timepass.network.repository.convertor.ProfileConverterFactory
import com.gregantech.timepass.util.sharedpreference.SharedPreferenceHelper
import java.io.File

class UserProfileScreenRepository(
    private val sharedPref: SharedPreferenceHelper
) {

    fun getUserId(): String {
        return sharedPref.getUserId()
    }
}