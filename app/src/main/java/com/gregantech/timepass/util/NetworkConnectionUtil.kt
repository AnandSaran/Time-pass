package com.gregantech.timepass.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.gregantech.timepass.general.ConnectionType

object NetworkConnectionUtil {

    fun isMobileDataConnected(context: Context): Boolean {
        var result = false
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val networkCapabilities =
                connectivityManager.getNetworkCapabilities(network) ?: return false
            result = networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
        } else {
            connectivityManager.run {
                connectivityManager.activeNetworkInfo?.run {
                    result = type == ConnectivityManager.TYPE_MOBILE
                }
            }
        }
        return result
    }

    fun getNetworkType(context: Context): ConnectionType {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    return ConnectionType.MOBILE_DATA
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    return ConnectionType.WIFI
                }
            }
        } else {
            connectivityManager.run {
                connectivityManager.activeNetworkInfo?.run {
                    return when (type) {
                        ConnectivityManager.TYPE_MOBILE -> {
                            ConnectionType.MOBILE_DATA
                        }
                        ConnectivityManager.TYPE_WIFI -> {
                            ConnectionType.WIFI
                        }
                        else -> {
                            ConnectionType.UNKNOWN
                        }
                    }
                }
            }
        }
        return ConnectionType.UNKNOWN
    }
}