package com.gregantech.timepass.network.response

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class Category(
    val catID: String,
    val catImage: String,
    val catName: String
) : Parcelable