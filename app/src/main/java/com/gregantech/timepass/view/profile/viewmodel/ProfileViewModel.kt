package com.gregantech.timepass.view.profile.viewmodel

import android.net.Uri
import androidx.lifecycle.*
import com.gregantech.timepass.base.TimePassBaseResult
import com.gregantech.timepass.network.ApiResult
import com.gregantech.timepass.network.repository.ProfileRepository
import com.gregantech.timepass.network.repository.local.ProfileScreenRepository
import com.gregantech.timepass.network.response.LoginResponse
import com.gregantech.timepass.util.constant.ANNOTATION_UNCHECKED_CAST
import com.gregantech.timepass.util.constant.UNKNOWN_VIEW_MODEL_CLASS
import kotlinx.coroutines.Dispatchers

class ProfileViewModel(
    private val profileRepository: ProfileRepository,
    private val profileScreenRepository: ProfileScreenRepository
) : ViewModel() {

    fun updateProfile(userName: String, emailID: String, bio: String, youTubeProfileUrl: String, profileImage: Uri?) =
        liveData<TimePassBaseResult<LoginResponse>>(Dispatchers.IO) {
            emit(TimePassBaseResult.loading(null))
            val result = profileRepository.updateProfile(
                userName,
                emailID,
                bio,
                youTubeProfileUrl,
                profileScreenRepository.getCompressedImage(profileImage),
                profileScreenRepository.getUserId()
            )
            when (result.status) {
                TimePassBaseResult.Status.SUCCESS -> {
                    onUpdateProfileSuccess(result)
                }
                TimePassBaseResult.Status.ERROR -> {
                    onUpdateProfileFail()
                }
                else -> {
                    onUpdateProfileFail()
                }
            }
        }

    private suspend fun LiveDataScope<TimePassBaseResult<LoginResponse>>.onUpdateProfileFail() {
        emit(TimePassBaseResult.loading(null))

    }

    private suspend fun LiveDataScope<TimePassBaseResult<LoginResponse>>.onUpdateProfileSuccess(
        result: TimePassBaseResult<LoginResponse>
    ) {
        if (isValidFetchLoginData(result)) {
            result.data?.let {
                emit(TimePassBaseResult.success(data = it))
            }
        } else {
            result.data?.let {
                emit(TimePassBaseResult.error(it.message))
            }
        }
    }

    private fun isValidFetchLoginData(
        result: TimePassBaseResult<LoginResponse>
    ) = result.data != null && result.data.status == ApiResult.SUCCESS.value

    @Suppress(ANNOTATION_UNCHECKED_CAST)
    class Factory(
        private val profileRepository: ProfileRepository,
        private val profileScreenRepository: ProfileScreenRepository
    ) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
                return ProfileViewModel(profileRepository, profileScreenRepository) as T
            }
            throw IllegalArgumentException(UNKNOWN_VIEW_MODEL_CLASS)
        }
    }
}