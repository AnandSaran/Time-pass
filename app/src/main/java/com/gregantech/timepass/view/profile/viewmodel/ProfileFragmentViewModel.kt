package com.gregantech.timepass.view.profile.viewmodel

import android.app.DownloadManager
import android.net.Uri
import android.os.Environment
import android.webkit.MimeTypeMap
import android.webkit.URLUtil
import androidx.lifecycle.*
import com.gregantech.timepass.base.TimePassBaseResult
import com.gregantech.timepass.model.RailItemTypeTwoModel
import com.gregantech.timepass.network.ApiResult
import com.gregantech.timepass.network.repository.LoginRepository
import com.gregantech.timepass.network.repository.VideoListRepository
import com.gregantech.timepass.network.request.LoginRequest
import com.gregantech.timepass.network.request.UserFollowRequest
import com.gregantech.timepass.network.request.UserVideoListRequest
import com.gregantech.timepass.network.request.VideoLikeRequest
import com.gregantech.timepass.network.response.LoginResponse
import com.gregantech.timepass.network.response.VideoListResponse
import com.gregantech.timepass.util.constant.ANNOTATION_UNCHECKED_CAST
import com.gregantech.timepass.util.constant.ErrorMessage
import com.gregantech.timepass.util.constant.UNKNOWN_VIEW_MODEL_CLASS
import com.gregantech.timepass.util.sharedpreference.SharedPreferenceHelper
import kotlinx.coroutines.Dispatchers


class ProfileFragmentViewModel(
    private val videoListRepository: VideoListRepository,
    private val loginRepository: LoginRepository,
    private val sharedPreferenceHelper: SharedPreferenceHelper
) :
    ViewModel() {

    val downloadRequest = MutableLiveData<DownloadManager.Request>()

    fun fetchLogin() =
        liveData<TimePassBaseResult<LoginResponse>>(Dispatchers.IO) {
            emit(TimePassBaseResult.loading(null))
            val result =
                loginRepository.login(generateLoginRequest(sharedPreferenceHelper.getUserMobileNumber()))
            when (result.status) {
                TimePassBaseResult.Status.SUCCESS -> {
                    onFetchLoginSuccess(result)
                }
                TimePassBaseResult.Status.ERROR -> {
                }
                else -> {
                }
            }
        }


    fun getUserVideoList(pageNo: Int) =
        liveData<TimePassBaseResult<VideoListResponse>>(Dispatchers.IO) {
            emit(TimePassBaseResult.loading(null))
            val result =
                videoListRepository.fetchUserVideoList(generateVideoListRequest(pageNo))
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

    fun getMoreCategoryVideoList(pageNo: Int) =
        liveData<TimePassBaseResult<VideoListResponse>>(Dispatchers.IO) {
            emit(TimePassBaseResult.loading(null))
            val result =
                videoListRepository.fetchUserVideoList(generateVideoListRequest(pageNo))
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

    fun setUserFollow(isFollow: Boolean, followerId: String) =
        liveData<TimePassBaseResult<VideoListResponse>>(Dispatchers.IO) {
            emit(TimePassBaseResult.loading(null))
            val result =
                videoListRepository.setUserFollow(generateUserFollowRequest(isFollow, followerId))
        }

    fun setVideoLike(id: String, isLiked: Boolean) =
        liveData<TimePassBaseResult<VideoListResponse>>(Dispatchers.IO) {
            emit(TimePassBaseResult.loading(null))
            val result =
                videoListRepository.setUserVideoLike(
                    generateVideoLikeRequest(
                        id,
                        isLiked
                    )
                )
        }

    private fun generateUserFollowRequest(
        isFollowed: Boolean,
        followerId: String
    ): UserFollowRequest {
        return UserFollowRequest(sharedPreferenceHelper.getUserId(), isFollowed, followerId)
    }

    private fun generateVideoLikeRequest(id: String, isLiked: Boolean): VideoLikeRequest {
        return VideoLikeRequest(sharedPreferenceHelper.getUserId(), id, isLiked)
    }

    private fun generateVideoListRequest(pageNo: Int): UserVideoListRequest {
        return UserVideoListRequest(sharedPreferenceHelper.getUserId(), pageNo)
    }

    private suspend fun LiveDataScope<TimePassBaseResult<VideoListResponse>>.onFetchCategoryVideoListFail() {
        emit(TimePassBaseResult.error(ErrorMessage.NETWORK.value))
    }

    fun createDownloadRequest(
        railItemTypeTwoModel: RailItemTypeTwoModel,
        appName: String
    ) {
        val downloadRequest =
            DownloadManager.Request(Uri.parse(railItemTypeTwoModel.video))
        downloadRequest.setTitle(railItemTypeTwoModel.title)
        downloadRequest.setDescription(railItemTypeTwoModel.title)
        downloadRequest.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        val filename: String =
            URLUtil.guessFileName(
                railItemTypeTwoModel.video,
                null,
                MimeTypeMap.getFileExtensionFromUrl(railItemTypeTwoModel.video)
            )
        downloadRequest.setDestinationInExternalPublicDir(
            Environment.DIRECTORY_MOVIES,
            filename
        )
        updateDownloadRequest(downloadRequest)
    }

    private fun updateDownloadRequest(request: DownloadManager.Request) {
        downloadRequest.value = request
    }

    private fun generateLoginRequest(mobileNumber: String): LoginRequest {
        return LoginRequest(mobileNumber)
    }

    private suspend fun LiveDataScope<TimePassBaseResult<LoginResponse>>.onFetchLoginSuccess(result: TimePassBaseResult<LoginResponse>) {
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
        private val videoListRepository: VideoListRepository,
        private val loginRepository: LoginRepository,
        private val sharedPreferenceHelper: SharedPreferenceHelper
    ) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ProfileFragmentViewModel::class.java)) {
                return ProfileFragmentViewModel(
                    videoListRepository,
                    loginRepository,
                    sharedPreferenceHelper
                ) as T
            }
            throw IllegalArgumentException(UNKNOWN_VIEW_MODEL_CLASS)
        }
    }
}