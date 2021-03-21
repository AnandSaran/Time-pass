package com.gregantech.timepass.model

data class AdvertisementResponse(
    val adds: List<AddsItem?>? = null
) {
    data class AddsItem(
        val name: String? = null,
        var isVisible: Boolean? = null,
        val slno: String? = null
    )
}
