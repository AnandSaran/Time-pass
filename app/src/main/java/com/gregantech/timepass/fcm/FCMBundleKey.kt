package com.gregantech.timepass.fcm

enum class FCMBundleKey(val value: String) {
    TYPE("type"),
    TITLE("title"),
    STREAM_URL("stream_url"),
    STREAM_ID("stream_id"),
}

enum class FCMBundleValue(val value: String) {
    LIVE("live"),
}