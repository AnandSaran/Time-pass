package com.gregantech.timepass.util.extension

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.annotation.DimenRes
import androidx.annotation.IntegerRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.gregantech.timepass.R
import com.gregantech.timepass.general.ConnectionType
import com.gregantech.timepass.util.NetworkConnectionUtil

/**
 * Created by ana on 17/8/20.
 */
val Context.isTablet: Boolean
    get() = this.resources.getBoolean(R.bool.isTablet)

fun Context.getIntValue(@IntegerRes value: Int): Int {
    return resources.getInteger(value)
}

fun Context.getDimenValue(@DimenRes value: Int): Float {
    return resources.getDimension(value)
}

fun Context.getDimenIntValue(@DimenRes value: Int): Int{
    return resources.getDimension(value).toInt()
}

fun Context.isNetworkTypeConnected(connectionType: ConnectionType): Boolean {
    return NetworkConnectionUtil.getNetworkType(this) == connectionType
}

fun Context.hideSoftKeyboard() {
    if (this is Activity) {
        currentFocus?.let { currentFocus ->
            val inputMethodManager = getSystemService(
                Context
                    .INPUT_METHOD_SERVICE
            ) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(currentFocus.windowToken, 0)
        }
    } else {
        this.hideSoftKeyboard()
    }
}

fun AppCompatActivity.showFragment(
    fragment: Fragment,
    frameId: Int,
    replace: Boolean = true,
    addToStack: Boolean = false,
    clearBackStack: Boolean = false
) {
    if (replace) {
        replaceFragment(fragment, frameId, addToStack, clearBackStack)
    } else {
        addFragment(fragment, frameId, addToStack)
    }
}

fun AppCompatActivity.popBackStack() {
    this.hideSoftKeyboard()
    supportFragmentManager.popBackStack()
}

fun AppCompatActivity.popBackStackInclusive() {
    this.hideSoftKeyboard()
    if (supportFragmentManager.backStackEntryCount > 0) {
        supportFragmentManager.popBackStack(
            supportFragmentManager.getBackStackEntryAt(0).id,
            FragmentManager.POP_BACK_STACK_INCLUSIVE
        )
    }
}

inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> FragmentTransaction) {
    beginTransaction().func().commit()
}

fun AppCompatActivity.replaceFragment(
    fragment: Fragment,
    frameId: Int,
    addToStack: Boolean = false,
    clearBackStack: Boolean = false
) {
    supportFragmentManager.inTransaction {
        if (clearBackStack && supportFragmentManager.backStackEntryCount > 0) {
            val first = supportFragmentManager.getBackStackEntryAt(0)
            supportFragmentManager.popBackStack(first.id, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }
        if (addToStack) {
            replace(frameId, fragment, fragment.javaClass.simpleName)
                .addToBackStack(fragment.javaClass.simpleName)
        } else {
            replace(frameId, fragment, fragment.javaClass.simpleName)
        }
    }
}

fun AppCompatActivity.addFragment(fragment: Fragment, frameId: Int, addToStack: Boolean = false) {
    supportFragmentManager.inTransaction {
        if (addToStack) {
            add(frameId, fragment, fragment.javaClass.simpleName)
                .addToBackStack(fragment.javaClass.simpleName)
        } else {
            add(frameId, fragment)
        }
    }
}