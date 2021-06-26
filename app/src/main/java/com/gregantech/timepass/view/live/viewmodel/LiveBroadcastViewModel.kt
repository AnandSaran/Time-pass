package com.gregantech.timepass.view.live.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.gregantech.timepass.base.TimePassBaseResult
import com.gregantech.timepass.model.LiveUserCountResponse
import com.gregantech.timepass.model.LiveUserListRequest
import com.gregantech.timepass.model.LiveUserListResponse
import com.gregantech.timepass.network.repository.BroadCastRepository
import com.gregantech.timepass.network.repository.FireStoreRepository
import com.gregantech.timepass.network.request.BroadCastRequest
import com.gregantech.timepass.util.constant.ANNOTATION_UNCHECKED_CAST
import com.gregantech.timepass.util.constant.UNKNOWN_VIEW_MODEL_CLASS
import com.gregantech.timepass.util.extension.launchPeriodicAsync
import kotlinx.coroutines.*

class LiveBroadcastViewModel(
    private val broadCastRepository: BroadCastRepository,
    private val fireStoreRepository: FireStoreRepository? = null
) : ViewModel() {

    private lateinit var userPlaybackSessionJob: Deferred<Unit>

    val obVoiceInputState = MutableLiveData<Boolean>(true)
    val obSwitchCamState = MutableLiveData<Boolean>(true)
    val obToggleCommentState = MutableLiveData<Boolean>(true)

    val obLiveUserCount = MutableLiveData<TimePassBaseResult<LiveUserCountResponse>>()
    val obLiveUserList = MutableLiveData<TimePassBaseResult<LiveUserListResponse>>()
    var broadcastId: String? = null

    fun changeVoice() {
        obVoiceInputState.value = !(obVoiceInputState.value as Boolean)
    }

    fun changeCam() {
        obSwitchCamState.value = !(obSwitchCamState.value as Boolean)
    }

    fun toggleCommentState() {
        obToggleCommentState.value = !(obToggleCommentState.value as Boolean)
    }

    fun updateBroadCastStatus(brodCastRequest: BroadCastRequest) {
        CoroutineScope(Dispatchers.IO).launch {
            val result = broadCastRepository.updateStatus(brodCastRequest)
            when (result.status) {
                TimePassBaseResult.Status.SUCCESS -> {
                    if (brodCastRequest.liveStatus == false)
                        fireStoreRepository?.updateBroadcastState(brodCastRequest.streamId!!, false)
                }
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

    fun getLiveUserList(liveUserListRequest: LiveUserListRequest) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                delay(10000)
                val result = broadCastRepository.getLiveUsers(liveUserListRequest)
                when (result.status) {
                    TimePassBaseResult.Status.SUCCESS -> {
                        obLiveUserList.postValue(result)
                    }
                    else -> obLiveUserList.postValue(
                        TimePassBaseResult.error(
                            result.message ?: "Error"
                        )
                    )
                }
            }
        }
    }

    private fun fetchLiveViewers() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = broadCastRepository.getLiveUsersCount(broadcastId!!)
            obLiveUserCount.postValue(result)
        }
    }

    private fun cancelFetchLiveViewersJob() {
        if (::userPlaybackSessionJob.isInitialized) {
            userPlaybackSessionJob.cancel()
        }
    }


    @Suppress(ANNOTATION_UNCHECKED_CAST)
    class Factory(
        private val repository: BroadCastRepository,
        private val fireStoreRepository: FireStoreRepository? = null
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LiveBroadcastViewModel::class.java)) {
                return LiveBroadcastViewModel(
                    repository,
                    fireStoreRepository
                ) as T
            }
            throw IllegalArgumentException(UNKNOWN_VIEW_MODEL_CLASS)
        }
    }
}