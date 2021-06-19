package com.gregantech.timepass.view.live.fragment

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.content.res.Configuration
import android.hardware.Camera
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.gregantech.timepass.BuildConfig
import com.gregantech.timepass.R
import com.gregantech.timepass.base.TimePassBaseFragment
import com.gregantech.timepass.base.TimePassBaseResult
import com.gregantech.timepass.databinding.FragmentLiveBroadcasterBinding
import com.gregantech.timepass.firestore.FireStoreConst
import com.gregantech.timepass.network.repository.FireStoreRepository
import com.gregantech.timepass.util.constant.VIEW_MODEL_IN_ACCESSIBLE_MESSAGE
import com.gregantech.timepass.view.live.activity.LiveBroadCastActivity
import com.gregantech.timepass.view.live.viewmodel.LiveBroadcastViewModel
import com.gregantech.timepass.view.live.viewmodel.LiveChatViewModel
import io.antmedia.android.broadcaster.ILiveVideoBroadcaster
import io.antmedia.android.broadcaster.LiveVideoBroadcaster
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*


@Suppress("DEPRECATION")
class LiveBroadCasterFragment : TimePassBaseFragment() {

    private lateinit var binding: FragmentLiveBroadcasterBinding
    private var docKey: String? = null
    private var elapsedTime = 0L
    private val connectionLost = 2
    private val increaseTimer = 1
    private var isRecording = false
    private var isConnectionLost = false

    private var timer: Timer? = null
    private var timerHandler: TimerHandler? = null
    private var liveVideoBroadCaster: ILiveVideoBroadcaster? = null
    private var cameraResolutionDialog: CameraResolutionFragment? = null

    private lateinit var viewModelFactory: LiveBroadcastViewModel.Factory
    private lateinit var chatViewModelFactory: LiveChatViewModel.Factory

    private val chatViewModel: LiveChatViewModel by lazy {
        requireNotNull(this.activity) {
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

    private val liveVideoBroadcasterServiceIntent by lazy {
        Intent(requireContext(), LiveVideoBroadcaster::class.java)
    }

    companion object {
        fun newInstance(docKey: String? = null) =
            LiveBroadCasterFragment().apply {
                arguments = Bundle().apply {
                    putString(FireStoreConst.Keys.DOC_KEY, docKey)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        docKey = arguments?.getString(FireStoreConst.Keys.DOC_KEY)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (!::binding.isInitialized) {
            binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_live_broadcaster,
                container,
                false
            )
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initWindow()
        setAssets()
        initView()
        initClicks()
        subscribeToChanges()
    }

    private fun subscribeToChanges() {

        viewModel.obSwitchCamState.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            liveVideoBroadCaster?.changeCamera()
        })

        viewModel.obVoiceInputState.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            liveVideoBroadCaster?.setAudioEnable(it)
            binding.includedBottomOptions.ivBroadMic.apply {
                background =
                    ContextCompat.getDrawable(
                        requireContext(),
                        if (it == true) R.drawable.bg_mic_active else R.drawable.bg_mic_inactive
                    )
                setImageResource(if (it == true) R.drawable.ic_mic_on else R.drawable.ic_mic_off)
            }
        })
    }

    private fun initWindow() {

    }

    private fun setAssets() {
        viewModelFactory = LiveBroadcastViewModel.Factory()
        chatViewModelFactory = LiveChatViewModel.Factory(FireStoreRepository())
        timerHandler = TimerHandler()
        requireContext().startService(liveVideoBroadcasterServiceIntent)
    }

    private fun initView() {
        binding.includedCamLoader.tvLoadingMsg.text = getString(R.string.loading)
        binding.cameraPreviewSurfaceView.setEGLContextClientVersion(2)
    }

    private fun initClicks() {
        with(binding.includedBottomOptions) {
            ivBroadCam.setOnClickListener(onClick)
            ivBroadMic.setOnClickListener(onClick)
        }
    }

    private val onClick = View.OnClickListener {
        when (it.id) {
            R.id.ivBroadCam -> viewModel.changeCam()
            R.id.ivBroadMic -> viewModel.changeVoice()
        }
    }

    override fun onPause() {
        super.onPause()
        cameraResolutionDialog?.dismiss()
        liveVideoBroadCaster?.pause()
    }

