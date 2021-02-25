package com.gregantech.timepass.view.uservideolist.viewmodel

import androidx.lifecycle.LiveDataScope
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import com.gregantech.timepass.base.TimePassBaseResult
import com.gregantech.timepass.general.content.PostMoreOptionContentModel
import com.gregantech.timepass.model.RailBaseItemModel
import com.gregantech.timepass.model.RailItemTypeTwoModel
import com.gregantech.timepass.network.ApiResult
import com.gregantech.timepass.network.repository.VideoListRepository
import com.gregantech.timepass.network.request.UserPostDeleteRequest
import com.gregantech.timepass.network.response.UserPostDeleteResponse
import com.gregantech.timepass.util.constant.ANNOTATION_UNCHECKED_CAST
import com.gregantech.timepass.util.constant.ErrorMessage
import com.gregantech.timepass.util.constant.UNKNOWN_VIEW_MODEL_CLASS
import com.gregantech.timepass.util.sharedpreference.SharedPreferenceHelper
import kotlinx.coroutines.Dispatchers


class UserPostViewModel(
    private val videoListRepository: VideoListRepository,
    private val sharedPreferenceHelper: SharedPreferenceHelper
) : ViewModel() {


    fun deleteUserPost(railModel: RailBaseItemModel) =
        liveData<TimePassBaseResult<UserPostDeleteResponse>>(Dispatchers.IO) {
            emit(TimePassBaseResult.loading(null))
            val result =
                videoListRepository.deleteUserPost(generateUserPostDeleteRequest(railModel))
            when (result.status) {
                TimePassBaseResult.Status.SUCCESS -> {
                    if (result.data != null && result.data.status == ApiResult.SUCCESS.value) {
                        emit(result)
                    } else {
                        onFetchCategoryVideoListFail()
                    }
                }
                TimePassBaseResult.Status.ERROR -> {
                    onFetchCategoryVideoListFail()
                }
                else -> {
                    onFetchCategoryVideoListFail()
                }
            }
        }

    private fun generateUserPostDeleteRequest(railModel: RailBaseItemModel): UserPostDeleteRequest {
        return UserPostDeleteRequest(
            userID = sharedPreferenceHelper.getUserId(),
            postId = railModel.contentId
        )
    }

    private suspend fun LiveDataScope<TimePassBaseResult<UserPostDeleteResponse>>.onFetchCategoryVideoListFail() {
        emit(TimePassBaseResult.error(ErrorMessage.NETWORK.value))
    }

    fun generatePostMoreOptionContentModel(
        railModel: RailItemTypeTwoModel,
        isAdminPost: Boolean=false
    ): PostMoreOptionContentModel {
        val isSameUser = if (isAdminPost) {
            false
        } else {
            sharedPreferenceHelper.isPostUserAreSameUser(railModel.followerId)
        }

        return PostMoreOptionContentModel(
            true,
            isSameUser
        )
    }

    @Suppress(ANNOTATION_UNCHECKED_CAST)
    class Factory(
        private val videoListRepository: VideoListRepository,
        private val sharedPreferenceHelper: SharedPreferenceHelper
    ) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(UserPostViewModel::class.java)) {
                return UserPostViewModel(
                    videoListRepository,
                    sharedPreferenceHelper
                ) as T
            }
            throw IllegalArgumentException(UNKNOWN_VIEW_MODEL_CLASS)
        }
    }
}