package com.gregantech.timepass.view.live.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.gregantech.timepass.R
import com.gregantech.timepass.databinding.ActivityLiveVideoPlayerBinding
import com.gregantech.timepass.util.constant.VIEW_MODEL_IN_ACCESSIBLE_MESSAGE
import com.gregantech.timepass.util.extension.showSystemUI
import com.gregantech.timepass.view.live.fragment.LivePlayerContentContainerFragment
import com.gregantech.timepass.view.live.viewmodel.LivePlayerSharedViewModel
import com.singtel.cast.utils.navigation.FragmentNavigationUtil

class LiveVideoPlayerActivity : AppCompatActivity() {

    private lateinit var drModePlayerBinding: ActivityLiveVideoPlayerBinding
    private lateinit var viewModelFactory: LivePlayerSharedViewModel.Factory

    private val viewModel: LivePlayerSharedViewModel by lazy {
        requireNotNull(this) {
            VIEW_MODEL_IN_ACCESSIBLE_MESSAGE
        }
        ViewModelProvider(this, viewModelFactory).get(
            LivePlayerSharedViewModel::class.java
        )
    }

    companion object {
        fun present(
            context: Context
        ) {
            val intent = Intent(context, LiveVideoPlayerActivity::class.java).apply {
            }
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window?.showSystemUI(false)
        drModePlayerBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_live_video_player)
        setupViewModel()
        updatePlayBackInfo()
        updateTitle()

        if (savedInstanceState == null) {
            showDrModePlayerContentContainerFragment()
        }

    }

    private fun showDrModePlayerContentContainerFragment() {
        val drModePlayerContentFragment = LivePlayerContentContainerFragment.newInstance()

        FragmentNavigationUtil.commitFragment(
            drModePlayerContentFragment,
            supportFragmentManager,
            R.id.playerContainer
        )
    }

    private fun setupViewModel() {
        viewModelFactory = LivePlayerSharedViewModel.Factory()
    }

    private fun updatePlayBackInfo() {
        // viewModel.updatePlayBack(playbackInfoModel)
    }

    private fun updateTitle() {
        // viewModel.updateTitle(title)
    }
}