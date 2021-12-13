package com.gregantech.timepass.view.live.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.exoplayer2.ExoPlaybackException
import com.gregantech.timepass.R
import com.gregantech.timepass.base.TimePassBaseFragment
import com.gregantech.timepass.databinding.FragmentLivePlayerBinding
import com.gregantech.timepass.model.playback.PlaybackInfoModel
import com.gregantech.timepass.util.constant.VIEW_MODEL_IN_ACCESSIBLE_MESSAGE
import com.gregantech.timepass.util.extension.gone
import com.gregantech.timepass.util.extension.keepScreenOn
import com.gregantech.timepass.util.extension.show
import com.gregantech.timepass.view.live.lifecycleobserver.LivePlayerLifeCycleObserver
import com.gregantech.timepass.view.live.lifecycleobserver.listener.PlayerLifeCycleActionListener
import com.gregantech.timepass.view.live.viewmodel.LivePlayerSharedViewModel
import com.gregantech.timepass.widget.player.listener.PlayState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Live player
 */
class LivePlayerFragment : TimePassBaseFragment() {
    companion object {
        fun newInstance() = LivePlayerFragment()
    }

    private var inBufferMode = false

    private lateinit var mContext: Context
    private lateinit var binding: FragmentLivePlayerBinding
    private lateinit var livePlayerLifeCycleObserver: LivePlayerLifeCycleObserver
    private lateinit var viewModelFactory: LivePlayerSharedViewModel.Factory

    private val viewModel: LivePlayerSharedViewModel by lazy {
        requireNotNull(this.activity) {
            VIEW_MODEL_IN_ACCESSIBLE_MESSAGE
        }
        ViewModelProvider(this.requireActivity(), viewModelFactory).get(
            LivePlayerSharedViewModel::class.java
        )
    }

    private val playerLifecycleActionListener = object : PlayerLifeCycleActionListener {
        override fun pausePlayer() {
            pauseVideoPlayer()
        }

        override fun startPlayer() {
            handleStartVideoPlayer()
        }

        override fun releasePlayer() {
            releaseVideoPlayer()
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_live_player, container, false)
        context?.let {
            mContext = it
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
        setupPlayerLifeCycleObserver()
        setupViewModelDataObserver()
        setUpClickListener()
        subscribeToChanges()
    }

    private fun subscribeToChanges() {
        //checking stream state!
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                delay(15000)
                withContext(Dispatchers.Main) {
                    handleErrorView()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        removeLifecycleObserver()
    }

    private fun setupViewModel() {
        viewModelFactory = LivePlayerSharedViewModel.Factory()
    }

    private fun setupViewModelDataObserver() {
        viewModel.playBack.observe(viewLifecycleOwner, Observer {
            it?.let { playBack ->
                setupPlayer(playBack)
                handleStartVideoPlayer()
            }
        })
    }

    private fun setupPlayer(playback: PlaybackInfoModel) {
        binding.player.preparePlayback(playback, playState)
    }

    private fun setupPlayerLifeCycleObserver() {
        livePlayerLifeCycleObserver = LivePlayerLifeCycleObserver(playerLifecycleActionListener)
        lifecycle.addObserver(livePlayerLifeCycleObserver)
    }

    private fun setUpClickListener() {
        binding.player.setOnClickListener {
            viewModel.updateShowController(true)
        }
    }

    private fun removeLifecycleObserver() {
        if (::livePlayerLifeCycleObserver.isInitialized) {
            lifecycle.removeObserver(livePlayerLifeCycleObserver)
        }
    }

    private fun handleStartVideoPlayer() {
        startVideoPlayer()
    }

    private fun pauseVideoPlayer() {
        binding.player.castPlayerHelper.pausePlayer()
        activity?.window?.keepScreenOn(false)
    }

    private fun startVideoPlayer() {
        binding.player.castPlayerHelper.startPlayer()
        activity?.window?.keepScreenOn()
    }

    private fun releaseVideoPlayer() {
        binding.player.castPlayerHelper.releasePlayer()
    }

    private val playState = object : PlayState {

        override fun buffering() {
            inBufferMode = true
            Log.d(TAG, "playState-> buffering: ")
        }

        override fun onPlaying() {
            inBufferMode = false
            handleErrorView()
            Log.d(TAG, "playState-> onPlaying: ")
        }

        override fun onIdle() {
            Log.d(TAG, "playState-> onIdle: ")
        }

        override fun onPlayerError(error: ExoPlaybackException) {
            inBufferMode = true
            handleErrorView(error.message)
            Log.d(TAG, "playState->onPlayerError: ")
        }

        override fun onEnded() {
            inBufferMode = false
            Log.d(TAG, "playState-> onEnded: ")
        }

    }

    private fun handleErrorView(errorMsg: String? = null) {
        with(binding) {
            pBar.gone()
            tvErrorMsg.apply {
                text = errorMsg ?: getString(R.string.video_stream_error)
                if (inBufferMode) show() else gone()
            }
        }
    }

}