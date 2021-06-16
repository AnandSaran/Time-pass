package com.gregantech.timepass.view.live.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gregantech.timepass.util.constant.ANNOTATION_UNCHECKED_CAST
import com.gregantech.timepass.util.constant.UNKNOWN_VIEW_MODEL_CLASS

class LiveBroadcastViewModel : ViewModel() {

    val obVoiceInputState = MutableLiveData<Boolean>(true)
    val obSwitchCamState = MutableLiveData<Boolean>(true)

    fun changeVoice() {
        obVoiceInputState.value = !(obVoiceInputState.value as Boolean)
    }

    fun changeCam() {
        obSwitchCamState.value = !(obSwitchCamState.value as Boolean)
    }


    @Suppress(ANNOTATION_UNCHECKED_CAST)
    class Factory : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LiveBroadcastViewModel::class.java)) {
                return LiveBroadcastViewModel(
                ) as T
            }
            throw IllegalArgumentException(UNKNOWN_VIEW_MODEL_CLASS)
        }
    }
}