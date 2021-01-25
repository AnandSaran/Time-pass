package com.gregantech.timepass.util.sharedpreference

abstract class SharedPreferenceAction {
    protected abstract fun putLongValue(key: String, value: Long)
    protected abstract fun putIntValue(key: String, value: Int)
    protected abstract fun putStringValue(key: String, value: String)
    protected abstract fun putBooleanValue(key: String, value: Boolean)

    protected abstract fun getLongValue(key: String, defValue: Long): Long
    protected abstract fun getIntValue(key: String): Int
    protected abstract fun getStringValue(key: String): String
    protected abstract fun getBooleanValue(key: String): Boolean

    protected abstract fun clear()
    protected abstract fun remove(key: String)
    protected abstract fun getAll(): Map<String, Any>
}