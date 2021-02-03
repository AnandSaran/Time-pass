package com.gregantech.timepass.base

import androidx.fragment.app.Fragment
import com.gregantech.timepass.model.RailItemTypeTwoModel

/**
 * Created by anand
 */

abstract class TimePassBaseFragment : Fragment() {
    protected val TAG = this::class.java.simpleName

    val baseActivity: TimePassBaseActivity
        get() = activity as TimePassBaseActivity

    fun showProgressBar() {
        baseActivity.showProgressBar()
    }

    fun dismissProgressBar() {
        baseActivity.dismissProgressBar()
    }

    fun downloadWithFlow(railItemTypeTwoModel: RailItemTypeTwoModel) {
        baseActivity.downloadWithFlow(railItemTypeTwoModel)
    }

}