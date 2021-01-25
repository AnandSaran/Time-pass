package com.gregantech.timepass.view.createvideo.viewmodel

import androidx.lifecycle.LiveDataScope
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import com.gregantech.timepass.base.TimePassBaseResult
import com.gregantech.timepass.network.ApiResult
import com.gregantech.timepass.network.repository.VideoListRepository
import com.gregantech.timepass.network.response.VideoUploadResponse
import com.gregantech.timepass.util.constant.ANNOTATION_UNCHECKED_CAST
import com.gregantech.timepass.util.constant.ErrorMessage
import com.gregantech.timepass.util.constant.UNKNOWN_VIEW_MODEL_CLASS
import com.gregantech.timepass.util.sharedpreference.SharedPreferenceHelper
import kotlinx.coroutines.Dispatchers
import java.io.File

class VideoUploadViewModel(
    private val videoListRepository: VideoListRepository
) : ViewModel() {

    fun updateVideo(videoTitle: String, videoDescription: String, videoFilePath: String) =
        liveData<TimePassBaseResult<VideoUploadResponse>>(Dispatchers.IO) {
            emit(TimePassBaseResult.loading(null))
            val result = videoListRepository.uploadVideo(
                SharedPreferenceHelper.getUserId(),
                videoTitle,
                videoDescription,
                File(videoFilePath)
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

    private suspend fun LiveDataScope<TimePassBaseResult<VideoUploadResponse>>.onUpdateProfileFail() {
        emit(TimePassBaseResult.error(ErrorMessage.NETWORK.value))

    }

    private suspend fun LiveDataScope<TimePassBaseResult<VideoUploadResponse>>.onUpdateProfileSuccess(
        result: TimePassBaseResult<VideoUploadResponse>
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
        result: TimePassBaseResult<VideoUploadResponse>
    ) = result.data != null && result.data.status == ApiResult.SUCCESS.value

    @Suppress(ANNOTATION_UNCHECKED_CAST)
    class Factory(
        private val videoListRepository: VideoListRepository
    ) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(VideoUploadViewModel::class.java)) {
                return VideoUploadViewModel(videoListRepository) as T
            }
            throw IllegalArgumentException(UNKNOWN_VIEW_MODEL_CLASS)
        }
    }
}