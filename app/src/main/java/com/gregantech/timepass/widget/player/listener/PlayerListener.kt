package com.gregantech.timepass.widget.player.listener

import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.TrackSelectionArray

class PlayerListener : Player.EventListener {

    var playState: PlayState? = null

    // Player.EventListener implementation
    override fun onTracksChanged(
        tracks: TrackGroupArray,
        selections: TrackSelectionArray
    ) {
    }

    @ExperimentalStdlibApi
    override fun onPlayerStateChanged(
        playWhenReady: Boolean,
        playbackState: Int
    ) {
        when (playbackState) {
            ExoPlayer.STATE_BUFFERING -> playState?.buffering()
            ExoPlayer.STATE_ENDED -> playState?.onEnded()
            ExoPlayer.STATE_READY -> playState?.onPlaying()
            ExoPlayer.STATE_IDLE -> playState?.onIdle()
        }
    }

    override fun onPlayerError(error: ExoPlaybackException) {
    }

    private fun onPlayerReady() {

    }
}

interface PlayState {
    fun buffering()
    fun onPlaying()
    fun onIdle()
    fun onPlayerError(error: ExoPlaybackException)
    fun onEnded()
}