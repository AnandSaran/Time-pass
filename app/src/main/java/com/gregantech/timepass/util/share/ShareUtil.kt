package com.gregantech.timepass.util.share

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.gregantech.timepass.util.constant.ErrorMessage
import com.gregantech.timepass.util.extension.toast


object ShareUtil {
    fun shareText() {

    }

    fun openYoutube(context: Context, youtubeProfileUrl: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        try {
            intent.setPackage("com.google.android.youtube")
            intent.data = Uri.parse(youtubeProfileUrl)
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            try {
                intent.data = Uri.parse(youtubeProfileUrl)
                context.startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                ErrorMessage.INVALID_YOUTUBE_PROFILE.value.toast(context)
            }
        }
    }
}