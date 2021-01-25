package com.gregantech.timepass.base

data class TimePassBaseResult<out T>(val status: Status, val data: T?, val message: String?) {

    enum class Status {
        SUCCESS,
        ERROR,
        LOADING
    }

    companion object {
        fun <T> success(data: T): TimePassBaseResult<T> {
            return TimePassBaseResult(Status.SUCCESS, data, null)
        }

        fun <T> error(message: String, data: T? = null): TimePassBaseResult<T> {
            return TimePassBaseResult(Status.ERROR, data, message)
        }

        fun <T> loading(data: T? = null): TimePassBaseResult<T> {
            return TimePassBaseResult(Status.LOADING, data, null)
        }
    }
}