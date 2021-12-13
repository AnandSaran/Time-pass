package com.gregantech.timepass.view.tiktok.work

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.Cache
import com.google.android.exoplayer2.upstream.cache.CacheUtil
import com.google.android.exoplayer2.util.Util
import com.gregantech.timepass.R
import com.gregantech.timepass.general.bundklekey.TikTokBundleKeyEnum
import com.gregantech.timepass.util.PlayerUtil.cache
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll

private const val TAG = "PreCachingService"

class PreCachingService(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result = coroutineScope {

        val dataList = inputData.getStringArray(TikTokBundleKeyEnum.VIDEO_URLS.value)

        val jobs = dataList?.map { data ->
            async {
                val dataUri = Uri.parse(data)
                val dataSpec = DataSpec(dataUri, 0, 500 * 1024, null)

                val dataSource: DataSource =
                    DefaultDataSourceFactory(
                        applicationContext,
                        Util.getUserAgent(
                            applicationContext,
                            applicationContext.getString(R.string.app_name)
                        )
                    ).createDataSource()

                preloadVideo(dataSpec,
                    cache(applicationContext),
                    dataSource,
                    CacheUtil.ProgressListener { requestLength: Long, bytesCached: Long, newBytesCached: Long ->
                        val downloadPercentage = (bytesCached * 100.0
                                / requestLength)
                        //Log.d(TAG, "downloadPercentage: $downloadPercentage")
                    }
                )
            }
        }
        jobs?.joinAll()
        Result.success()
    }

    private fun preloadVideo(
        dataSpec: DataSpec?,
        cache: Cache?,
        upstream: DataSource?,
        progressListener: CacheUtil.ProgressListener?
    ) {
        Log.d(TAG, "preloadVideo")
        try {
            CacheUtil.cache(
                dataSpec,
                cache,
                upstream,
                progressListener,
                null
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}