package com.gregantech.timepass.base

import androidx.fragment.app.Fragment

/**
 * Created by anand
 */

abstract class TimePassBaseFragment : Fragment() {
    private val TAG = this::class.java.simpleName

    val baseActivity: TimePassBaseActivity
        get() = activity as TimePassBaseActivity

    fun showProgressBar() {
        baseActivity.showProgressBar()
    }

    fun dismissProgressBar() {
        baseActivity.dismissProgressBar()
    }

}