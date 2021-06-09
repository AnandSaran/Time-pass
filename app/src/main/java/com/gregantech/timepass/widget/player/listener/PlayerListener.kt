package com.gregantech.timepass.widget.player.listener

import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.TrackSelectionArray

class PlayerListener : Player.EventListener {

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
            ExoPlayer.STATE_BUFFERING -> {

            }
            ExoPlayer.STATE_ENDED -> {

            }
            ExoPlayer.STATE_READY -> {
                onPlayerReady()
            }
            ExoPlayer.STATE_IDLE -> {

            }
            else -> {

            }
        }
    }

    override fun onPlayerError(error: ExoPlaybackException) {
    }

    private fun onPlayerReady() {
    }
}