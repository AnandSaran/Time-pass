package com.gregantech.timepass.view.interactions.viewmodel

import androidx.lifecycle.LiveDataScope
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import com.gregantech.timepass.base.TimePassBaseResult
import com.gregantech.timepass.model.InteractionsResponse
import com.gregantech.timepass.network.repository.InteractionsRepository
import com.gregantech.timepass.util.constant.ANNOTATION_UNCHECKED_CAST
import com.gregantech.timepass.util.constant.ErrorMessage
import com.gregantech.timepass.util.constant.UNKNOWN_VIEW_MODEL_CLASS
import kotlinx.coroutines.Dispatchers

class InteractionsViewModel(private val interactionsRepository: InteractionsRepository) :
    ViewModel() {

    fun getActivityState() = liveData<TimePassBaseResult<InteractionsResponse?>>(Dispatchers.IO) {
        emit(TimePassBaseResult.loading(null))
        val result = interactionsRepository.getActivityState()
        when (result.status) {
            TimePassBaseResult.Status.SUCCESS -> emit(TimePassBaseResult.success(data = result.data))
            TimePassBaseResult.Status.ERROR -> onFetchFail()
            else -> onFetchFail()
        }
    }

    private suspend fun LiveDataScope<TimePassBaseResult<InteractionsResponse?>>.onFetchFail() {
        emit(TimePassBaseResult.error(ErrorMessage.NETWORK.value))
    }

    @Suppress(ANNOTATION_UNCHECKED_CAST)
    class Factory(private val interactionsRepository: InteractionsRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(InteractionsViewModel::class.java)) {
                return InteractionsViewModel(interactionsRepository) as T
            }
            throw IllegalArgumentException(UNKNOWN_VIEW_MODEL_CLASS)
        }
    }

}