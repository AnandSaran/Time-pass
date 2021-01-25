package com.gregantech.timepass.view.categorydetail.viewmodel

import android.app.DownloadManager
import android.net.Uri
import android.os.Environment
import android.webkit.MimeTypeMap
import android.webkit.URLUtil
import androidx.lifecycle.*
import com.gregantech.timepass.base.TimePassBaseResult
import com.gregantech.timepass.model.RailItemTypeTwoModel
import com.gregantech.timepass.network.repository.VideoListRepository
import com.gregantech.timepass.network.request.UserFollowRequest
import com.gregantech.timepass.network.request.VideoLikeRequest
import com.gregantech.timepass.network.request.VideoListRequest
import com.gregantech.timepass.network.response.VideoListResponse
import com.gregantech.timepass.repository.factory.RailDataFactory
import com.gregantech.timepass.util.constant.ANNOTATION_UNCHECKED_CAST
import com.gregantech.timepass.util.constant.ErrorMessage
import com.gregantech.timepass.util.constant.UNKNOWN_VIEW_MODEL_CLASS
import com.gregantech.timepass.util.sharedpreference.SharedPreferenceHelper
import kotlinx.coroutines.Dispatchers


class CategoryDetailFragmentViewModel(
    private val videoListRepository: VideoListRepository,
    private val sharedPreferenceHelper: SharedPreferenceHelper
) :
    ViewModel() {

    val downloadRequest = MutableLiveData<DownloadManager.Request>()

    fun getCategoryVideoList(categoryId: String, pageNo: Int) =
        liveData<TimePassBaseResult<VideoListResponse>>(Dispatchers.IO) {
            emit(TimePassBaseResult.loading(null))
            val result =
                videoListRepository.fetchCategoryVideoList(
                    generateVideoListRequest(
                        categoryId,
                        pageNo
                    )
                )
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

    fun getMoreCategoryVideoList(categoryId: String, pageNo: Int) =
        liveData<TimePassBaseResult<VideoListResponse>>(Dispatchers.IO) {
            emit(TimePassBaseResult.loading(null))
            val result =
                videoListRepository.fetchCategoryVideoList(
                    generateVideoListRequest(
                        categoryId,
                        pageNo
                    )
                )
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
                videoListRepository.setVideoLike(
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

    private fun generateVideoListRequest(categoryId: String, pageNo: Int): VideoListRequest {
        return VideoListRequest(sharedPreferenceHelper.getUserId(), categoryId, pageNo)
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

    @Suppress(ANNOTATION_UNCHECKED_CAST)
    class Factory(
        private val videoListRepository: VideoListRepository,
        private val sharedPreferenceHelper: SharedPreferenceHelper
    ) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CategoryDetailFragmentViewModel::class.java)) {
                return CategoryDetailFragmentViewModel(
                    videoListRepository,
                    sharedPreferenceHelper
                ) as T
            }
            throw IllegalArgumentException(UNKNOWN_VIEW_MODEL_CLASS)
        }
    }
}