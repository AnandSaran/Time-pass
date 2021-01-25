package com.gregantech.timepass.network.response.comments

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class Comment(
    val DateTime: String,
    val Id: String,
    val commentprofileImage: String,
    val comments: String,
    val commentuserID: String,
    val commentuserName: String
) : Parcelable