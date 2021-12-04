package com.gregantech.timepass.view.profile.viewmodel

import android.app.DownloadManager
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.webkit.MimeTypeMap
import android.webkit.URLUtil
import androidx.lifecycle.*
import com.gregantech.timepass.base.TimePassBaseResult
import com.gregantech.timepass.model.RailItemTypeTwoModel
import com.gregantech.timepass.model.getFileToDownload
import com.gregantech.timepass.network.repository.VideoListRepository
import com.gregantech.timepass.network.request.UserVideoListRequest
import com.gregantech.timepass.network.request.VideoLikeRequest
import com.gregantech.timepass.network.response.LoginResponse
import com.gregantech.timepass.network.response.VideoListResponse
import com.gregantech.timepass.util.constant.ANNOTATION_UNCHECKED_CAST
import com.gregantech.timepass.util.constant.ErrorMessage
import com.gregantech.timepass.util.constant.UNKNOWN_VIEW_MODEL_CLASS
import com.gregantech.timepass.util.sharedpreference.SharedPreferenceHelper
import kotlinx.coroutines.Dispatchers

class UserVideoListActivityViewModel(
    private val videoListRepository: VideoListRepository,
    private val sharedPreferenceHelper: SharedPreferenceHelper
) : ViewModel() {
    val downloadRequest = MutableLiveData<DownloadManager.Request>()

    private fun generateVideoListRequest(
        userId: String,
        pageNo: Int,
        videoId: String? = null
    ): UserVideoListRequest {
        return UserVideoListRequest(userId, pageNo, videoId)
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

    fun getFullScreenVideos(userId: String, pageNo: Int, videoId: String?) =
        liveData<TimePassBaseResult<VideoListResponse>>(Dispatchers.IO) {
            emit(TimePassBaseResult.loading(null))

            val request = generateVideoListRequest(userId, pageNo, videoId)

            Log.d(
                "UserVideoListX",
                "videoId $videoId is null ${videoId == null}. Using ${if (videoId == null) "fetchVideo" else "fetchUserVideo"} "
            )

            val result = if (videoId == null)
                videoListRepository.fetchFullScreenVideoList(request)
            else
                videoListRepository.fetchFullScreenUserVideoList(request)

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

    fun createDownloadRequest(
        railItemTypeTwoModel: RailItemTypeTwoModel,
        appName: String
    ) {
        val downloadRequest =
            DownloadManager.Request(Uri.parse(generateDownloadUrl(railItemTypeTwoModel)))
        downloadRequest.setTitle(railItemTypeTwoModel.title)
        downloadRequest.setDescription(railItemTypeTwoModel.title)

        downloadRequest.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

        val filename: String =
            URLUtil.guessFileName(
                railItemTypeTwoModel.getFileToDownload(),
                null,
                MimeTypeMap.getFileExtensionFromUrl(railItemTypeTwoModel.getFileToDownload())
            )
        downloadRequest.setDestinationInExternalPublicDir(
            Environment.DIRECTORY_MOVIES,
            filename
        )
        updateDownloadRequest(downloadRequest)
    }

    private fun generateDownloadUrl(railItemTypeTwoModel: RailItemTypeTwoModel): String {
        return if (railItemTypeTwoModel.isImage != null && railItemTypeTwoModel.isImage!!) {
            railItemTypeTwoModel.image
        } else {
            railItemTypeTwoModel.video
        }
    }

    private fun updateDownloadRequest(request: DownloadManager.Request) {
        downloadRequest.value = request
    }

    private fun generateVideoLikeRequest(id: String, isLiked: Boolean): VideoLikeRequest {
        return VideoLikeRequest(sharedPreferenceHelper.getUserId(), id, isLiked)
    }

    @Suppress(ANNOTATION_UNCHECKED_CAST)
    class Factory(
        private val videoListRepository: VideoListRepository,
        private val sharedPreferenceHelper: SharedPreferenceHelper
    ) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(UserVideoListActivityViewModel::class.java)) {
                return UserVideoListActivityViewModel(
                    videoListRepository,
                    sharedPreferenceHelper
                ) as T
            }
            throw IllegalArgumentException(UNKNOWN_VIEW_MODEL_CLASS)
        }
    }
}