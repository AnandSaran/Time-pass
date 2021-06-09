package com.gregantech.timepass.widget.player

import android.content.Context
import android.util.AttributeSet
import android.view.*
import android.widget.FrameLayout
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelection
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.SubtitleView
import com.google.android.exoplayer2.util.EventLogger
import com.gregantech.timepass.R
import com.gregantech.timepass.model.playback.PlaybackInfoModel
import com.gregantech.timepass.widget.player.listener.PlayerListener
import com.gregantech.timepass.widget.player.helper.TimePassPlayerHelper

class TimePassPlayerView : FrameLayout {

    private val SURFACE_TYPE_NONE = 0
    private val SURFACE_TYPE_SURFACE_VIEW = 1
    private val SURFACE_TYPE_TEXTURE_VIEW = 2
    private val drmSchemeUuid = C.WIDEVINE_UUID

    // Todo - change to configurable filed in future for resize mode and surface.
    private var resizeMode: Int = AspectRatioFrameLayout.RESIZE_MODE_FIT
    private var surfaceType: Int = SURFACE_TYPE_SURFACE_VIEW

    /**
     * content frame of exoplayer
     */
    private lateinit var contentFrame: AspectRatioFrameLayout

    /**
     * exoplayer
     */
    private lateinit var player: SimpleExoPlayer

    /**
     * player view for exoplayer
     */
    private lateinit var playerView: View

    /**
     * subtitle view for exoplayer
     */
    private lateinit var subtitleView: SubtitleView

    /**
     * default track selection
     */
    private lateinit var trackSelector: DefaultTrackSelector

    /**
     * track selection factory
     */
    private var trackSelectionFactory: TrackSelection.Factory = AdaptiveTrackSelection.Factory()

    /**
     * player listener
     */
    private val playerListener = PlayerListener()

    /**
     * event log for debug and analytics purpose
     */
    private lateinit var eventLogger: EventLogger

    var castPlayerHelper = TimePassPlayerHelper(context)

    init {
        initPlayerView()
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    /**
     * set playback info and player
     * @param playbackInfoModel playback information include playback url, drmToken, license url
     */
    fun preparePlayback(
        playbackInfoModel: PlaybackInfoModel
    ) {
        castPlayerHelper.setPlaybackInfoModel(playbackInfoModel)
        setupPlayer(playbackInfoModel)
        setupPlayerEventListener()
        setupPlayerMediaSource()
    }

    /**
     * init player UI
     */
    private fun initPlayerView() {
        trackSelector = DefaultTrackSelector(context, trackSelectionFactory)

        LayoutInflater.from(context).inflate(R.layout.view_time_pass_player, this)
        contentFrame = findViewById(R.id.exo_content_frame)
        setResizeModeRaw(contentFrame, resizeMode)

        setupPlayerView()
    }

    // Create a surface view and insert it into the content frame, if there is one.
    private fun setupPlayerView() {
        // Subtitle view.
        subtitleView = findViewById(R.id.exo_subtitles)

        // Create a surface view and insert it into the content frame, if there is one.
        if (surfaceType != SURFACE_TYPE_NONE) {
            val params = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
            )
            playerView =
                if (surfaceType == SURFACE_TYPE_TEXTURE_VIEW)
                    TextureView(context)
                else
                    SurfaceView(context)
            playerView.layoutParams = params
            contentFrame.addView(playerView, 0)
        } else {
            playerView = SurfaceView(context)
        }
    }

    private fun setResizeModeRaw(aspectRatioFrame: AspectRatioFrameLayout, resizeMode: Int) {
        aspectRatioFrame.resizeMode = resizeMode
        //  aspectRatioFrame.setAspectRatio(16f / 9f)
    }

    private fun setupPlayer(playbackInfoModel: PlaybackInfoModel) {
        if (!::player.isInitialized) {
            player = castPlayerHelper.generatePlayer(trackSelector)

            if (playerView is TextureView) {
                player.setVideoTextureView(playerView as TextureView)
            } else if (playerView is SurfaceView) {
                player.setVideoSurfaceView(playerView as SurfaceView)
            }
            player.playWhenReady = true
        } else {
            player.removeListener(playerListener)
        }
        castPlayerHelper.setPlayer(player)
    }

    private fun setupPlayerEventListener() {
        player.addListener(playerListener)
    }

    private fun setupPlayerMediaSource() {
        val mediaSource = castPlayerHelper.createVideoMediaSource()
        if (mediaSource != null) {
            player.prepare(mediaSource, false, false)
        }
    }
}