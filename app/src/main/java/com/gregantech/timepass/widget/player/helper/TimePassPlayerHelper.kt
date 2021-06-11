package com.gregantech.timepass.widget.player.helper

import android.content.Context
import android.net.Uri
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.RenderersFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.drm.UnsupportedDrmException
import com.google.android.exoplayer2.ext.rtmp.RtmpDataSourceFactory
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DataSource
import com.gregantech.timepass.model.playback.PlaybackInfoModel


class TimePassPlayerHelper(
    private val context: Context
) {
    private lateinit var playbackInfo: PlaybackInfoModel
    private lateinit var player: SimpleExoPlayer

    fun setPlayer(player: SimpleExoPlayer) {
        this.player = player
    }

    fun setPlaybackInfoModel(playbackInfoModel: PlaybackInfoModel) {
        this.playbackInfo = playbackInfoModel
    }

    private fun buildRenderersFactory(): RenderersFactory {
        @DefaultRenderersFactory.ExtensionRendererMode val extensionRendererMode =
            DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF
        return DefaultRenderersFactory(context)
            .setExtensionRendererMode(extensionRendererMode)
    }

    fun createVideoMediaSource(): MediaSource? {
        return try {
            generateVideoMediaSource()
        } catch (e: UnsupportedDrmException) {
            print(e.message)
            null
        }
    }


    fun addPlayerEventListener(listener: Player.EventListener) {
        if (::player.isInitialized) {
            player.addListener(listener)
        }
    }

    fun releasePlayer() {
        if (::player.isInitialized) {
            player.release()
        }
    }

    fun pausePlayer() {
        if (::player.isInitialized) {
            player.playWhenReady = false
        }
    }

    fun stopPlayer() {
        if (::player.isInitialized) {
            player.stop()
        }
    }

    fun startPlayer() {
        if (::player.isInitialized) {
            player.playWhenReady = true
        }
    }

    fun retryPlayer() {
        if (::player.isInitialized) {

        }
    }

    fun seekTo(position: Long) {
        if (::player.isInitialized) {
            val totalDuration = getTotalDuration()
            val seekToPosition =
                if (position <= totalDuration) {
                    position
                } else {
                    totalDuration
                }

            player.seekTo(seekToPosition)
        }
    }

    fun getBufferedPosition(): Long {
        return if (::player.isInitialized) {
            player.bufferedPosition
        } else {
            0
        }
    }

    fun getCurrentPosition(): Long {
        return if (::player.isInitialized) {
            player.currentPosition
        } else {
            0
        }
    }

    fun getTotalDuration(): Long {
        return if (::player.isInitialized) {
            player.duration
        } else {
            0
        }
    }

    private fun generateVideoMediaSource(): MediaSource {
        val dataSourceFactory: DataSource.Factory =
            RtmpDataSourceFactory()
        return ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(Uri.parse(playbackInfo.url))
    }

    fun generatePlayer(trackSelector: DefaultTrackSelector): SimpleExoPlayer {
        val renderersFactory = buildRenderersFactory()
        return SimpleExoPlayer.Builder(context, renderersFactory)
            .setTrackSelector(trackSelector)
            .build()
    }

    fun setupPlayerMediaSource() {
        val mediaSource = createVideoMediaSource()
        if (mediaSource != null) {
            player.prepare(mediaSource, false, false)
        }
    }
}