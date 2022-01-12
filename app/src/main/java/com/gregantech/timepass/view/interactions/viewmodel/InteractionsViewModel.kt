package com.gregantech.timepass.view.interactions.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.gregantech.timepass.base.TimePassBaseResult
import com.gregantech.timepass.model.InteractionListResponse
import com.gregantech.timepass.model.InteractionRequest
import com.gregantech.timepass.model.InteractionsResponse
import com.gregantech.timepass.network.repository.InteractionsRepository
import com.gregantech.timepass.util.constant.ANNOTATION_UNCHECKED_CAST
import com.gregantech.timepass.util.constant.ErrorMessage
import com.gregantech.timepass.util.constant.UNKNOWN_VIEW_MODEL_CLASS
import com.gregantech.timepass.util.sharedpreference.SharedPreferenceHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class InteractionsViewModel(private val interactionsRepository: InteractionsRepository) :
    ViewModel() {


    fun getActivityState() = liveData<TimePassBaseResult<InteractionsResponse?>>(Dispatchers.IO) {
        emit(TimePassBaseResult.loading(null))
        val result =
            interactionsRepository.getActivityState(InteractionRequest(SharedPreferenceHelper.getUserId()))
        when (result.status) {
            TimePassBaseResult.Status.SUCCESS -> emit(TimePassBaseResult.success(data = result.data))
            TimePassBaseResult.Status.ERROR -> onFetchFail()
            else -> onFetchFail()
        }
    }

    fun getInteractions() = liveData<TimePassBaseResult<InteractionListResponse?>>(Dispatchers.IO) {
        emit(TimePassBaseResult.loading(null))
        val result =
            interactionsRepository.getInteractionList(InteractionRequest(SharedPreferenceHelper.getUserId()))
        when (result.status) {
            TimePassBaseResult.Status.SUCCESS -> emit(TimePassBaseResult.success(data = result.data))
            TimePassBaseResult.Status.ERROR -> onListFetchFail()
            else -> onListFetchFail()
        }
    }

    fun updateState() {
        viewModelScope.launch {
            val result =
                interactionsRepository.updateState(InteractionRequest(SharedPreferenceHelper.getUserId()))
            when (result.status) {
                TimePassBaseResult.Status.SUCCESS -> Log.e(
                    "InteractionsVM",
                    "updateState: success ${result.message}"
                )
                TimePassBaseResult.Status.ERROR -> Log.e(
                    "InteractionsVM",
                    "updateState: error ${result.message}"
                )
                else -> {}
            }
        }
    }

    private suspend fun LiveDataScope<TimePassBaseResult<InteractionsResponse?>>.onFetchFail() {
        emit(TimePassBaseResult.error(ErrorMessage.NETWORK.value))
    }

    private suspend fun LiveDataScope<TimePassBaseResult<InteractionListResponse?>>.onListFetchFail() {
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