package com.gregantech.timepass.util.log

import android.util.Log
import com.gregantech.timepass.BuildConfig

/**
 * Created by anand
 */

object LogUtil {
    fun print(tag: String, text: String) {
        if (BuildConfig.BUILD_TYPE.contentEquals("debug")) {
            Log.i(tag, "PRINT:$text")
        }
    }

    fun printException(e: Exception) {
        e.printStackTrace()
        //        Crashlytics.logException(e);
    }

    fun printThrowable(throwable: Throwable?) {
        // Check if null as sometimes it causes NullPointerException
        throwable?.printStackTrace()

        //        Crashlytics.logException(throwable);
    }

    fun printError(tag: String, string: String) {
        if (BuildConfig.BUILD_TYPE.contentEquals("debug")) {
            Log.e(tag, "ERROR:$string")
        }
    }

    fun printDebug(tag: String, string: String) {
        if (BuildConfig.BUILD_TYPE.contentEquals("debug")) {
            Log.d(tag, "DEBUG:$string")
        }
    }
}