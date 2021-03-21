package com.gregantech.timepass.util

import com.gregantech.timepass.BuildConfig
import com.gregantech.timepass.model.AdvertisementResponse

const val CARD_HOME_VIDEO_LIST = "card_home_video_list"
const val CARD_ADMIN_VIDEO_LIST = "card_admin_video_list"
const val CARD_OTHER_USER_VIDEO_LIST = "card_other_user_video_list"
const val FULL_SCREEN_ADMIN_VIDEO_LIST = "full_screen_admin_video_list"
const val BANNER_ADMIN_VIDEO_CATEGORY = "banner_admin_video_category"
const val BANNER_USER_PROFILE = "banner_user_profile"
const val BANNER_OTHER_USER_PROFILE = "banner_other_user_profile"

object AdvertisementHandler {

    var advertisementResponse: AdvertisementResponse? = null

    fun isAdEnabled(adName: String): Boolean {
        advertisementResponse?.adds?.forEach {
            if (it?.name == adName && it.isVisible == true)
                return true
        }
        return false
    }

    fun getAdUnitByType(adName: String) = when (adName) {
        CARD_HOME_VIDEO_LIST -> BuildConfig.CARD_NATIVE
        CARD_ADMIN_VIDEO_LIST -> BuildConfig.CARD_NATIVE
        CARD_OTHER_USER_VIDEO_LIST -> BuildConfig.CARD_NATIVE
        FULL_SCREEN_ADMIN_VIDEO_LIST -> BuildConfig.FULL_SCREEN_ADMIN_VIDEO_LIST
        BANNER_ADMIN_VIDEO_CATEGORY -> BuildConfig.BANNER_ADMIN_VIDEO_CATEGORY
        BANNER_USER_PROFILE -> BuildConfig.BANNER_USER_PROFILE
        BANNER_OTHER_USER_PROFILE -> BuildConfig.BANNER_OTHER_USER_PROFILE
        else -> throw RuntimeException("Unknown adName $adName")
    }

}