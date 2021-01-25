package com.gregantech.timepass.network.request

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class LoginRequest(
    val mobileNo: String,
    val fcm_token: String = ""
) : Parcelable