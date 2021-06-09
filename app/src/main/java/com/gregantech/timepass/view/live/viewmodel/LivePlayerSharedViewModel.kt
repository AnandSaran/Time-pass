package com.gregantech.timepass.view.live.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.gregantech.timepass.model.playback.PlaybackInfoModel
import com.gregantech.timepass.util.constant.ANNOTATION_UNCHECKED_CAST
import com.gregantech.timepass.util.constant.UNKNOWN_VIEW_MODEL_CLASS
import kotlinx.coroutines.launch

/**
 * shared view model for live player
 */
class LivePlayerSharedViewModel : ViewModel() {
    val playBack = MutableLiveData<PlaybackInfoModel>()
    val title = MutableLiveData<String>()
    val showController = MutableLiveData<Boolean>()

    fun updatePlayBack(item: PlaybackInfoModel) {
        viewModelScope.launch {
            playBack.value = item
        }
    }

    fun updateTitle(item: String) {
        viewModelScope.launch {
            title.value = item
        }
    }

    fun updateShowController(item: Boolean) {
        viewModelScope.launch {
            showController.value = item
        }
    }

    @Suppress(ANNOTATION_UNCHECKED_CAST)
    class Factory : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LivePlayerSharedViewModel::class.java)) {
                return LivePlayerSharedViewModel(
                ) as T
            }
            throw IllegalArgumentException(UNKNOWN_VIEW_MODEL_CLASS)
        }
    }
}