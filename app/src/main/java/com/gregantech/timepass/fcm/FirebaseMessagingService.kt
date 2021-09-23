package com.gregantech.timepass.fcm

import android.content.Intent
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.gregantech.timepass.BuildConfig
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

        remoteMessage.notification?.run {

            var navigationIntent: Intent? = null

            //{"message":"User in Live Streaming","user":{"streamID":"PtZgi5orKybxi0aOn4Ad","userID":"28"},"liveStatus":true,"status":"success"}
            with(remoteMessage) {

                val arr = data.values.toTypedArray()
                val fcmData = Gson().fromJson(arr[0], FCMDataModel::class.java)
                Log.d(TAG, "onMessageReceived: ${Gson().toJson(fcmData)}")
                if (SharedPreferenceHelper.getUserId() != fcmData.user?.userID) {
                    if (fcmData?.liveStatus == true) {
                        val playbackInfoModel = PlaybackInfoModel(
                            fcmData.message ?: "Streaming Live",
                            BuildConfig.ANT_URL + fcmData.user?.streamID,
                            fcmData.user?.streamID!!,
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
                        showNotification(title ?: "User is Live", body ?: "", navigationIntent!!)
                    } else {
                        navigationIntent =
                            Intent(applicationContext, SplashActivity::class.java).apply {
                                putExtra("fromFCM", true)
                            }
                        showNotification(title ?: "User is Live", body ?: "", navigationIntent!!)
                    }

                }
            }
        }

    }

    private fun showNotification(title: String, body: String, intent: Intent) {
        notificationUtil.showNotificationMessage(
            title,
            body,
            System.currentTimeMillis(),
            intent
        )
    }

}