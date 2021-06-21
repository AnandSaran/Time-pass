package com.gregantech.timepass.view.live.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.gregantech.timepass.base.TimePassBaseResult
import com.gregantech.timepass.model.LiveUserCountResponse
import com.gregantech.timepass.model.LiveUserListRequest
import com.gregantech.timepass.model.LiveUserListResponse
import com.gregantech.timepass.network.repository.BroadCastRepository
import com.gregantech.timepass.network.request.BroadCastRequest
import com.gregantech.timepass.util.constant.ANNOTATION_UNCHECKED_CAST
import com.gregantech.timepass.util.constant.ErrorMessage
import com.gregantech.timepass.util.constant.UNKNOWN_VIEW_MODEL_CLASS
import com.gregantech.timepass.util.extension.launchPeriodicAsync
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LiveBroadcastViewModel(private val broadCastRepository: BroadCastRepository) : ViewModel() {


    private lateinit var userPlaybackSessionJob: Deferred<Unit>
    val obVoiceInputState = MutableLiveData<Boolean>(true)
    val obSwitchCamState = MutableLiveData<Boolean>(true)
    val obLiveUserCount = MutableLiveData<TimePassBaseResult<LiveUserCountResponse>>()
    var broadcastId: String? = null

    fun changeVoice() {
        obVoiceInputState.value = !(obVoiceInputState.value as Boolean)
    }

    fun changeCam() {
        obSwitchCamState.value = !(obSwitchCamState.value as Boolean)
    }

    fun updateBroadCastStatus(brodCastRequest: BroadCastRequest) {
        CoroutineScope(Dispatchers.IO).launch {
            val result = broadCastRepository.updateStatus(brodCastRequest)
            when (result.status) {
                TimePassBaseResult.Status.ERROR -> Log.e(
                    "LiveBroadcastViewModel",
                    "updateBroadCastStatus: error ${result.message}"
                )
                else -> {
                }
            }
        }
    }

    fun setupFetchLiveViewersJob(id: String) {
        broadcastId = id
        cancelFetchLiveViewersJob()
        userPlaybackSessionJob =
            CoroutineScope(Dispatchers.IO).launchPeriodicAsync(
                repeatMillis = 10000,
                action = ::fetchLiveViewers
            )
    }


    fun getLiveUserList(liveUserListRequest: LiveUserListRequest) =
        liveData<TimePassBaseResult<LiveUserListResponse>>(Dispatchers.IO) {
            emit(TimePassBaseResult.loading(null))
            val result = broadCastRepository.getLiveUsers(liveUserListRequest)
            when (result.status) {
                TimePassBaseResult.Status.SUCCESS -> {
                    emit(result)
                }
                else -> onLiveUserFetchFail()
            }
        }

    private fun fetchLiveViewers() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = broadCastRepository.getLiveUsersCount(broadcastId!!)
            obLiveUserCount.value = result
        }
    }

    private fun cancelFetchLiveViewersJob() {
        if (::userPlaybackSessionJob.isInitialized) {
            userPlaybackSessionJob.cancel()
        }
    }

    private suspend fun LiveDataScope<TimePassBaseResult<LiveUserListResponse>>.onLiveUserFetchFail() {
        emit(TimePassBaseResult.error(ErrorMessage.NETWORK.value))
    }


    @Suppress(ANNOTATION_UNCHECKED_CAST)
    class Factory(
        private val repository: BroadCastRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LiveBroadcastViewModel::class.java)) {
                return LiveBroadcastViewModel(
                    repository
                ) as T
            }
            throw IllegalArgumentException(UNKNOWN_VIEW_MODEL_CLASS)
        }
    }
}