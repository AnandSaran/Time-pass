package com.gregantech.timepass.util

import com.gregantech.timepass.BuildConfig
import com.gregantech.timepass.model.AdvertisementResponse

const val CARD_HOME_VIDEO_LIST = 1
const val CARD_ADMIN_VIDEO_LIST = 2
const val CARD_OTHER_USER_VIDEO_LIST = 3
const val FULL_SCREEN_ADMIN_VIDEO_LIST = 4
const val BANNER_ADMIN_VIDEO_CATEGORY = 5
const val BANNER_USER_PROFILE = 6
const val BANNER_OTHER_USER_PROFILE = 7

object AdvertisementHandler {

    var advertisementResponse: AdvertisementResponse? = null

    fun isAdEnabled(ad_id: String): Boolean {
        advertisementResponse?.adds?.forEach {
            if (it?.slno == ad_id && it.isVisible == true)
                return true
        }
        return false
    }

    fun getAdUnitByType(adType: Int) = when (adType) {
        CARD_HOME_VIDEO_LIST -> BuildConfig.CARD_NATIVE
        CARD_ADMIN_VIDEO_LIST -> BuildConfig.CARD_NATIVE
        CARD_OTHER_USER_VIDEO_LIST -> BuildConfig.CARD_NATIVE
        FULL_SCREEN_ADMIN_VIDEO_LIST -> BuildConfig.FULL_SCREEN_ADMIN_VIDEO_LIST
        BANNER_ADMIN_VIDEO_CATEGORY -> BuildConfig.BANNER_ADMIN_VIDEO_CATEGORY
        BANNER_USER_PROFILE -> BuildConfig.BANNER_USER_PROFILE
        BANNER_OTHER_USER_PROFILE -> BuildConfig.BANNER_OTHER_USER_PROFILE
        else -> throw RuntimeException("Unknown ad type $adType")
    }

}