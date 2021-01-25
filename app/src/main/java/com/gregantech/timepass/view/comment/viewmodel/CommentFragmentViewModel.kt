package com.gregantech.timepass.view.comment.viewmodel

import androidx.lifecycle.LiveDataScope
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import com.gregantech.timepass.base.TimePassBaseResult
import com.gregantech.timepass.network.repository.CommentListRepository
import com.gregantech.timepass.network.request.AddCommentRequest
import com.gregantech.timepass.network.request.CommentListRequest
import com.gregantech.timepass.network.response.VideoListResponse
import com.gregantech.timepass.network.response.comments.CommentListResponse
import com.gregantech.timepass.util.constant.ANNOTATION_UNCHECKED_CAST
import com.gregantech.timepass.util.constant.ErrorMessage
import com.gregantech.timepass.util.constant.UNKNOWN_VIEW_MODEL_CLASS
import com.gregantech.timepass.util.sharedpreference.SharedPreferenceHelper
import kotlinx.coroutines.Dispatchers


class CommentFragmentViewModel(
    private val commentListRepository: CommentListRepository,
    private val sharedPreferenceHelper: SharedPreferenceHelper
) :
    ViewModel() {


    fun getAdminVideoCommentList(postId: String) =
        liveData<TimePassBaseResult<CommentListResponse>>(Dispatchers.IO) {
            emit(TimePassBaseResult.loading(null))
            val result =
                commentListRepository.getAdminVideoComment(generateCommentListRequest(postId))
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

    fun getUserVideoCommentList(postId: String) =
        liveData<TimePassBaseResult<CommentListResponse>>(Dispatchers.IO) {
            emit(TimePassBaseResult.loading(null))
            val result =
                commentListRepository.getUserVideoComment(generateCommentListRequest(postId))
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

    fun addComment(comment: String) {
        TODO("Not yet implemented")
    }

    fun setUserVideoComment(comments: String, postId: String) =
        liveData<TimePassBaseResult<VideoListResponse>>(Dispatchers.IO) {
            emit(TimePassBaseResult.loading(null))
            val result =
                commentListRepository.addUserVideoComment(
                    generateAddCommentRequest(
                        comments,
                        postId
                    )
                )
        }

    fun setAdminVideoComment(comments: String, postId: String) =
        liveData<TimePassBaseResult<VideoListResponse>>(Dispatchers.IO) {
            emit(TimePassBaseResult.loading(null))
            val result =
                commentListRepository.addAdminVideoComment(
                    generateAddCommentRequest(
                        comments,
                        postId
                    )
                )
        }


    private fun generateAddCommentRequest(
        comments: String, postId: String
    ): AddCommentRequest {
        return AddCommentRequest(sharedPreferenceHelper.getUserId(), postId, comments)
    }

    private fun generateCommentListRequest(postId: String): CommentListRequest {
        return CommentListRequest(sharedPreferenceHelper.getUserId(), postId)
    }

    private suspend fun LiveDataScope<TimePassBaseResult<CommentListResponse>>.onFetchCategoryVideoListFail() {
        emit(TimePassBaseResult.error(ErrorMessage.NETWORK.value))
    }

    @Suppress(ANNOTATION_UNCHECKED_CAST)
    class Factory(
        private val commentListRepository: CommentListRepository,
        private val sharedPreferenceHelper: SharedPreferenceHelper
    ) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CommentFragmentViewModel::class.java)) {
                return CommentFragmentViewModel(
                    commentListRepository,
                    sharedPreferenceHelper
                ) as T
            }
            throw IllegalArgumentException(UNKNOWN_VIEW_MODEL_CLASS)
        }
    }
}