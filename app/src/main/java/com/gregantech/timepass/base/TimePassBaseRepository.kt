package com.gregantech.timepass.base

import android.util.Log
import com.gregantech.timepass.util.log.LogUtil
import retrofit2.Response

abstract class TimePassBaseRepository {

    protected suspend fun <T> getResult(call: suspend () -> Response<T>): TimePassBaseResult<T> {
        try {
            val response = call()
            if (response.isSuccessful) {
                val body = response.body()
                Log.d("TimePassBaseRepository", "getResult: body is null ${body == null}")
                if (body != null) return TimePassBaseResult.success(body)
            }
            return error(" ${response.code()} ${response.message()}")
        } catch (e: Exception) {
            return error(e.message ?: e.toString())
        }
    }

    private fun <T> error(message: String): TimePassBaseResult<T> {
        LogUtil.printError("BaseDataSource", message)
        return TimePassBaseResult.error("Network call has failed for a following reason: $message")
    }
}