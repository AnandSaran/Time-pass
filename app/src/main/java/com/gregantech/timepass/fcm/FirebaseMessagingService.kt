package com.gregantech.timepass.fcm

import android.content.Intent
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.gregantech.timepass.BuildConfig
import com.gregantech.timepass.general.bundklekey.LivePlayerBundleKey
import com.gregantech.timepass.general.bundklekey.UserProfileActivityBundleKeyEnum
import com.gregantech.timepass.model.playback.PlaybackInfoModel
import com.gregantech.timepass.util.sharedpreference.SharedPreferenceHelper
import com.gregantech.timepass.view.live.activity.LiveVideoPlayerActivity
import com.gregantech.timepass.view.profile.activity.UserProfileActivity
import com.gregantech.timepass.view.splash.activity.SplashActivity
import org.json.JSONObject

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

            with(remoteMessage) {

                val arr = data.values.toTypedArray()
                val firstIndexValue = arr[0]
                val jsObj = JSONObject(firstIndexValue)

                if (jsObj.opt("video") != null) {
                    // post notification
                    val videoJsonArr = jsObj.getJSONArray("video")
                    val videoObj = videoJsonArr[0]
                    if (videoObj.toString().isEmpty()) {
                        return
                    }
                    val videoData =
                        Gson().fromJson(videoObj.toString(), FCMPostResponse::class.java)
                    Log.d(TAG, "id ${videoData.Id} followerId ${videoData.followerId}")

                    videoData.followerId?.let {
                        if (!isCurrentUser(it)) { // not current user
                            val intent = Intent(applicationContext, UserProfileActivity::class.java)
                            intent.putExtra(UserProfileActivityBundleKeyEnum.FOLLOWER_ID.value, it)
                            showNotification(title ?: "", body ?: "", intent)
                        }
                    }

                } else {
                    val fcmData = Gson().fromJson(firstIndexValue, FCMDataModel::class.java)
                    fcmData.user?.userID?.let {
                        if (!isCurrentUser(it)) { // not current user
                            if (fcmData?.liveStatus == true) {
                                //live notification
                                val playbackInfoModel = PlaybackInfoModel(
                                    fcmData.message ?: "Streaming Live",
                                    BuildConfig.ANT_URL + fcmData.user.streamID,
                                    fcmData.user.streamID!!,
                                    true
                                )
                                val navigationIntent =
                                    Intent(
                                        applicationContext,
                                        LiveVideoPlayerActivity::class.java
                                    ).apply {
                                        putExtra(FCMBundleKey.TYPE.value, FCMBundleValue.LIVE.value)
                                        putExtra(
                                            LivePlayerBundleKey.PLAYBACK_INFO_MODEL.value,
                                            playbackInfoModel
                                        )
                                    }
                                showNotification(
                                    title ?: "User is Live", body ?: "",
                                    navigationIntent
                                )
                            } else triggerSplashNotification(title, body)
                        }
                    } ?: triggerSplashNotification(title, body)
                }
            }
        }

    }

    private fun triggerSplashNotification(title: String?, body: String?) {
        val navigationIntent =
            Intent(applicationContext, SplashActivity::class.java).apply {
                putExtra("fromFCM", true)
            }
        showNotification(title ?: "User is Live", body ?: "", navigationIntent)
    }

    private fun isCurrentUser(id: String): Boolean {
        return SharedPreferenceHelper.getUserId() == id
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