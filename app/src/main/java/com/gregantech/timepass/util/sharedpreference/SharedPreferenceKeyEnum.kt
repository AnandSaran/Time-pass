package com.gregantech.timepass.util.sharedpreference

enum class SharedPreferenceKeyEnum(val value: String) {
    IS_USER_LOGGED_IN("is_user_logged_in"),
    USER_ID("userID"),
    USER_NAME("userName"),
    USER_EMAIL_ID("emailID"),
    USER_MOBILE_NO("mobileNo"),
    USER_PROFILE_IMAGE("profileImage"),
    USER_TOTAL_POST("totalPost"),
    USER_FOLLOWERS("followers"),
    USER_FOLLOWING("Following"),
    USER_BIO("bio"),
    YOUTUBE_PROFILE_URL("youtube_profile_url"),
}