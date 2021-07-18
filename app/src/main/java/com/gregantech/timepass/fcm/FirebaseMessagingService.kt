package com.gregantech.timepass.fcm

import android.content.Intent
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.gregantech.timepass.general.bundklekey.LivePlayerBundleKey
import com.gregantech.timepass.model.playback.PlaybackInfoModel
import com.gregantech.timepass.util.sharedpreference.SharedPreferenceHelper
import com.gregantech.timepass.view.live.activity.LiveVideoPlayerActivity
import com.gregantech.timepass.view.splash.activity.SplashActivity

class FirebaseMessagingService : FirebaseMessagingService() {

    private val TAG = "FirebaseMessaging"

    private val notificationUtil: NotificationUtil by lazy {
        NotificationUtil(this)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "onNewToken: token $token")
        SharedPreferenceHelper.setFCMToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Log.d(TAG, "onMessageReceived: ")

        remoteMessage.notification?.run {

            var navigationIntent: Intent

            with(remoteMessage) {
                if (data["type"] == FCMBundleValue.LIVE.value) {
                    val playbackInfoModel = PlaybackInfoModel(
                        data[FCMBundleKey.TITLE.value] ?: "Streaming Live",
                        data[FCMBundleKey.STREAM_URL.value]!!,
                        data[FCMBundleKey.STREAM_ID.value]!!,
                        true
                    )
                    navigationIntent =
                        Intent(applicationContext, LiveVideoPlayerActivity::class.java).apply {
                            putExtra(FCMBundleKey.TYPE.value, FCMBundleValue.LIVE.value)
                            putExtra(
                                LivePlayerBundleKey.PLAYBACK_INFO_MODEL.value,
                                playbackInfoModel
                            )
                        }
                } else
                    navigationIntent =
                        Intent(applicationContext, SplashActivity::class.java).apply {
                            putExtra("fromFCM", true)
                        }
            }

            val title = title ?: "Message received"
            val message = body ?: "Tap for more details"

            notificationUtil.showNotificationMessage(
                title,
                message,
                System.currentTimeMillis(),
                navigationIntent
            )
        }

    }

}