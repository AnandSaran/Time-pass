package com.gregantech.timepass.model.playback

import android.os.Parcelable
import com.gregantech.timepass.util.constant.EMPTY_STRING
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PlaybackInfoModel(
    val title: String = EMPTY_STRING,
    val url: String = EMPTY_STRING,
    val chatKey: String = EMPTY_STRING,
    val isLive: Boolean = true
) : Parcelable