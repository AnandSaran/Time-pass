package com.gregantech.timepass.util.exoplayer

import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.upstream.cache.CacheKeyFactory
import com.google.android.exoplayer2.upstream.cache.CacheUtil
import com.gregantech.timepass.base.TimePassApplication.Companion.simpleCache

object VideoPreLoadingService {
    private fun cacheVideo(
        dataSpec: DataSpec,
        defaultCacheKeyFactory: CacheKeyFactory?,
        dataSource: DataSource,
        progressListener: CacheUtil.ProgressListener
    ) {
        CacheUtil.cache(
            dataSpec,
            simpleCache,
            defaultCacheKeyFactory,
            dataSource,
            progressListener,
            null
        )
    }
}