package com.gregantech.timepass.view.login.viewmodel

import androidx.lifecycle.LiveDataScope
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import com.gregantech.timepass.base.TimePassBaseResult
import com.gregantech.timepass.network.ApiResult
import com.gregantech.timepass.network.repository.LoginRepository
import com.gregantech.timepass.network.request.LoginRequest
import com.gregantech.timepass.network.response.LoginResponse
import com.gregantech.timepass.util.constant.ANNOTATION_UNCHECKED_CAST
import com.gregantech.timepass.util.constant.ErrorMessage
import com.gregantech.timepass.util.constant.UNKNOWN_VIEW_MODEL_CLASS
import kotlinx.coroutines.Dispatchers

class LoginViewModel(private val loginRepository: LoginRepository) : ViewModel() {

    fun fetchLogin(mobileNumber: String) =
        liveData<TimePassBaseResult<LoginResponse>>(Dispatchers.IO) {
            emit(TimePassBaseResult.loading(null))
            val result = loginRepository.login(generateLoginRequest(mobileNumber))
            when (result.status) {
                TimePassBaseResult.Status.SUCCESS -> {
                    onFetchLoginSuccess(result)
                }
                TimePassBaseResult.Status.ERROR -> {
                    onFetchLoginFail()
                }
                else -> {
                    onFetchLoginFail()
                }
            }
        }

    private suspend fun LiveDataScope<TimePassBaseResult<LoginResponse>>.onFetchLoginSuccess(result: TimePassBaseResult<LoginResponse>) {
        if (isValidFetchLoginData(result)) {
            result.data?.let {
                emit(TimePassBaseResult.success(data = it))
            }
        } else {
            result.data?.let {
                emit(TimePassBaseResult.error(it.message))
            }
        }
    }

    private fun isValidFetchLoginData(
        result: TimePassBaseResult<LoginResponse>
    ) = result.data != null && result.data.status == ApiResult.SUCCESS.value

    private suspend fun LiveDataScope<TimePassBaseResult<LoginResponse>>.onFetchLoginFail() {
        emit(TimePassBaseResult.error(ErrorMessage.NETWORK.value))
    }

    private fun generateLoginRequest(mobileNumber: String): LoginRequest {
        return LoginRequest(mobileNumber)
    }

    @Suppress(ANNOTATION_UNCHECKED_CAST)
    class Factory(private val loginRepository: LoginRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
                return LoginViewModel(loginRepository) as T
            }
            throw IllegalArgumentException(UNKNOWN_VIEW_MODEL_CLASS)
        }
    }
}