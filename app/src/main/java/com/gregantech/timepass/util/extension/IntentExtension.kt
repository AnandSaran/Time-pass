package com.gregantech.timepass.util.extension

import android.content.Intent
import android.os.Bundle
import android.util.Log

fun Bundle?.toString(TAG: String? = null) {

    if (this == null) {
        Log.d(TAG.plus("bundleToString"), "args null")
        return
    }

    for (key in this.keySet()) {
        Log.d(TAG.plus("bundleToString"), "Key -> $key value -> ${this.get(key)}")
    }
}

fun Intent?.toString(TAG: String? = null) {
    if (this == null) {
        Log.d(TAG.plus("IntentToString"), "intent null")
        return
    }
    val stringBuilder = StringBuilder("action: ")
        .append(this.action)
        .append(" data: ")
        .append(this.dataString)
        .append(" extras: ")

    this.extras?.let {
        for (key in it.keySet()) stringBuilder.append(key)
            .append("=").append(it[key]).append(" ")
    }

    Log.d("IntentToString", stringBuilder.toString())
}