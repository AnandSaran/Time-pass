package com.gregantech.timepass.network.request

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SearchVideosRequest(
    val userID: String,
    val searchKey: String
) : Parcelable