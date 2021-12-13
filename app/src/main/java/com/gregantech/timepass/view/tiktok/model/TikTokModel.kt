package com.gregantech.timepass.view.tiktok.model

data class TikTokModel(
    val title: String? = null,
    val sourceUrl: String? = null,
    val desc: String? = null,
    var isLiked: Boolean = false,
    val viewType: Int = 0
)
