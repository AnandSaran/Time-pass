package com.gregantech.timepass.util.sharedpreference

import android.content.Context
import android.content.SharedPreferences
import com.gregantech.timepass.R

object SharedPreferenceUtil : SharedPreferenceAction() {
    private val TAG = SharedPreferenceUtil::class.java.simpleName
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var context: Context

    fun init(context: Context) {
        this.context = context
        createEncryptedSharedPreference()
    }

    private fun createEncryptedSharedPreference() {
        sharedPreferences = context.getSharedPreferences(
            context.getString(R.string.app_name),
            Context.MODE_PRIVATE
        )
    }

    public override fun putLongValue(key: String, value: Long) {
        sharedPreferences.edit().putLong(key, value).apply()
    }

    public override fun putIntValue(key: String, value: Int) {
        sharedPreferences.edit().putInt(key, value).apply()
    }

    public override fun putStringValue(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    public override fun putBooleanValue(key: String, value: Boolean) {
        sharedPreferences.edit().putBoolean(key, value).apply()
    }

    public override fun getLongValue(key: String, defValue: Long): Long {
        return sharedPreferences.getLong(key, defValue)
    }

    public override fun getIntValue(key: String): Int {
        return sharedPreferences.getInt(key, SharedPreferenceDefaultValueEnum.INT.value as Int)
    }

    public override fun getStringValue(key: String): String {
        return sharedPreferences.getString(
            key,
            SharedPreferenceDefaultValueEnum.STRING.value as String
        ) ?: SharedPreferenceDefaultValueEnum.STRING.value
    }

    public override fun getBooleanValue(key: String): Boolean {
        return sharedPreferences.getBoolean(
            key,
            SharedPreferenceDefaultValueEnum.BOOLEAN.value as Boolean
        )
    }

    public override fun clear() {
        sharedPreferences.edit().clear().apply()
    }

    public override fun remove(key: String) {
        sharedPreferences.edit().remove(key).apply()
    }

    public override fun getAll(): Map<String, Any> {
        TODO("Not yet implemented")
    }

    fun getSharedPreference(): SharedPreferences {
        return sharedPreferences
    }
}