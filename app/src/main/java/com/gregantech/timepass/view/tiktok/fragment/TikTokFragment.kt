package com.gregantech.timepass.view.tiktok.fragment

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView
import com.gregantech.timepass.R
import com.gregantech.timepass.base.TimePassBaseFragment
import com.gregantech.timepass.databinding.FragmentTikTokBinding
import com.gregantech.timepass.general.bundklekey.TikTokBundleKeyEnum
import com.gregantech.timepass.network.response.Video
import com.gregantech.timepass.view.tiktok.PlayerUtil.buildMediaSources

class TikTokFragment : TimePassBaseFragment() {

    private lateinit var binding: FragmentTikTokBinding
    private val videoModel by lazy { arguments?.getParcelable<Video>(TikTokBundleKeyEnum.VIDEO_DATA.value) }

    private var simplePlayer: SimpleExoPlayer? = null
    private var toPlayVideoPosition: Int = -1


    companion object {
        fun newInstance(video: Video) = TikTokFragment()
            .apply {
                arguments = Bundle().apply {
                    putParcelable(TikTokBundleKeyEnum.VIDEO_DATA.value, video)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_tik_tok, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAssets()
    }

    private fun initViewModel() {

    }

    private fun setAssets() {
        val simplePlayer = getPlayer()
        binding.fullScreenPlayer.player = simplePlayer
        videoModel?.run {
            binding.tvName.text = videoTitle
            binding.tvDesc.text = videoDescription
            prepareMedia(videoName) // start playing
        }
    }

    private val playerCallback: Player.EventListener = object : Player.EventListener {
        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            Log.e("onPlayerStateChanged", "playbackState: $playbackState")
        }

        override fun onPlayerError(error: ExoPlaybackException) {
            super.onPlayerError(error)
            Log.e("onPlayerError", "error $error")
        }
    }

    private fun prepareVideoPlayer() {
        val loadControl =
            DefaultLoadControl.Builder().setBufferDurationsMs(32 * 1024, 64 * 1024, 1024, 1024)
                .createDefaultLoadControl()
        simplePlayer = SimpleExoPlayer.Builder(requireContext()).setLoadControl(loadControl).build()
    }

    private fun getPlayer(): SimpleExoPlayer? {
        if (simplePlayer == null) {
            prepareVideoPlayer()
        }
        return simplePlayer
    }

    private fun prepareMedia(linkUrl: String) {
        Log.d("TikTokFragment", "prepareMedia linkUrl: $linkUrl")

        val mediaSource = buildMediaSources(requireContext(), linkUrl)

        simplePlayer?.apply {
            prepare(mediaSource, true, true)
            repeatMode = Player.REPEAT_MODE_ONE
            playWhenReady = true
            addListener(playerCallback)
        }
        toPlayVideoPosition = -1
    }


    private fun setArtwork(drawable: Drawable, playerView: PlayerView) {
        playerView.useArtwork = true
        playerView.defaultArtwork = drawable
    }

    private fun playVideo() {
        simplePlayer?.playWhenReady = true
    }

    private fun restartVideo() {
        if (simplePlayer == null) {
            videoModel?.videoName?.let {
                prepareMedia(it)
            }
        } else {
            simplePlayer?.seekToDefaultPosition()
            simplePlayer?.playWhenReady = true
        }
    }

    private fun pauseVideo() {
        simplePlayer?.playWhenReady = false
    }

    private fun releasePlayer() {
        simplePlayer?.stop(true)
        simplePlayer?.release()
    }

    override fun onPause() {
        pauseVideo()
        super.onPause()
    }

    override fun onResume() {
        restartVideo()
        super.onResume()
    }

    override fun onDestroy() {
        releasePlayer()
        super.onDestroy()
    }

}