    private fun doUpdateCloseConnectionStatus() {
        chatViewModel.obUpdateBroadcastState(docKey!!, false)
            .observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                when (it.status) {
                    TimePassBaseResult.Status.ERROR -> showSnackError(it.message!!)
                }
            })
    }

    override fun onStart() {
        super.onStart()
        requireContext().bindService(liveVideoBroadcasterServiceIntent, serviceConnection, 0)
    }

    override fun onStop() {
        super.onStop()
        requireContext().unbindService(serviceConnection)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE || newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            liveVideoBroadCaster?.setDisplayOrientation()
        }
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName?, service: IBinder?) {
            val binder = service as LiveVideoBroadcaster.LocalBinder
            if (liveVideoBroadCaster == null) {
                liveVideoBroadCaster = binder.service
                liveVideoBroadCaster?.run {
                    init(
                        requireContext() as LiveBroadCastActivity,
                        binding.cameraPreviewSurfaceView
                    )
                    setAdaptiveStreaming(true)
                    liveVideoBroadCaster?.openCamera(Camera.CameraInfo.CAMERA_FACING_FRONT)
                    setCameraCallback(cameraCallback)
                }
            }
        }

        override fun onServiceDisconnected(className: ComponentName?) {
            liveVideoBroadCaster = null
        }

    }

    fun onBackPressed() {
        stopBroadcasting()
    }

    private val cameraCallback = LiveVideoBroadcaster.CameraCallback {
        binding.tpvS.setChildVisible()
        startBroadcasting()
    }



    private fun startBroadcasting() {
        if (liveVideoBroadCaster != null && liveVideoBroadCaster?.isConnected == false) {
            val url = BuildConfig.ANT_URL + docKey
            Log.d(TAG, "toggleBroadcasting: url $url")
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    val resAwait = async { liveVideoBroadCaster?.startBroadcasting(url) }
                    val res = resAwait.await()
                    Log.d(TAG, "startBroadcasting: status $res")
                    val value = res as Boolean

                    withContext(Dispatchers.Main) {
                        isRecording = value
                        if (value) {
                            startTimer()
                        } else {
                            showSnackError(getString(R.string.stream_not_started))
                            stopBroadcasting()
                        }
                    }
                }
            }
        } else
            showSnackError("liveVideoBroadCaster is null")
    }

    private fun stopBroadcasting() {
        if (isRecording) {

            stopTimer()
            liveVideoBroadCaster?.stopBroadcasting()
        }
        isRecording = false
    }

    private fun switchToRecordingMode() {
        startTimer()
    }

    private fun switchToIdleMode() {
        stopBroadcasting()
        stopTimer()
        liveVideoBroadCaster?.stopBroadcasting()
    }

    private fun startTimer() {

        if (timer == null)
            timer = Timer()

        elapsedTime = 0
        timer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                elapsedTime += 1 //increase every sec
                timerHandler?.obtainMessage(increaseTimer)?.sendToTarget()
                if (liveVideoBroadCaster == null || liveVideoBroadCaster?.isConnected == false) {
                    timerHandler?.obtainMessage(connectionLost)?.sendToTarget()
                }
            }
        }, 0, 1000)
    }

    private fun stopTimer() {
        timer?.cancel()
        timer = null
        elapsedTime = 0
    }

    private fun showSnackError(msg: String) {
        Snackbar.make(
            binding.rootLayout,
            msg,
            Snackbar.LENGTH_LONG
        ).show()
    }

    @SuppressLint("HandlerLeak")
    inner class TimerHandler : Handler() {

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (isAdded) {
                when (msg.what) {
                    increaseTimer -> {
                        /*   binding.tvStreamLiveStatus.text = String.format(
                               "%s - %s", getString(R.string.live_indicator), getDurationString(
                                   elapsedTime
                               )
                           )*/
                    }
                    connectionLost -> {
                        isConnectionLost = true
                        showAlert()
                        stopBroadcasting()
                    }
                }
            }
        }
    }

    private fun showAlert() {
        if (isAdded)
            AlertDialog.Builder(requireContext())
                .setMessage(R.string.broadcast_connection_lost)
                .setPositiveButton(android.R.string.ok) { dialog, which ->
                    requireActivity().finish()
                    dialog.dismiss()
                }
                .show()
    }


}



