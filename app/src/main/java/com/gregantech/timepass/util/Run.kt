package com.gregantech.timepass.util

import android.os.Handler

/**
 * Created by anand
 */
class Run {
    companion object {
        fun after(delay: Long, process: () -> Unit) {
            Handler().postDelayed({
                process()
            }, delay)
        }
    }
}