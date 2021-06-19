package com.gregantech.timepass.util.extension

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay

fun CoroutineScope.launchPeriodicAsync(
    repeatMillis: Long = 1000,
    action: () -> Unit
) = this.async {
    if (repeatMillis > 0) {
        while (true) {
            action()
            delay(repeatMillis)
        }
    } else {
        action()
    }
}