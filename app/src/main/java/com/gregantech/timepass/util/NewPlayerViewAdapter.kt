package com.gregantech.timepass.util

import android.net.Uri
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.video.VideoListener


class NewPlayerViewAdapter() {

    // for hold all players generated
    private var playersMap: MutableMap<Int, SimpleExoPlayer> = mutableMapOf()

    // for hold current player
    private var currentPlayingVideo: Pair<Int, SimpleExoPlayer>? = null

    fun releaseAllPlayers() {
        playersMap.map {
            it.value.release()
        }
    }

    fun pauseAllPlayers() {
        playersMap.map {
            it.value.playWhenReady = false
        }
    }

    fun changePlayerCurrentPosition(playerCurrentPosition: Long) {
        currentPlayingVideo?.second?.seekTo(playerCurrentPosition)
    }

    fun getCurrentPlayerPosition(): Long {
        return currentPlayingVideo?.second?.currentPosition ?: 0
    }

    fun releaseRecycledPlayers(index: Int) {
        playersMap[index]?.release()
    }

    fun pauseCurrentPlayingVideo() {
        if (currentPlayingVideo != null) {
            currentPlayingVideo?.second?.playWhenReady = false
        }
    }

    fun playIndexThenPausePreviousPlayer(index: Int) {
        if (playersMap.get(index)?.playWhenReady == false) {
            pauseCurrentPlayingVideo()
            playersMap.get(index)?.playWhenReady = true
            currentPlayingVideo = Pair(index, playersMap.get(index)!!)
        } else if (playersMap.get(index) == null) {
            pauseCurrentPlayingVideo()
        }

    }

    fun loadVideo(
        url: String,
        /*  callback: PlayerStateCallback,*/
        progressbar: View,
        item_index: Int? = null,
        playerView: PlayerView
    ) {
        val context = playerView.context
        val player = SimpleExoPlayer.Builder(context).build()
        player.playWhenReady = false
        player.repeatMode = Player.REPEAT_MODE_ALL
        // When changing track, retain the latest frame instead of showing a black screen
        playerView.setKeepContentOnPlayerReset(true)
        // We'll show the controller, change to true if want controllers as pause and start
        playerView.useController = false
        // Provide url to load the video from here
        val mediaSource = ProgressiveMediaSource.Factory(DefaultHttpDataSourceFactory("Demo"))
            .createMediaSource(Uri.parse(url))

        player.prepare(mediaSource)
        player.addVideoListener(object : VideoListener {
            override fun onVideoSizeChanged(
                width: Int,
                height: Int,
                unappliedRotationDegrees: Int,
                pixelWidthHeightRatio: Float
            ) {
                super.onVideoSizeChanged(
                    width,
                    height,
                    unappliedRotationDegrees,
                    pixelWidthHeightRatio
                )
                ((playerView.parent as ConstraintLayout).layoutParams as ConstraintLayout.LayoutParams).dimensionRatio =
                    width.toString().plus(":").plus(height.toString())
            }
        })

        playerView.player = player

        // add player with its index to map
        if (playersMap.containsKey(item_index))
            playersMap.remove(item_index)
        if (item_index != null)
            playersMap[item_index] = player

        playerView.player!!.addListener(object : Player.EventListener {

            override fun onPlayerError(error: ExoPlaybackException) {
                super.onPlayerError(error)
                //   this@loadVideo.context.toast("Oops! Error occurred while playing media.")
            }

            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                super.onPlayerStateChanged(playWhenReady, playbackState)

                if (playbackState == Player.STATE_BUFFERING) {
                    // callback.onVideoBuffering(player)
                    // Buffering..
                    // set progress bar visible here
                    // set thumbnail visible
                    //  thumbnail.visibility = View.VISIBLE
                    progressbar.visibility = View.VISIBLE
                }

                if (playbackState == Player.STATE_READY) {
                    // [PlayerView] has fetched the video duration so this is the block to hide the buffering progress bar
                    progressbar.visibility = View.GONE
                    // set thumbnail gone
                    //   thumbnail.visibility = View.GONE
                    //     callback.onVideoDurationRetrieved(playerView.player!!.duration, player)
                }

                if (playbackState == Player.STATE_READY && player.playWhenReady) {
                    // [PlayerView] has started playing/resumed the video
                    //   callback.onStartedPlaying(player)
                }
            }
        })
    }
}