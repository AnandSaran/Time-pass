package com.gregantech.timepass.view.live.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.*
import android.content.res.Configuration
import android.hardware.Camera
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.ContentLoadingProgressBar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.gregantech.timepass.BuildConfig
import com.gregantech.timepass.R
import com.gregantech.timepass.databinding.ActivityLiveVideoBroadCastBinding
import com.gregantech.timepass.util.extension.gone
import com.gregantech.timepass.util.extension.keepScreenOn
import com.gregantech.timepass.util.extension.show
import com.gregantech.timepass.util.extension.showSystemUI
import com.gregantech.timepass.view.live.fragment.CameraResolutionFragment
import io.antmedia.android.broadcaster.ILiveVideoBroadcaster
import io.antmedia.android.broadcaster.LiveVideoBroadcaster
import io.antmedia.android.broadcaster.utils.Resolution
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.CookieHandler
import java.net.CookieManager
import java.net.CookiePolicy
import java.util.*

class LiveVideoBroadCastActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLiveVideoBroadCastBinding
    private val liveVideoBroadcasterServiceIntent by lazy {
        Intent(this, LiveVideoBroadcaster::class.java)
    }
    private var timer: Timer? = null
    private var timerHandler :TimerHandler?=null
    private var elapsedTime = 0L
    private var liveVideoBroadCaster: ILiveVideoBroadcaster? = null
    private val connectionLost = 2
    private val increaseTimer = 1
    private var cameraResolutionDialog: CameraResolutionFragment? = null
    private var isMuted = false
    private var isRecording = false
    private var DEFAULT_COOKIE_MANAGER : CookieManager?=null

    companion object {
        fun present(context: Context) {
            val intent = Intent(context, LiveVideoBroadCastActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DEFAULT_COOKIE_MANAGER = CookieManager()
        DEFAULT_COOKIE_MANAGER?.setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER)
        if (CookieHandler.getDefault() !== DEFAULT_COOKIE_MANAGER) {
            CookieHandler.setDefault(DEFAULT_COOKIE_MANAGER)
        }
        initWindow()
        setAssets()
        initBinding()
        initView()
        initClicks()
    }

    private fun initWindow() {
        window?.apply {
            showSystemUI(false)
            keepScreenOn()
        }
    }

    private fun setAssets() {
        timerHandler = TimerHandler()
        startService(liveVideoBroadcasterServiceIntent)
    }

    private fun initClicks() {
        with(binding){
            arrayOf(
                settingsButton,
                changeCameraButton,
                micMuteButton,
                toggleBroadcasting
            ).forEach {
                it.setOnClickListener(onClick)
            }
        }
    }

    private fun initBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_live_video_broad_cast)
    }

    private fun initView() {
        binding.cameraPreviewSurfaceView.setEGLContextClientVersion(2)
    }

    private fun changeCamera() {
        liveVideoBroadCaster?.changeCamera()
    }

    override fun onStart() {
        super.onStart()
        bindService(liveVideoBroadcasterServiceIntent, serviceConnection, 0)
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName?, service: IBinder?) {
            val binder = service as LiveVideoBroadcaster.LocalBinder
            if (liveVideoBroadCaster == null) {
                liveVideoBroadCaster = binder.service
                liveVideoBroadCaster?.run {
                    init(this@LiveVideoBroadCastActivity, binding.cameraPreviewSurfaceView)
                    setAdaptiveStreaming(true)
                }
            }
            liveVideoBroadCaster?.openCamera(Camera.CameraInfo.CAMERA_FACING_FRONT)
        }

        override fun onServiceDisconnected(className: ComponentName?) {
            liveVideoBroadCaster = null
        }

    }

    private val onClick = View.OnClickListener{
        when(it){
            binding.settingsButton -> showSetResolutionDialog()
            binding.changeCameraButton -> changeCamera()
            binding.micMuteButton -> toggleMute()
            binding.toggleBroadcasting -> toggleBroadcasting()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            LiveVideoBroadcaster.PERMISSIONS_REQUEST -> {
                if (liveVideoBroadCaster?.isPermissionGranted == true) {
                    liveVideoBroadCaster?.openCamera(Camera.CameraInfo.CAMERA_FACING_BACK)
                } else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                            this,
                            Manifest.permission.CAMERA
                        ) ||
                        ActivityCompat.shouldShowRequestPermissionRationale(
                            this,
                            Manifest.permission.RECORD_AUDIO
                        )
                    ) {
                        liveVideoBroadCaster?.requestPermission()
                    } else showPermissionDeniedDialog()
                }
                return
            }
        }
    }

    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(this@LiveVideoBroadCastActivity)
            .setTitle(R.string.permission)
            .setMessage(getString(R.string.app_doesnot_work_without_permissions))
            .setPositiveButton(
                android.R.string.yes
            ) { dialog, which ->
                try {
                    //Open the specific App Info page:
                    val intent =
                        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    intent.data =
                        Uri.parse("package:" + applicationContext.packageName)
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    val intent =
                        Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS)
                    startActivity(intent)
                }
            }
            .show()
    }

    override fun onPause() {
        super.onPause()
        cameraResolutionDialog?.dismiss()
        liveVideoBroadCaster?.pause()
    }

    override fun onStop() {
        super.onStop()
        unbindService(serviceConnection)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE || newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            liveVideoBroadCaster?.setDisplayOrientation()
        }
    }


    private fun showSetResolutionDialog() {
        val ft = supportFragmentManager.beginTransaction()
        val fragDialog = supportFragmentManager.findFragmentByTag("dialog")
        fragDialog?.let {
            ft.remove(it)
        }
        val sizeList = liveVideoBroadCaster?.previewSizeList
        if (sizeList?.isNotEmpty() == true) {
            cameraResolutionDialog = CameraResolutionFragment()
            liveVideoBroadCaster?.previewSize?.let {
                cameraResolutionDialog?.setCameraResolutions(
                    sizeList,
                    it
                )
            }
            cameraResolutionDialog?.show(ft, "resolution_dialog")
        }else
            Snackbar.make(binding.rootLayout, "No resolution available", Snackbar.LENGTH_LONG).show()

    }

    private fun toggleBroadcasting() {
        if(!isRecording){
            if(liveVideoBroadCaster  != null && liveVideoBroadCaster?.isConnected == false){
                val url = BuildConfig.ANT_URL+binding.streamNameEditText.text.toString()
                val contentProgressBar = ContentLoadingProgressBar(this@LiveVideoBroadCastActivity)
                contentProgressBar.show()
                lifecycleScope.launch {
                    withContext(Dispatchers.IO){

                        val resAwait = async { liveVideoBroadCaster?.startBroadcasting(url) }
                        val res = resAwait.await()
                        Log.d("LiveVideoBroadcast", "toggleBroadcasting: res $res")
                        val value = res as Boolean

                        withContext(Dispatchers.Main){
                            contentProgressBar.hide()
                            isRecording = value
                            if(value){
                                with(binding){
                                    streamLiveStatus.show()
                                    toggleBroadcasting.setText(R.string.stop_broadcasting)
                                    settingsButton.gone()
                                }
                                startTimer()
                            }else{
                                Snackbar.make(
                                    binding.rootLayout,
                                    R.string.stream_not_started,
                                    Snackbar.LENGTH_LONG
                                ).show()
                                triggerStopRecording()
                            }
                        }
                    }
                }
            }else
                Snackbar.make(
                    binding.rootLayout,
                    "liveVideoBroadCaster is null",
                    Snackbar.LENGTH_LONG
                ).show()
        }else
            triggerStopRecording()
    }

    private fun toggleMute() {
        isMuted = !isMuted
        liveVideoBroadCaster?.setAudioEnable(!isMuted)
        binding.micMuteButton.setImageDrawable(
            ContextCompat
                .getDrawable(
                    this,
                    if (isMuted) R.drawable.ic_mic_mute_off_24 else R.drawable.ic_mic_mute_on_24
                )
        )
    }

    private fun triggerStopRecording(){
        if(isRecording){
            binding.toggleBroadcasting.text = getString(R.string.start_broadcasting)
            binding.streamLiveStatus.apply {
                gone()
                text = getString(R.string.live_indicator)
            }
            binding.settingsButton.show()
            stopTimer()
            liveVideoBroadCaster?.stopBroadcasting()
        }
        isRecording = false
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

    fun setResolution(size: Resolution) {
        cameraResolutionDialog?.dismiss()
        liveVideoBroadCaster?.setResolution(size)
    }


    @SuppressLint("HandlerLeak")
    inner class TimerHandler : Handler() {

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                increaseTimer -> binding.streamLiveStatus.setText(
                    String.format(
                        "%s - %s", getString(R.string.live_indicator), getDurationString(
                            elapsedTime
                        )
                    )
                )
                connectionLost -> {
                    triggerStopRecording()
                    AlertDialog.Builder(this@LiveVideoBroadCastActivity)
                        .setMessage(R.string.broadcast_connection_lost)
                        .setPositiveButton(android.R.string.yes, null)
                        .show()
                }
            }
        }
    }

    private fun getDurationString(orgSeconds: Long): String {
        var seconds = orgSeconds
        if (seconds < 0 || seconds > 2000000) //there is an codec problem and duration is not set correctly,so display meaningfull string
            seconds = 0
        val hours = seconds / 3600
        val minutes = seconds % 3600 / 60
        seconds %= 60
        return if (hours == 0L) twoDigitString(minutes) + " : " + twoDigitString(seconds) else twoDigitString(
            hours
        ) + " : " + twoDigitString(minutes) + " : " + twoDigitString(seconds)
    }

    private fun twoDigitString(number: Long): String {
        if (number == 0L) {
            return "00"
        }
        return if (number / 10L == 0L) {
            "0$number"
        } else number.toString()
    }

}