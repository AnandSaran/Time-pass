package com.gregantech.timepass.view.live.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.gregantech.timepass.util.constant.ANNOTATION_UNCHECKED_CAST
import com.gregantech.timepass.util.constant.UNKNOWN_VIEW_MODEL_CLASS
import kotlinx.coroutines.launch

/**
 * shared view model for live broadcast
 */
class LiveBroadCastSharedViewModel : ViewModel() {
    val docKey = MutableLiveData<String>()

    fun updateLiveBroadCastDocumentKey(item: String?) {
        item?.let {
            viewModelScope.launch {
                docKey.value = item
            }
        }
    }

    @Suppress(ANNOTATION_UNCHECKED_CAST)
    class Factory : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LiveBroadCastSharedViewModel::class.java)) {
                return LiveBroadCastSharedViewModel(
                ) as T
            }
            throw IllegalArgumentException(UNKNOWN_VIEW_MODEL_CLASS)
        }
    }
}