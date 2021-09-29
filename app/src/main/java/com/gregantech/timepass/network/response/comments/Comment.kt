package com.gregantech.timepass.network.response.comments

import android.os.Parcelable
import com.gregantech.timepass.network.jsonadapter.NullToEmptyString
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class Comment(
    val DateTime: String,
    @NullToEmptyString
    val Id: String,
    @NullToEmptyString
    val commentprofileImage: String,
    @NullToEmptyString
    val comments: String,
    @NullToEmptyString
    val commentuserID: String,
    @NullToEmptyString
    val commentuserName: String
) : Parcelable