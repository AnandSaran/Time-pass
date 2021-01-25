package com.gregantech.timepass.view.player.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.*
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.BandwidthMeter
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.gregantech.timepass.R
import com.gregantech.timepass.base.TimePassBaseActivity
import com.gregantech.timepass.databinding.ActivityPlayerBinding
import com.gregantech.timepass.general.bundklekey.CategoryDetailBundleKeyEnum
import com.gregantech.timepass.general.bundklekey.PlayerBundleKey
import com.gregantech.timepass.util.constant.EMPTY_LONG
import com.gregantech.timepass.util.constant.EMPTY_STRING

class PlayerActivity : TimePassBaseActivity() {
    private lateinit var binding: ActivityPlayerBinding
    private lateinit var videoSurfaceView: PlayerView
    private lateinit var videoPlayer: SimpleExoPlayer
    private lateinit var mediaUrl: String
    private var resumePosition: Long = 0

    companion object {
        fun present(
            context: Context, videoUrl: String, videoPosition: Long
        ) {
            val intent = Intent(context, PlayerActivity::class.java)
            intent.putExtra(PlayerBundleKey.VIDEO_URL.value, videoUrl)
            intent.putExtra(PlayerBundleKey.VIDEO_POSITION.value, videoPosition)
            context.startActivity(intent)
        }

        fun generateIntent(
            context: Context, videoUrl: String, videoPosition: Long
        ): Intent {
            val intent = Intent(context, PlayerActivity::class.java)
            intent.putExtra(PlayerBundleKey.VIDEO_URL.value, videoUrl)
            intent.putExtra(PlayerBundleKey.VIDEO_POSITION.value, videoPosition)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initDataBinding()
        handleIntentData()
        createPlayer()
        createDialog()
    }

    private fun initDataBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_player)
    }

    private fun handleIntentData() {
        mediaUrl = intent.getStringExtra(PlayerBundleKey.VIDEO_URL.value) ?: EMPTY_STRING
        resumePosition = intent.getLongExtra(PlayerBundleKey.VIDEO_POSITION.value, EMPTY_LONG)
    }

    private fun createPlayer() {
        videoSurfaceView = PlayerView(this)
        videoSurfaceView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
        val bandwidthMeter: BandwidthMeter = DefaultBandwidthMeter()
        val videoTrackSelectionFactory: TrackSelection.Factory =
            AdaptiveTrackSelection.Factory(bandwidthMeter)
        val trackSelector: TrackSelector = DefaultTrackSelector(videoTrackSelectionFactory)


        // 2. Create the player
        videoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector)
        videoSurfaceView.useController = true
        videoSurfaceView.player = videoPlayer
    }

    private fun createDialog() {

        addContentView(
            videoSurfaceView,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
        playVideo()


    }

    private fun playVideo() {
        videoSurfaceView.player = videoPlayer
        val dataSourceFactory: DataSource.Factory =
            DefaultDataSourceFactory(
                this,
                Util.getUserAgent(
                    this,
                    this.getString(R.string.app_name)
                )
            )
        if (mediaUrl.isNotEmpty()) {
            val videoSource: MediaSource = ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(Uri.parse(mediaUrl))
            videoPlayer.prepare(videoSource)
            videoPlayer.seekTo(resumePosition)
            videoPlayer.playWhenReady = true
        }
        videoPlayer.addListener(playerEventListener())

    }

    private fun playerEventListener(): Player.EventListener {
        return object : Player.EventListener {
            override fun onTimelineChanged(
                timeline: Timeline,
                manifest: Any?,
                reason: Int
            ) {
            }

            override fun onTracksChanged(
                trackGroups: TrackGroupArray,
                trackSelections: TrackSelectionArray
            ) {
            }

            override fun onLoadingChanged(isLoading: Boolean) {}
            override fun onPlayerStateChanged(
                playWhenReady: Boolean,
                playbackState: Int
            ) {
                when (playbackState) {
                    Player.STATE_BUFFERING -> {
                        showProgressBar()
                    }
                    Player.STATE_ENDED -> {
                        videoPlayer.seekTo(0)
                    }
                    Player.STATE_IDLE -> {
                    }
                    Player.STATE_READY -> {
                        dismissProgressBar()
                    }
                    else -> {
                    }
                }
            }

            override fun onRepeatModeChanged(repeatMode: Int) {}
            override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {}
            override fun onPlayerError(error: ExoPlaybackException) {}
            override fun onPositionDiscontinuity(reason: Int) {}
            override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters) {}
            override fun onSeekProcessed() {}
        }
    }

    override fun onBackPressed() {
        releasePlayer()
        val data = Intent()
        data.putExtra(CategoryDetailBundleKeyEnum.VIDEO_POSITION.value, getPlayCurrentPosition())
        setResult(Activity.RESULT_OK, data)
        super.onBackPressed()
    }

    private fun releasePlayer() {
        if (::videoPlayer.isInitialized) {
            videoPlayer.release()
        }
    }

    override fun onPause() {
        super.onPause()
        if (::videoPlayer.isInitialized) {
            videoPlayer.playWhenReady = false
        }
    }

    override fun onResume() {
        super.onResume()
        if (::videoPlayer.isInitialized) {
            videoPlayer.playWhenReady = true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::videoPlayer.isInitialized) {
            videoPlayer.release()
        }
    }

    private fun getPlayCurrentPosition(): Long {
        return if (::videoPlayer.isInitialized) {
            videoPlayer.currentPosition
        } else {
            EMPTY_LONG
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            videoSurfaceView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            videoSurfaceView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
        }
    }
}