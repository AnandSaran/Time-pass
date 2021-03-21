package com.gregantech.timepass.view.splash.viewmodel

import androidx.lifecycle.LiveDataScope
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import com.gregantech.timepass.base.TimePassBaseResult
import com.gregantech.timepass.model.AdvertisementResponse
import com.gregantech.timepass.network.repository.AdvertisementRepository
import com.gregantech.timepass.util.constant.ANNOTATION_UNCHECKED_CAST
import com.gregantech.timepass.util.constant.ErrorMessage
import com.gregantech.timepass.util.constant.UNKNOWN_VIEW_MODEL_CLASS
import kotlinx.coroutines.Dispatchers

class AdvertisementViewModel(private val advertisementRepository: AdvertisementRepository) :
    ViewModel() {

    fun getAdStatus() =
        liveData<TimePassBaseResult<AdvertisementResponse>>(Dispatchers.IO) {
            emit(TimePassBaseResult.loading(null))
            val result = advertisementRepository.getAdStatus()
            when (result.status) {
                TimePassBaseResult.Status.SUCCESS -> {
                    emit(result)
                }
                TimePassBaseResult.Status.ERROR -> {
                    onAddDetailFetchFail()
                }
                else -> {
                    onAddDetailFetchFail()
                }
            }
        }

    private suspend fun LiveDataScope<TimePassBaseResult<AdvertisementResponse>>.onAddDetailFetchFail() {
        emit(TimePassBaseResult.error(ErrorMessage.NETWORK.value))
    }

    @Suppress(ANNOTATION_UNCHECKED_CAST)
    class Factory(private val advertisementRepository: AdvertisementRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AdvertisementViewModel::class.java)) {
                return AdvertisementViewModel(advertisementRepository) as T
            }
            throw IllegalArgumentException(UNKNOWN_VIEW_MODEL_CLASS)
        }
    }

}