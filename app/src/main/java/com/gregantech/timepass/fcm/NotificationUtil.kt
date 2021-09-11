package com.gregantech.timepass.fcm

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.messaging.FirebaseMessaging
import com.gregantech.timepass.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.*


class NotificationUtil(private val context: Context) {

    private val channelId = "com.adroit.timepass"
    private val channelName = "timepass"
    private val notificationManager: NotificationManager by lazy {
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    fun clearNotifications() {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
    }

    fun showNotificationMessage(title: String, message: String, timeStamp: Long, intent: Intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannels()
        }
        showNotificationMessage(title, message, timeStamp, intent, null)
    }


    fun enableFCM() {
        // Enable FCM via enable Auto-init service which generate new token and receive in FCMService
        FirebaseMessaging.getInstance().isAutoInitEnabled = true
    }

    fun disableFCM() {
        FirebaseMessaging.getInstance().isAutoInitEnabled = false
        Thread(Runnable {
            try {
                FirebaseInstallations.getInstance().delete()
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                enableFCM()
            }
        }).start()
    }

    fun showNotificationMessageWithImage(
        title: String,
        message: String,
        imageUrl: String,
        timeStamp: Long,
        intent: Intent
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannels()
        }
        showNotificationMessage(title, message, timeStamp, intent, imageUrl)
    }


    private fun isValidImageURL(imageUrl: String?): Boolean {

        imageUrl?.let {
            if (!TextUtils.isEmpty(it) &&
                it.length > 4 &&
                Patterns.WEB_URL.matcher(imageUrl).matches()
            ) {
                return true
            }
        }
        return false
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun createChannels() {

        // create android channel
        val androidChannel =
            NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
        // Sets whether notifications posted to this channel should display notification lights
        androidChannel.enableLights(true)
        // Sets whether notification posted to this channel should vibrate.
        androidChannel.enableVibration(true)
        // Sets the notification light color for notifications posted to this channel
        androidChannel.lightColor = Color.MAGENTA
        // Sets whether notifications posted to this channel appear on the lockscreen or not
        androidChannel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE

        notificationManager.createNotificationChannel(androidChannel)

        val notifyChannel = NotificationChannel(
            channelId,
            channelName, NotificationManager.IMPORTANCE_HIGH
        ).apply {
            enableLights(true)
            enableVibration(true)
            lightColor = Color.GREEN
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        }

        notificationManager.createNotificationChannel(notifyChannel)
    }


    private fun showNotificationMessage(
        title: String,
        message: String,
        timeStamp: Long,
        intent: Intent,
        imageUrl: String?
    ) {

        val uniqueInt = (System.currentTimeMillis() and 0xfffffff).toInt()
        val resultPendingIntent =
            PendingIntent.getActivity(context, uniqueInt, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val mBuilder = NotificationCompat.Builder(context, channelId)

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        //val defaultSoundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.packageName + "/" + R.raw.notify)

        when {
            imageUrl.isNullOrEmpty() -> showSmallNotification(
                mBuilder,
                title,
                message,
                timeStamp,
                resultPendingIntent,
                defaultSoundUri
            )
            else -> {
                if (isValidImageURL(imageUrl)) {
                    showBigNotification(
                        imageUrl,
                        mBuilder,
                        title,
                        message,
                        timeStamp,
                        resultPendingIntent,
                        defaultSoundUri
                    )
                }
            }
        }
    }


    private fun showSmallNotification(
        mBuilder: NotificationCompat.Builder,
        title: String,
        message: String,
        timeStamp: Long,
        resultPendingIntent: PendingIntent?,
        alarmSound: Uri?
    ) {

        val inboxStyle = NotificationCompat.InboxStyle()

        inboxStyle.addLine(message)

        val notification: Notification
        notification = mBuilder.setSmallIcon(R.drawable.logo_app_icon).setTicker(title).setWhen(0)
            .setAutoCancel(true)
            .setContentTitle(title)
            .setContentIntent(resultPendingIntent)
            .setSound(alarmSound)
            .setStyle(inboxStyle)
            .setWhen(timeStamp)
            .setSmallIcon(R.drawable.logo_app_icon)
            .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.logo_app_icon))
            .setContentText(message)
            .build()

        Log.d("FCM", "Normal Notification Triggered")

        notificationManager.notify(getRand(), notification)

    }

    private fun showBigNotification(
        imageUrl: String,
        mBuilder: NotificationCompat.Builder,
        title: String?,
        message: String?,
        timeStamp: Long,
        resultPendingIntent: PendingIntent?,
        alarmSound: Uri?
    ) {

        GlobalScope.launch {
            doFetch(imageUrl)?.let {
                val bigPictureStyle = NotificationCompat.BigPictureStyle()
                bigPictureStyle.setBigContentTitle(title)
                bigPictureStyle.setSummaryText(
                    message
                )
                bigPictureStyle.bigPicture(it)
                val notification =
                    mBuilder.setSmallIcon(R.drawable.logo_app_icon).setTicker(title).setWhen(0)
                        .setAutoCancel(true)
                        .setContentTitle(title)
                        .setContentIntent(resultPendingIntent)
                        .setSound(alarmSound)
                        .setStyle(bigPictureStyle)
                        .setWhen(timeStamp)
                        .setSmallIcon(R.drawable.logo_app_icon)
                        .setLargeIcon(
                            BitmapFactory.decodeResource(
                                context.resources,
                                R.drawable.logo_app_icon
                            )
                        )
                        .setContentText(message)
                        .build()
                Log.d("FCM", "Big Notification Triggered")
                notificationManager.notify(getRand(), notification)
            }
        }
    }

    private fun getRand(): Int = Random().nextInt(9999 - 1000) + 1000


    private suspend fun doFetch(imageURL: String): Bitmap? {
        var finalBitmap: Bitmap? = null
        withContext(Dispatchers.IO) {
            try {
                val url = URL(imageURL)
                val inputStream: InputStream
                val connection = url.openConnection() as HttpURLConnection
                connection.doInput = true
                connection.connect()
                inputStream = connection.inputStream
                finalBitmap = BitmapFactory.decodeStream(inputStream)
            } catch (ex: Exception) {
                Log.e("FCMUtil", "Error ${ex.localizedMessage}")
            }
        }
        return finalBitmap
    }

}