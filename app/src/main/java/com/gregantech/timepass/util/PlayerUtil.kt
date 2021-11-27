package com.gregantech.timepass.util

import android.content.Context
import android.net.Uri
import com.google.android.exoplayer2.database.ExoDatabaseProvider
import com.google.android.exoplayer2.offline.StreamKey
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.upstream.FileDataSource
import com.google.android.exoplayer2.upstream.cache.*
import com.gregantech.timepass.R
import java.io.File

object PlayerUtil {

    private val CACHE_SIZE = 50 * 1024 * 1024L
    private var cacheInstance: Cache? = null

    private fun upStream(context: Context) =
        DefaultDataSourceFactory(context, context.getString(R.string.app_name))

    fun cache(context: Context): Cache {
        return cacheInstance ?: run {
            val exoCacheDir =
                File("${context.cacheDir.absolutePath}/${context.getString(R.string.app_name)}")
            val evictor = LeastRecentlyUsedCacheEvictor(CACHE_SIZE)
            SimpleCache(exoCacheDir, evictor, ExoDatabaseProvider(context)).also {
                cacheInstance = it
            }
        }
    }

    val cacheStreamKeys = arrayListOf(
        StreamKey(0, 1),
        StreamKey(1, 1),
        StreamKey(2, 1),
        StreamKey(3, 1),
        StreamKey(4, 1)
    )

    fun cacheFactory(context: Context): CacheDataSourceFactory {
        val simpleCache = cache(context)
        return CacheDataSourceFactory(
            simpleCache,
            upStream(context),
            FileDataSource.Factory(),
            CacheDataSinkFactory(simpleCache, CacheDataSink.DEFAULT_FRAGMENT_SIZE),
            CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR,
            object : CacheDataSource.EventListener {
                override fun onCachedBytesRead(cacheSizeBytes: Long, cachedBytesRead: Long) {
                    /* Log.d(
                         "TikTokFragmentX",
                         "onCachedBytesRead. cacheSizeBytes:$cacheSizeBytes, cachedBytesRead: $cachedBytesRead"
                     )*/
                }

                override fun onCacheIgnored(reason: Int) {
                    //Log.d("TikTokFragmentX", "onCacheIgnored. reason:$reason")
                }
            }
        )
    }

    fun buildMediaSources(
        context: Context,
        url: String
    ) = when {
        url.endsWith(".mp4") || url.endsWith(".3gp") -> {
            ProgressiveMediaSource
                .Factory(cacheFactory(context))
                .setStreamKeys(cacheStreamKeys)
                .createMediaSource(Uri.parse(url))
        }
        url.endsWith(".m3u8") -> {
            HlsMediaSource.Factory(cacheFactory(context))
                .setStreamKeys(cacheStreamKeys)
                .setAllowChunklessPreparation(true)
                .createMediaSource(Uri.parse(url))
        }
        else -> {
            DashMediaSource.Factory(
                DefaultDashChunkSource
                    .Factory(
                        DefaultHttpDataSourceFactory(context.getString(R.string.app_name))
                    ),
                cacheFactory(context)
            ).createMediaSource(Uri.parse(url))
        }
    }

    private fun nonCache(context: Context, url: String) =
        ProgressiveMediaSource.Factory(DefaultHttpDataSourceFactory("Demo"))
            .createMediaSource(Uri.parse(url))

}