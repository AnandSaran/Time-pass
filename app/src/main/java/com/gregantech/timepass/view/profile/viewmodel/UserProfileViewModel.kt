package com.gregantech.timepass.view.profile.viewmodel

import androidx.lifecycle.LiveDataScope
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import com.gregantech.timepass.base.TimePassBaseResult
import com.gregantech.timepass.network.repository.ProfileRepository
import com.gregantech.timepass.network.repository.VideoListRepository
import com.gregantech.timepass.network.repository.local.UserProfileScreenRepository
import com.gregantech.timepass.network.request.OtherProfileRequest
import com.gregantech.timepass.network.request.UserVideoListRequest
import com.gregantech.timepass.network.response.LoginResponse
import com.gregantech.timepass.network.response.VideoListResponse
import com.gregantech.timepass.util.constant.ANNOTATION_UNCHECKED_CAST
import com.gregantech.timepass.util.constant.ErrorMessage
import com.gregantech.timepass.util.constant.UNKNOWN_VIEW_MODEL_CLASS
import kotlinx.coroutines.Dispatchers

class UserProfileViewModel(
    private val profileRepository: ProfileRepository,
    private val userProfileScreenRepository: UserProfileScreenRepository,
    private val videoListRepository: VideoListRepository

) : ViewModel() {

    fun getUserProfile(followerId: String) =
        liveData<TimePassBaseResult<LoginResponse>>(Dispatchers.IO) {
            emit(TimePassBaseResult.loading(null))
            val result =
                profileRepository.getOtherProfile(generateUserProfileRequest(followerId))
            when (result.status) {
                TimePassBaseResult.Status.SUCCESS -> {
                    emit(result)
                }
                TimePassBaseResult.Status.ERROR -> {
                    onFetchProfileFail()
                }
                else -> {
                    onFetchProfileFail()
                }
            }
        }

    private fun generateUserProfileRequest(
        followerId: String
    ): OtherProfileRequest {
        return OtherProfileRequest(userProfileScreenRepository.getUserId(), followerId)
    }

    fun getUserVideoList(userId: String, pageNo: Int) =
        liveData<TimePassBaseResult<VideoListResponse>>(Dispatchers.IO) {
            emit(TimePassBaseResult.loading(null))
            val result =
                videoListRepository.fetchUserVideoList(generateVideoListRequest(userId, pageNo))
            when (result.status) {
                TimePassBaseResult.Status.SUCCESS -> {
                    emit(result)
                }
                TimePassBaseResult.Status.ERROR -> {
                    onFetchCategoryVideoListFail()
                }
                else -> {
                    onFetchCategoryVideoListFail()
                }
            }
        }

    private fun generateVideoListRequest(userId: String, pageNo: Int): UserVideoListRequest {
        return UserVideoListRequest(userId, pageNo)
    }

    fun getMoreCategoryVideoList(userId: String, pageNo: Int) =
        liveData<TimePassBaseResult<VideoListResponse>>(Dispatchers.IO) {
            emit(TimePassBaseResult.loading(null))
            val result =
                videoListRepository.fetchUserVideoList(generateVideoListRequest(userId, pageNo))
            when (result.status) {
                TimePassBaseResult.Status.SUCCESS -> {
                    emit(result)
                }
                TimePassBaseResult.Status.ERROR -> {
                    onFetchCategoryVideoListFail()
                }
                else -> {
                    onFetchCategoryVideoListFail()
                }
            }
        }

    private suspend fun LiveDataScope<TimePassBaseResult<VideoListResponse>>.onFetchCategoryVideoListFail() {
        emit(TimePassBaseResult.error(ErrorMessage.NETWORK.value))
    }

    private suspend fun LiveDataScope<TimePassBaseResult<LoginResponse>>.onFetchProfileFail() {
        emit(TimePassBaseResult.error(ErrorMessage.NETWORK.value))
    }


    @Suppress(ANNOTATION_UNCHECKED_CAST)
    class Factory(
        private val profileRepository: ProfileRepository,
        private val userProfileScreenRepository: UserProfileScreenRepository,
        private val videoListRepository: VideoListRepository
    ) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(UserProfileViewModel::class.java)) {
                return UserProfileViewModel(
                    profileRepository,
                    userProfileScreenRepository,
                    videoListRepository
                ) as T
            }
            throw IllegalArgumentException(UNKNOWN_VIEW_MODEL_CLASS)
        }
    }
}