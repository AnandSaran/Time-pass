package com.gregantech.timepass.view.live.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.gregantech.timepass.R
import com.gregantech.timepass.base.TimePassBaseResult
import com.gregantech.timepass.databinding.ActivityLiveVideoPlayerBinding
import com.gregantech.timepass.general.bundklekey.LivePlayerBundleKey
import com.gregantech.timepass.model.playback.PlaybackInfoModel
import com.gregantech.timepass.network.repository.BroadCastRepository
import com.gregantech.timepass.network.repository.FireStoreRepository
import com.gregantech.timepass.util.constant.VIEW_MODEL_IN_ACCESSIBLE_MESSAGE
import com.gregantech.timepass.util.extension.animGone
import com.gregantech.timepass.util.extension.animShow
import com.gregantech.timepass.util.extension.showSystemUI
import com.gregantech.timepass.util.extension.visible
import com.gregantech.timepass.util.navigation.FragmentNavigationUtil
import com.gregantech.timepass.view.live.fragment.LiveChatFragment
import com.gregantech.timepass.view.live.fragment.LivePlayerContentContainerFragment
import com.gregantech.timepass.view.live.viewmodel.LiveBroadcastViewModel
import com.gregantech.timepass.view.live.viewmodel.LiveChatViewModel
import com.gregantech.timepass.view.live.viewmodel.LivePlayerSharedViewModel

class LiveVideoPlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLiveVideoPlayerBinding
    private lateinit var viewModelFactory: LivePlayerSharedViewModel.Factory
    private lateinit var bcViewModelFactory: LiveBroadcastViewModel.Factory
    private lateinit var chatViewModelFactory: LiveChatViewModel.Factory

    private val viewModel: LivePlayerSharedViewModel by lazy {
        requireNotNull(this) {
            VIEW_MODEL_IN_ACCESSIBLE_MESSAGE
        }
        ViewModelProvider(this, viewModelFactory).get(
            LivePlayerSharedViewModel::class.java
        )
    }
    private val chatViewModel: LiveChatViewModel by lazy {
        requireNotNull(this) {
            VIEW_MODEL_IN_ACCESSIBLE_MESSAGE
        }
        ViewModelProvider(this, chatViewModelFactory).get(LiveChatViewModel::class.java)
    }

    private val bcViewModel: LiveBroadcastViewModel by lazy {
        requireNotNull(this) {
            VIEW_MODEL_IN_ACCESSIBLE_MESSAGE
        }
        ViewModelProvider(this, bcViewModelFactory).get(
            LiveBroadcastViewModel::class.java
        )
    }

    private var playBackInfoModel = PlaybackInfoModel()

    companion object {
        fun present(
            context: Context,
            playbackInfoModel: PlaybackInfoModel
        ) {
            val intent = Intent(context, LiveVideoPlayerActivity::class.java).apply {
                putExtra(
                    LivePlayerBundleKey.PLAYBACK_INFO_MODEL.value,
                    playbackInfoModel
                )
            }
            context.startActivity(intent)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Log.d("LiveVideoPlayer", "onNewIntent: ")
        intent?.let {
            getInputs(it)
            updatePlayBackInfo()
            loadChatContainerFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window?.showSystemUI(false)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_live_video_player)
        getInputs(intent)
        setupOnClick()
        setupViewModel()
        updatePlayBackInfo()
        updateTitle()
        subscribeToChanges()
        showView()
        if (savedInstanceState == null) {
            showLivePlayerContentContainerFragment()
            loadChatContainerFragment()
            bcViewModel.setupFetchLiveViewersJob(playBackInfoModel.chatKey)
        }

    }

    private fun showView() {
        binding.liveOptions.tpItvLive.visible(true)
        binding.liveOptions.tpItvUsers.visible(true)
        binding.liveOptions.tpItvLove.visible(true)
        binding.liveOptions.ivChat.visible(true)
    }

    private fun getInputs(intent: Intent) {
        if (intent.hasExtra(LivePlayerBundleKey.PLAYBACK_INFO_MODEL.value)) {
            playBackInfoModel =
                intent.getParcelableExtra(LivePlayerBundleKey.PLAYBACK_INFO_MODEL.value) as PlaybackInfoModel
        }
    }

    private fun setupViewModel() {
        viewModelFactory = LivePlayerSharedViewModel.Factory()
        chatViewModelFactory = LiveChatViewModel.Factory(FireStoreRepository())
        bcViewModelFactory = LiveBroadcastViewModel.Factory(BroadCastRepository())
    }

    private fun setupOnClick() {
        binding.liveOptions.ivClose.setOnClickListener {
            finish()
        }
        binding.liveOptions.ivChat.setOnClickListener {
            viewModel.toggleCommentState()
        }
    }

    private fun subscribeToChanges() {

        viewModel.obToggleCommentState.observe(this, androidx.lifecycle.Observer {
            binding.liveOptions.ivChat.setImageResource(if (it) R.drawable.ic_chat_active else R.drawable.ic_chat_inactive)
            binding.chatContainer.apply {
                if (it) animShow() else animGone()
            }
        })

        chatViewModel.obReactionCount(playBackInfoModel.chatKey).observe(this, Observer {
            when (it.status) {
                TimePassBaseResult.Status.SUCCESS -> {
                    Log.d("LiveVideoPlayer", "subscribeToChanges: ${it?.data?.loves} ")
                    if (it?.data?.broadcast_live == false) {
                        showLiveEndedAlert()
                    }
                    binding.liveOptions.tpItvLove.setLabel(it?.data?.loves)
                }
                TimePassBaseResult.Status.ERROR -> Log.e("LiveVideoPlayer", "subscribeToChanges: ")
                TimePassBaseResult.Status.LOADING -> {
                }
            }
        })
        bcViewModel.obLiveUserCount.observe(this, Observer {
            when (it.status) {
                TimePassBaseResult.Status.SUCCESS -> {
                    binding.liveOptions.tpItvUsers.setLabel(
                        it?.data?.totalRTMPWatchersCount?.toString() ?: "0"
                    )
                }
                TimePassBaseResult.Status.ERROR -> Log.e(
                    "LiveVideoPlayer",
                    "subscribeToChanges: error ${it.message}"
                )
                TimePassBaseResult.Status.LOADING -> {
                }
            }
        })
    }

    private fun showLiveEndedAlert() {
        AlertDialog.Builder(this)
            .setMessage(R.string.video_ended)
            .setCancelable(false)
            .setPositiveButton(android.R.string.yes) { dialogInterface, i ->
                finish()
                dialogInterface.dismiss()
            }
            .show()
    }


    private fun showLivePlayerContentContainerFragment() {
        val livePlayerContentContainerFragment = LivePlayerContentContainerFragment.newInstance()
        FragmentNavigationUtil.commitFragment(
            livePlayerContentContainerFragment,
            supportFragmentManager,
            R.id.playerContainer
        )
    }

    private fun loadChatContainerFragment() {
        val chatFragment = LiveChatFragment.newInstance(docKey = playBackInfoModel.chatKey)
        FragmentNavigationUtil.commitFragment(
            chatFragment,
            supportFragmentManager,
            R.id.chatContainer
        )
    }


    private fun updatePlayBackInfo() {
        viewModel.updatePlayBack(playBackInfoModel)
    }

    private fun updateTitle() {
        viewModel.updateTitle(playBackInfoModel.title)
    }
}