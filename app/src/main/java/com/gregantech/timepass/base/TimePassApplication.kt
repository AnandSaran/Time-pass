package com.gregantech.timepass.base

import android.app.Application
import androidx.work.WorkManager
import com.google.android.exoplayer2.database.ExoDatabaseProvider
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.gregantech.timepass.util.sharedpreference.SharedPreferenceHelper
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.features.logging.ANDROID
import io.ktor.client.features.logging.LogLevel
import io.ktor.client.features.logging.Logger
import io.ktor.client.features.logging.Logging
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.dsl.module

class TimePassApplication : Application() {

    companion object {
        var simpleCache: SimpleCache? = null
        var leastRecentlyUsedCacheEvictor: LeastRecentlyUsedCacheEvictor? = null
        var exoDatabaseProvider: ExoDatabaseProvider? = null
        var exoPlayerCacheSize: Long = 90 * 1024 * 1024
    }

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@TimePassApplication)
            modules(module {
                single { WorkManager.getInstance(get()) }
                single { initKtorClient() }
            })
        }

        if (leastRecentlyUsedCacheEvictor == null) {
            leastRecentlyUsedCacheEvictor = LeastRecentlyUsedCacheEvictor(exoPlayerCacheSize)
        }

        if (exoDatabaseProvider != null) {
            exoDatabaseProvider = ExoDatabaseProvider(this)
        }

        if (simpleCache == null) {
            simpleCache =
                exoDatabaseProvider?.let {
                    SimpleCache(
                        cacheDir, leastRecentlyUsedCacheEvictor!!,
                        it
                    )
                }
        }

        initSharedPreference()
    }

    private fun initSharedPreference() {
        SharedPreferenceHelper.initSharedPreference(this)
    }

    fun initKtorClient() = HttpClient(Android) {
        install(Logging) {
            logger = Logger.ANDROID
            level = LogLevel.ALL
        }
    }
}