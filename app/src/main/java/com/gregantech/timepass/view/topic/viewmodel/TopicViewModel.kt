package com.gregantech.timepass.view.topic.viewmodel

import androidx.lifecycle.LiveDataScope
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import com.gregantech.timepass.base.TimePassBaseResult
import com.gregantech.timepass.network.repository.TopicRepository
import com.gregantech.timepass.util.constant.ANNOTATION_UNCHECKED_CAST
import com.gregantech.timepass.util.constant.ErrorMessage
import com.gregantech.timepass.util.constant.UNKNOWN_VIEW_MODEL_CLASS
import com.gregantech.timepass.view.topic.model.TopicResponse
import kotlinx.coroutines.Dispatchers

class TopicViewModel(private val topicRepository: TopicRepository) : ViewModel() {


    fun getTopics() = liveData<TimePassBaseResult<TopicResponse?>>(Dispatchers.IO) {
        emit(TimePassBaseResult.loading(null))
        val result = topicRepository.getTopics()
        when (result.status) {
            TimePassBaseResult.Status.SUCCESS -> emit(TimePassBaseResult.success(data = result.data))
            TimePassBaseResult.Status.ERROR -> onTopicFetchFail()
            else -> onTopicFetchFail()
        }
    }

    private suspend fun LiveDataScope<TimePassBaseResult<TopicResponse?>>.onTopicFetchFail() {
        emit(TimePassBaseResult.error(ErrorMessage.NETWORK.value))
    }

    @Suppress(ANNOTATION_UNCHECKED_CAST)
    class Factory(
        private val topicRepository: TopicRepository
    ) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(TopicViewModel::class.java)) {
                return TopicViewModel(
                    topicRepository
                ) as T
            }
            throw IllegalArgumentException(UNKNOWN_VIEW_MODEL_CLASS)
        }
    }


}