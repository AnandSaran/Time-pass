package com.gregantech.timepass.view.live.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.gregantech.timepass.R
import com.gregantech.timepass.base.TimePassBaseResult
import com.gregantech.timepass.databinding.ActivityBroadcastBinding
import com.gregantech.timepass.network.repository.LiveChatRepository
import com.gregantech.timepass.util.constant.VIEW_MODEL_IN_ACCESSIBLE_MESSAGE
import com.gregantech.timepass.util.extension.keepScreenOn
import com.gregantech.timepass.util.extension.showSystemUI
import com.gregantech.timepass.util.extension.toast
import com.gregantech.timepass.util.navigation.FragmentNavigationUtil
import com.gregantech.timepass.view.live.fragment.LiveBroadCasterFragment
import com.gregantech.timepass.view.live.fragment.LiveChatFragment
import com.gregantech.timepass.view.live.viewmodel.LiveChatViewModel

class LiveBroadCastActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBroadcastBinding

    private lateinit var chatViewModelFactory: LiveChatViewModel.Factory
    private val chatViewModel: LiveChatViewModel by lazy {
        requireNotNull(this) {
            VIEW_MODEL_IN_ACCESSIBLE_MESSAGE
        }
        ViewModelProvider(this, chatViewModelFactory).get(LiveChatViewModel::class.java)
    }

    companion object {
        fun present(
            context: Context
        ) {
            val intent = Intent(context, LiveBroadCastActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_broadcast)
        initWindow()
        setupOnClick()
        setupViewModel()
        if (savedInstanceState == null) {
            obtainDocKey()
        }
    }

    private fun initWindow() {
        window?.apply {
            showSystemUI(false)
            keepScreenOn()
        }
    }

    private fun setupOnClick() {
        binding.liveOptions.ivClose.setOnClickListener {
            showExitAlert()
        }
    }

    private fun showExitAlert() {
        AlertDialog.Builder(this)
            .setMessage(R.string.close_live_msg)
            .setPositiveButton(android.R.string.yes) { dialogInterface, i ->
                (supportFragmentManager.findFragmentById(R.id.broadcastContainer) as LiveBroadCasterFragment).onBackPressed()
                finish()
                dialogInterface.dismiss()
            }
            .setNegativeButton(android.R.string.no) { dialogInterface, i ->
                dialogInterface.dismiss()
            }
            .show()
    }

    private fun setupViewModel() {
        chatViewModelFactory = LiveChatViewModel.Factory(LiveChatRepository())
    }

    private fun obtainDocKey() {
        chatViewModel.obCreateBroadcastDocument().observe(this, androidx.lifecycle.Observer {
            when (it.status) {
                TimePassBaseResult.Status.LOADING -> {
                }
                TimePassBaseResult.Status.SUCCESS -> {
                    Log.d("LiveBroadcastActivity", "obtainDocKey: DocId ${it.data.toString()}")
                    loadChatContainerFragment(it.data)
                    showBroadcastFragment(it.data)
                }
                TimePassBaseResult.Status.ERROR -> {
                    it.message?.toast(this)
                }
            }
        })
    }

    private fun showBroadcastFragment(docKey: String?) {
        val broadcasterFragment = LiveBroadCasterFragment.newInstance(docKey = docKey)
        FragmentNavigationUtil.commitFragment(
            broadcasterFragment,
            supportFragmentManager,
            R.id.broadcastContainer
        )
        binding.tpvSBc.setChildVisible()
    }

    private fun loadChatContainerFragment(docKey: String?) {
        val chatFragment = LiveChatFragment.newInstance(mode = 1, docKey = docKey)
        FragmentNavigationUtil.commitFragment(
            chatFragment,
            supportFragmentManager,
            R.id.broadcastChatContainer
        )
    }
}