package com.gregantech.timepass.view.player.fullScreen

import android.content.Context
import android.net.Uri
import android.widget.ProgressBar
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.gregantech.timepass.R


object PlayerProvider {

    private val BANDWIDTH_METER = DefaultBandwidthMeter()
    private val playerPool = mutableListOf<ExoPlayer>()
    private val progressBarPool = mutableListOf<ProgressBar>()

    fun createPlayers(context: Context, videoList: ArrayList<TikTokModel>) {
        buildMediaSources(context, videoList).forEach { mediaSource ->
            val player = SimpleExoPlayer.Builder(context)
            val loadControl = DefaultLoadControl.Builder()
                .setBufferDurationsMs(25000, 50000, 1500, 2000).createDefaultLoadControl()

            player.setLoadControl(loadControl)
            val build = player.build().apply {
                playWhenReady = true
                repeatMode = Player.REPEAT_MODE_ALL
                muted = true
                prepare(mediaSource)
            }
            progressBarPool.add(ProgressBar(context))
            playerPool.add(build)
        }
    }

    fun getProgress(position: Int): ProgressBar? {
        return if (progressBarPool.size > 0)
            progressBarPool[position % progressBarPool.size]
        else null
    }


    fun getPlayer(position: Int): ExoPlayer? {
        return if (playerPool.size > 0)
            playerPool[position % playerPool.size]
        else null
    }

    private fun buildMediaSources(
        context: Context,
        videoList: ArrayList<TikTokModel>
    ): List<MediaSource> {
        return videoList.map { model ->
            if (model.sourceUrl?.endsWith(".mp4") == true || model.sourceUrl?.endsWith(".3gp") == true) {
                ProgressiveMediaSource.Factory(
                    DefaultDataSourceFactory(
                        context,
                        Util.getUserAgent(context, context.getString(R.string.app_name))
                    )
                )
                    .createMediaSource(Uri.parse(model.sourceUrl))
            } else {
                DashMediaSource.Factory(
                    DefaultDashChunkSource.Factory(
                        DefaultHttpDataSourceFactory(
                            context.getString(R.string.app_name),
                            BANDWIDTH_METER
                        )
                    ),
                    DefaultHttpDataSourceFactory(context.getString(R.string.app_name))
                ).createMediaSource(Uri.parse(model.sourceUrl))
            }
        }
    }

    fun mutePlayer() {
        playerPool.forEach { it.muted = true }
    }

    fun resumePlayer() {
        playerPool.forEach { it.playWhenReady = true }
    }

    fun stopPlayer() {
        playerPool.forEach { it.playWhenReady = false }
    }

    fun releasePlayer() {
        playerPool.forEach { it.release() }
        playerPool.clear()
    }

}
