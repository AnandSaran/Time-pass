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
import com.gregantech.timepass.databinding.ActivityBroadcastBinding
import com.gregantech.timepass.network.repository.FireStoreRepository
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
    private var docKey: String? = null

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
        setupOnClick()
        setupViewModel()
        if (savedInstanceState == null) {
            obtainDocKey()
        }
    }

    override fun onPause() {
        super.onPause()
        Log.d("LiveBroadCastActivity", "onPause: ")
        doUpdateCloseConnectionStatus()
    }

    private fun doUpdateCloseConnectionStatus() {
        Log.d("LiveBroadCastActivity", "doUpdateCloseConnectionStatus: ")
        chatViewModel.obUpdateBroadcastState(docKey!!, false)
            .observe(this, androidx.lifecycle.Observer {
                when (it.status) {
                    TimePassBaseResult.Status.ERROR -> it.message?.toast(this)
                }
            })
    }


    override fun onResume() {
        initWindow()
        super.onResume()
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

    override fun onBackPressed() {
        showExitAlert()
    }

    private fun setupViewModel() {
        chatViewModelFactory = LiveChatViewModel.Factory(FireStoreRepository())
    }

    private fun obtainDocKey() {
        chatViewModel.obCreateBroadcastDocument().observe(this, androidx.lifecycle.Observer {
            when (it.status) {
                TimePassBaseResult.Status.LOADING -> {
                }
                TimePassBaseResult.Status.SUCCESS -> {
                    Log.d("LiveBroadcastActivity", "obtainDocKey: DocId ${it.data.toString()}")
                    docKey = it.data.toString()
                    loadChatContainerFragment()
                    showBroadcastFragment()
                    subscribeToChanges()
                }
                TimePassBaseResult.Status.ERROR -> {
                    it.message?.toast(this)
                }
            }
        })
    }

    private fun subscribeToChanges() {
        chatViewModel.obReactionCount(docKey!!).observe(this, Observer {
            when (it.status) {
                TimePassBaseResult.Status.SUCCESS -> {
                    Log.d("LiveBroadCast", "listenToIncomingMsg: ${it?.data?.loves} ")
                    binding.liveOptions.tpItvLove.setLabel(it?.data?.loves)
                }
                TimePassBaseResult.Status.ERROR -> Log.e(
                    "LiveBroadCast",
                    "listenToIncomingMsg: ${it.message}"
                )
                TimePassBaseResult.Status.LOADING -> {
                }
            }
        })
    }

    private fun showBroadcastFragment() {
        if (docKey.isNullOrEmpty())
            return
        val broadcasterFragment = LiveBroadCasterFragment.newInstance(docKey = docKey)
        FragmentNavigationUtil.commitFragment(
            broadcasterFragment,
            supportFragmentManager,
            R.id.broadcastContainer
        )
        binding.tpvSBc.setChildVisible()
    }

    private fun loadChatContainerFragment() {
        if (docKey.isNullOrEmpty())
            return
        val chatFragment = LiveChatFragment.newInstance(mode = 1, docKey = docKey)
        FragmentNavigationUtil.commitFragment(
            chatFragment,
            supportFragmentManager,
            R.id.broadcastChatContainer
        )
    }
}