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
import com.gregantech.timepass.network.repository.FireStoreRepository
import com.gregantech.timepass.util.constant.VIEW_MODEL_IN_ACCESSIBLE_MESSAGE
import com.gregantech.timepass.util.navigation.FragmentNavigationUtil
import com.gregantech.timepass.view.live.fragment.LiveChatFragment
import com.gregantech.timepass.view.live.fragment.LivePlayerContentContainerFragment
import com.gregantech.timepass.view.live.viewmodel.LiveChatViewModel
import com.gregantech.timepass.view.live.viewmodel.LivePlayerSharedViewModel

class LiveVideoPlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLiveVideoPlayerBinding
    private lateinit var viewModelFactory: LivePlayerSharedViewModel.Factory
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
    private val playBackInfoModel by lazy {
        if (intent.hasExtra(LivePlayerBundleKey.PLAYBACK_INFO_MODEL.value)) {
            intent.getParcelableExtra(LivePlayerBundleKey.PLAYBACK_INFO_MODEL.value) as PlaybackInfoModel
        } else {
            PlaybackInfoModel()
        }
    }

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_live_video_player)
        setupOnClick()
        setupViewModel()
        updatePlayBackInfo()
        updateTitle()
        subscribeToChanges()
        if (savedInstanceState == null) {
            showLivePlayerContentContainerFragment()
            loadChatContainerFragment()
        }

    }

    private fun setupViewModel() {
        viewModelFactory = LivePlayerSharedViewModel.Factory()
        chatViewModelFactory = LiveChatViewModel.Factory(FireStoreRepository())
    }

    private fun setupOnClick() {
        binding.liveOptions.ivClose.setOnClickListener {
            finish()
        }
    }

    private fun subscribeToChanges() {
        chatViewModel.obReactionCount(playBackInfoModel.chatKey).observe(this, Observer {
            when (it.status) {
                TimePassBaseResult.Status.SUCCESS -> {
                    Log.d("LiveVideoPlayer", "subscribeToChanges: ${it?.data?.loves} ")
                    if (it?.data?.broadcast_live == false) {
                        showLiveEndedAlert()
                    } else
                        binding.liveOptions.tpItvLove.setLabel(it?.data?.loves)
                }
                TimePassBaseResult.Status.ERROR -> Log.e("LiveVideoPlayer", "subscribeToChanges: ")
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
            .setNegativeButton(android.R.string.no) { dialogInterface, i ->
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