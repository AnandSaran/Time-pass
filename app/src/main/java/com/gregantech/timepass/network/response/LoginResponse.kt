package com.gregantech.timepass.network.response

import android.os.Parcelable
import com.gregantech.timepass.network.jsonadapter.NullToEmptyString
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class LoginResponse(
    @NullToEmptyString
    val message: String,
    val new_user: Boolean? = false,
    @NullToEmptyString
    val status: String,
    val user: User
) : Parcelable