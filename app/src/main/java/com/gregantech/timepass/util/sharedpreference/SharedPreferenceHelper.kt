package com.gregantech.timepass.util.sharedpreference

import android.content.Context
import android.content.SharedPreferences
import com.gregantech.timepass.network.response.User

object SharedPreferenceHelper {

    fun initSharedPreference(context: Context) {
        SharedPreferenceUtil.init(context)
    }

    fun getSharedPreference(): SharedPreferences {
        return SharedPreferenceUtil.getSharedPreference()
    }

    fun getStringValue(key: String): String {
        return SharedPreferenceUtil.getStringValue(key)
    }

    fun getIntValue(key: String): Int {
        return SharedPreferenceUtil.getIntValue(key)
    }

    fun getBooleanValue(key: String): Boolean {
        return SharedPreferenceUtil.getBooleanValue(key)
    }

    fun isUserLoggedIn(): Boolean {
        return SharedPreferenceUtil.getBooleanValue(SharedPreferenceKeyEnum.IS_USER_LOGGED_IN.value)
    }

    fun getUserId(): String {
        return SharedPreferenceUtil.getStringValue(SharedPreferenceKeyEnum.USER_ID.value)
    }

    fun clearAll() {
        setUserLoggedIn(false)
        SharedPreferenceUtil.clear()
    }

    fun setUserData(user: User) {
        setUserLoggedIn(true)
        setUserId(user.userID)
        setUserEmailId(user.emailID)
        setUserMobileNumber(user.mobileNo)
        setUserProfileImage(user.profileImage)
        setUserName(user.userName)
        setTotalPost(user.posts)
        setFollowers(user.followers)
        setFollowing(user.following)
        setBio(user.bio)
        setYouTubeProfileUrl(user.youtube)
    }

    private fun setUserId(value: String) {
        SharedPreferenceUtil.putStringValue(
            key = SharedPreferenceKeyEnum.USER_ID.value,
            value = value
        )
    }

    private fun setTotalPost(value: String) {
        SharedPreferenceUtil.putStringValue(
            key = SharedPreferenceKeyEnum.USER_TOTAL_POST.value,
            value = value
        )
    }

    fun getTotalPost(): String {
        return SharedPreferenceUtil.getStringValue(SharedPreferenceKeyEnum.USER_TOTAL_POST.value)
    }

    fun getFollowers(): String {
        return SharedPreferenceUtil.getStringValue(SharedPreferenceKeyEnum.USER_FOLLOWERS.value)
    }

    fun getFollowing(): String {
        return SharedPreferenceUtil.getStringValue(SharedPreferenceKeyEnum.USER_FOLLOWING.value)
    }

    fun getBio(): String {
        return SharedPreferenceUtil.getStringValue(SharedPreferenceKeyEnum.USER_BIO.value)
    }

    fun getYouTubeProfileUrl(): String {
        return SharedPreferenceUtil.getStringValue(SharedPreferenceKeyEnum.YOUTUBE_PROFILE_URL.value)
    }

    private fun setFollowers(value: String) {
        SharedPreferenceUtil.putStringValue(
            key = SharedPreferenceKeyEnum.USER_FOLLOWERS.value,
            value = value
        )
    }

    private fun setFollowing(value: String) {
        SharedPreferenceUtil.putStringValue(
            key = SharedPreferenceKeyEnum.USER_FOLLOWING.value,
            value = value
        )
    }

    private fun setBio(value: String) {
        SharedPreferenceUtil.putStringValue(
            key = SharedPreferenceKeyEnum.USER_BIO.value,
            value = value
        )
    }

    private fun setYouTubeProfileUrl(value: String) {
        SharedPreferenceUtil.putStringValue(
            key = SharedPreferenceKeyEnum.YOUTUBE_PROFILE_URL.value,
            value = value
        )
    }

    private fun setUserName(value: String) {
        SharedPreferenceUtil.putStringValue(
            key = SharedPreferenceKeyEnum.USER_NAME.value,
            value = value
        )
    }

    fun getUserName(): String {
        return SharedPreferenceUtil.getStringValue(SharedPreferenceKeyEnum.USER_NAME.value)
    }

    private fun setUserEmailId(value: String) {
        SharedPreferenceUtil.putStringValue(
            key = SharedPreferenceKeyEnum.USER_EMAIL_ID.value,
            value = value
        )
    }

    fun getUserEmailId(): String {
        return SharedPreferenceUtil.getStringValue(SharedPreferenceKeyEnum.USER_EMAIL_ID.value)
    }

    private fun setUserMobileNumber(value: String) {
        SharedPreferenceUtil.putStringValue(
            key = SharedPreferenceKeyEnum.USER_MOBILE_NO.value,
            value = value
        )
    }

    fun getUserMobileNumber(): String {
        return SharedPreferenceUtil.getStringValue(SharedPreferenceKeyEnum.USER_MOBILE_NO.value)
    }

    private fun setUserProfileImage(value: String) {
        SharedPreferenceUtil.putStringValue(
            key = SharedPreferenceKeyEnum.USER_PROFILE_IMAGE.value,
            value = value
        )
    }

    fun getUserProfileImage(): String {
        return SharedPreferenceUtil.getStringValue(SharedPreferenceKeyEnum.USER_PROFILE_IMAGE.value)
    }

    private fun setUserLoggedIn(value: Boolean) {
        SharedPreferenceUtil.putBooleanValue(
            key = SharedPreferenceKeyEnum.IS_USER_LOGGED_IN.value,
            value = value
        )
    }

    fun isPostUserAreSameUser(followerId: String): Boolean {
        return getUserId() == followerId
    }
}