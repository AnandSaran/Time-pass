package com.gregantech.timepass.util.sharedpreference

import android.content.Context
import android.content.SharedPreferences
import com.gregantech.timepass.R
import com.gregantech.timepass.util.constant.SharedPreferenceKey.FCM_TOKEN

class SharedPref {
    private lateinit var preference: SharedPreferences

    /**
     * Singleton object for the shared preference.
     *
     * @param context Context of current state of the application/object
     * @return SharedPreferences object is returned.
     */
    private fun getPreferenceInstance(context: Context): SharedPreferences? {
        return if (::preference.isInitialized) {
            preference
        } else {
            //TODO: Shared Preference name has to be set....
            preference = context.getSharedPreferences(
                context.getString(R.string.app_name),
                Context.MODE_PRIVATE
            )
            preference
        }
    }

    /**
     * Set the String value in the shared preference W.R.T the given key.
     *
     * @param context Context of current state of the application/object
     * @param key     String used as a key for accessing the value.
     * @param value   String value which is to be stored in shared preference.
     */
    fun setSharedValue(
        context: Context,
        key: String?,
        value: String?
    ) {
        getPreferenceInstance(context)
        val editor = preference.edit()
        /* if (value.equals("null")){
            value=null;
        }*/editor.putString(key, value)
        editor.apply()
    }

    /**
     * Set the Integer value in the shared preference W.R.T the given key.
     *
     * @param context Context of current state of the application/object
     * @param key     String used as a key for accessing the value.
     * @param value   Integer value which is to be stored in shared preference.
     */
    fun setSharedValue(
        context: Context,
        key: String?,
        value: Int
    ) {
        getPreferenceInstance(context)
        val editor = preference.edit()
        editor.putInt(key, value)
        editor.apply()
    }

    /**
     * Set the boolean value in the shared preference W.R.T the given key.
     *
     * @param context Context of current state of the application/object
     * @param key     String used as a key for accessing the value.
     * @param value   Boolean value which is to be stored in shared preference.
     */
    fun setSharedValue(
        context: Context,
        key: String?,
        value: Boolean
    ) {
        getPreferenceInstance(context)
        val editor = preference.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun setSharedValue(
        context: Context,
        key: String?,
        value: Long
    ) {
        getPreferenceInstance(context)
        val editor = preference.edit()
        editor.putLong(key, value)
        editor.apply()
    }

    /**
     * Returns Boolean value for the given key.
     * By default it will return "false".
     *
     * @param context Context of current state of the application/object
     * @param key     String used as a key for accessing the value.
     * @return false by default; returns the Boolean value for the given key.
     */
    fun getBooleanValue(context: Context, key: String?): Boolean {
        return getPreferenceInstance(context)!!.getBoolean(key, false)
    }

    /**
     * Returns Integer value for the given key.
     * By default it will return "-1".
     *
     * @param context Context of current state of the application/object
     * @param key     String used as a key for accessing the value.
     * @return -1 by default; returns the Integer value for the given key.
     */
    fun getIntValue(context: Context, key: String?): Int {
        return getPreferenceInstance(context)!!.getInt(key, -1)
    }

    /**
     * Returns String value for the given key.
     * By default it will return null.
     *
     * @param context Context of current state of the application/object
     * @param key     String used as a key for accessing the value.
     * @return null by default; returns the String value for the given key.
     */
    fun getStringValue(context: Context, key: String?): String? {
        return getPreferenceInstance(context)!!.getString(key, null)
    }

    fun getLongValue(context: Context, key: String?): Long {
        return getPreferenceInstance(context)!!.getLong(key, 0)
    }

    fun clearAll(context: Context) {
        val fcmToken =
            instance.getStringValue(context, FCM_TOKEN)
        preference.edit().clear().apply()
        instance.setSharedValue(context, FCM_TOKEN, fcmToken)
    }

    companion object {
        private val sharedPref: SharedPref =SharedPref()

        //Single ton method for this class...
        val instance: SharedPref
            get() = sharedPref
    }
}