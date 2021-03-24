package com.gregantech.timepass.view.home.viewmodel

import androidx.lifecycle.LiveDataScope
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import com.gregantech.timepass.base.TimePassBaseResult
import com.gregantech.timepass.model.AppConfigResponse
import com.gregantech.timepass.network.repository.AppConfigRepository
import com.gregantech.timepass.util.constant.ANNOTATION_UNCHECKED_CAST
import com.gregantech.timepass.util.constant.ErrorMessage
import com.gregantech.timepass.util.constant.UNKNOWN_VIEW_MODEL_CLASS
import kotlinx.coroutines.Dispatchers

class AppConfigViewModel(private val appConfigRepository: AppConfigRepository) : ViewModel() {

    fun getAppConfig() = liveData<TimePassBaseResult<AppConfigResponse?>>(Dispatchers.IO) {
        emit(TimePassBaseResult.loading(null))
        val result = appConfigRepository.getAppConfig()
        when (result.status) {
            TimePassBaseResult.Status.SUCCESS -> emit(TimePassBaseResult.success(data = result.data))
            TimePassBaseResult.Status.ERROR -> onAppConfigFail()
            else -> onAppConfigFail()
        }
    }

    private suspend fun LiveDataScope<TimePassBaseResult<AppConfigResponse?>>.onAppConfigFail() {
        emit(TimePassBaseResult.error(ErrorMessage.NETWORK.value))
    }

    @Suppress(ANNOTATION_UNCHECKED_CAST)
    class Factory(private val appConfigRepository: AppConfigRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AppConfigViewModel::class.java)) {
                return AppConfigViewModel(appConfigRepository) as T
            }
            throw IllegalArgumentException(UNKNOWN_VIEW_MODEL_CLASS)
        }
    }

}