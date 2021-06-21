package com.gregantech.timepass.view.live.activity

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.gregantech.timepass.R
import com.gregantech.timepass.base.TimePassBaseResult
import com.gregantech.timepass.databinding.ActivityBroadcastBinding
import com.gregantech.timepass.network.repository.BroadCastRepository
import com.gregantech.timepass.network.repository.FireStoreRepository
import com.gregantech.timepass.network.request.BroadCastRequest
import com.gregantech.timepass.util.constant.VIEW_MODEL_IN_ACCESSIBLE_MESSAGE
import com.gregantech.timepass.util.extension.keepScreenOn
import com.gregantech.timepass.util.extension.showSystemUI
import com.gregantech.timepass.util.extension.toast
import com.gregantech.timepass.util.navigation.FragmentNavigationUtil
import com.gregantech.timepass.util.sharedpreference.SharedPreferenceHelper
import com.gregantech.timepass.view.live.fragment.LiveBroadCasterFragment
import com.gregantech.timepass.view.live.fragment.LiveChatFragment
import com.gregantech.timepass.view.live.viewmodel.LiveBroadcastViewModel
import com.gregantech.timepass.view.live.viewmodel.LiveChatViewModel
import io.antmedia.android.broadcaster.LiveVideoBroadcaster

class LiveBroadCastActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBroadcastBinding
    private var docKey: String? = null

    private lateinit var viewModelFactory: LiveBroadcastViewModel.Factory
    private lateinit var chatViewModelFactory: LiveChatViewModel.Factory

    private val chatViewModel: LiveChatViewModel by lazy {
        requireNotNull(this) {
            VIEW_MODEL_IN_ACCESSIBLE_MESSAGE
        }
        ViewModelProvider(this, chatViewModelFactory).get(LiveChatViewModel::class.java)
    }

    private val viewModel: LiveBroadcastViewModel by lazy {
        requireNotNull(this) {
            VIEW_MODEL_IN_ACCESSIBLE_MESSAGE
        }
        ViewModelProvider(this, viewModelFactory).get(
            LiveBroadcastViewModel::class.java
        )
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
            checkAndObtainKey()
        }
    }

    private fun checkAndObtainKey() {

        val cameraPermissionGranted =
            (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED)

        val microPhonePermissionGranted =
            (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                    == PackageManager.PERMISSION_GRANTED)

        val permissionList = ArrayList<String?>()
        if (!cameraPermissionGranted)
            permissionList.add(Manifest.permission.CAMERA)

        if (!microPhonePermissionGranted)
            permissionList.add(Manifest.permission.RECORD_AUDIO)

        if (permissionList.size > 0) {
            when {
                ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.CAMERA
                ) -> showRationaleDialog(permissionList, Manifest.permission.CAMERA)
                ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.RECORD_AUDIO
                ) -> showRationaleDialog(permissionList, Manifest.permission.RECORD_AUDIO)
                else -> ActivityCompat.requestPermissions(
                    this,
                    permissionList.toTypedArray(),
                    LiveVideoBroadcaster.PERMISSIONS_REQUEST
                )
            }
        } else
            obtainDocKey()

    }

    override fun onPause() {
        super.onPause()
        Log.d("LiveBroadCastActivity", "onPause: ")
        updateBCStatus()
    }

    private fun updateBCStatus() {
        docKey?.let {
            chatViewModel.obUpdateBroadcastState(it, false)
            viewModel.updateBroadCastStatus(generateBCUpdateRequest(false))
        }
    }

    private fun generateBCUpdateRequest(state: Boolean) = BroadCastRequest(
        docKey!!,
        SharedPreferenceHelper.getUserId(),
        state
    )

    private fun showRationaleDialog(permissionList: ArrayList<String?>, permission: String) {
        android.app.AlertDialog.Builder(this)
            .setTitle(io.antmedia.android.R.string.permission)
            .setMessage(
                getString(
                    if (permission == Manifest.permission.CAMERA) io.antmedia.android.R.string.camera_permission_is_required
                    else io.antmedia.android.R.string.microphone_permission_is_required
                )
            )
            .setPositiveButton(
                android.R.string.yes
            ) { dialog, which ->
                dialog.dismiss()
                ActivityCompat.requestPermissions(
                    this,
                    permissionList.toTypedArray(),
                    LiveVideoBroadcaster.PERMISSIONS_REQUEST
                )
            }
            .show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LiveVideoBroadcaster.PERMISSIONS_REQUEST -> {
                if (isPermissionGranted()) {
                    obtainDocKey()
                } else {
                    val cam = ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        Manifest.permission.CAMERA
                    )
                    val audio = ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        Manifest.permission.RECORD_AUDIO
                    )
                    if (cam && audio)
                        checkAndObtainKey()
                    else
                        showPermissionDeniedDialog()
                }
                return
            }
        }
    }

    private fun showPermissionDeniedDialog() {
        android.app.AlertDialog.Builder(this)
            .setTitle(R.string.permission)
            .setMessage(getString(R.string.app_doesnot_work_without_permissions))
            .setPositiveButton(
                android.R.string.ok
            ) { dialog, which ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data =
                            Uri.parse("package:" + applicationContext.packageName)
                    }
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    val intent =
                        Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS)
                    startActivity(intent)
                }
                finish()
            }
            .setNegativeButton(
                getString(R.string.exit)
            ) { dialog, which ->
                dialog.dismiss()
                finish()
            }
            .show()
    }

    fun isPermissionGranted(): Boolean {
        val cameraPermissionGranted =
            (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED)
        val microPhonePermissionGranted =
            (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                    == PackageManager.PERMISSION_GRANTED)
        return cameraPermissionGranted && microPhonePermissionGranted
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
        viewModelFactory = LiveBroadcastViewModel.Factory(BroadCastRepository())
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
                    viewModel.updateBroadCastStatus(generateBCUpdateRequest(true))
                    viewModel.setupFetchLiveViewersJob(docKey!!)
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
        viewModel.obLiveUserCount.observe(this, Observer {
            when (it.status) {
                TimePassBaseResult.Status.SUCCESS -> {
                    binding.liveOptions.tpItvUsers.setLabel(
                        it?.data?.totalRTMPWatchersCount?.toString() ?: "0"
                    )
                }
                TimePassBaseResult.Status.ERROR -> Log.e(
                    "LiveBCActivity",
                    "subscribeToChanges: error ${it.message}"
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