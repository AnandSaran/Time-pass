package com.gregantech.timepass.view.userlist.viewmodel

import androidx.lifecycle.LiveDataScope
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import com.gregantech.timepass.base.TimePassBaseResult
import com.gregantech.timepass.network.repository.UserListRepository
import com.gregantech.timepass.network.request.SearchUsersRequest
import com.gregantech.timepass.network.request.UserFollowListRequest
import com.gregantech.timepass.network.response.userlist.UserListResponse
import com.gregantech.timepass.util.constant.ANNOTATION_UNCHECKED_CAST
import com.gregantech.timepass.util.constant.ErrorMessage
import com.gregantech.timepass.util.constant.UNKNOWN_VIEW_MODEL_CLASS
import com.gregantech.timepass.util.sharedpreference.SharedPreferenceHelper
import kotlinx.coroutines.Dispatchers

class UserListActivityViewModel(
    private val userListRepository: UserListRepository,
    private val sharedPref: SharedPreferenceHelper
) : ViewModel() {

    fun getSearchUserList(searchName: String) =
        liveData<TimePassBaseResult<UserListResponse>>(Dispatchers.IO) {
            emit(TimePassBaseResult.loading(null))
            val result =
                userListRepository.getSearchUserList(
                    generateSearchUsersRequest(searchName)
                )
            when (result.status) {
                TimePassBaseResult.Status.SUCCESS -> {
                    emit(result)
                }
                TimePassBaseResult.Status.ERROR -> {
                    onFetchUserFollowingListFail()
                }
                else -> {
                    onFetchUserFollowingListFail()
                }
            }
        }

    fun getUserFollowingList() =
        liveData<TimePassBaseResult<UserListResponse>>(Dispatchers.IO) {
            emit(TimePassBaseResult.loading(null))
            val result =
                userListRepository.getUserFollowing(
                    generateUserFollowListRequest()
                )
            when (result.status) {
                TimePassBaseResult.Status.SUCCESS -> {
                    emit(result)
                }
                TimePassBaseResult.Status.ERROR -> {
                    onFetchUserFollowingListFail()
                }
                else -> {
                    onFetchUserFollowingListFail()
                }
            }
        }

    fun getUserFollowerList() =
        liveData<TimePassBaseResult<UserListResponse>>(Dispatchers.IO) {
            emit(TimePassBaseResult.loading(null))
            val result =
                userListRepository.getUserFollower(
                    generateUserFollowListRequest()
                )
            when (result.status) {
                TimePassBaseResult.Status.SUCCESS -> {
                    emit(result)
                }
                TimePassBaseResult.Status.ERROR -> {
                    onFetchUserFollowingListFail()
                }
                else -> {
                    onFetchUserFollowingListFail()
                }
            }
        }

    private suspend fun LiveDataScope<TimePassBaseResult<UserListResponse>>.onFetchUserFollowingListFail() {
        emit(TimePassBaseResult.error(ErrorMessage.PLEASE_TRY_AGAIN.value))
    }

    private fun generateUserFollowListRequest(): UserFollowListRequest {
        return UserFollowListRequest(sharedPref.getUserId())
    }

    private fun generateSearchUsersRequest(searchName: String): SearchUsersRequest {
        return SearchUsersRequest(sharedPref.getUserId(), searchName)
    }


    @Suppress(ANNOTATION_UNCHECKED_CAST)
    class Factory(
        private val userListRepository: UserListRepository,
        private val sharedPref: SharedPreferenceHelper
    ) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(UserListActivityViewModel::class.java)) {
                return UserListActivityViewModel(userListRepository, sharedPref) as T
            }
            throw IllegalArgumentException(UNKNOWN_VIEW_MODEL_CLASS)
        }
    }
}