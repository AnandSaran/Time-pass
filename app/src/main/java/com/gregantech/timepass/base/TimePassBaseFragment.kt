package com.gregantech.timepass.base

import android.content.Context
import androidx.fragment.app.Fragment
import com.gregantech.timepass.model.RailItemTypeTwoModel
import com.gregantech.timepass.util.constant.RAW_DOWNLOAD_PATH
import com.gregantech.timepass.util.extension.isFileDownloaded
import com.gregantech.timepass.util.extension.shareFile
import com.gregantech.timepass.util.extension.toast

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

    fun isNotDownloaded(context: Context, fileName: String, isShareClick: Boolean) =
        if (context.isFileDownloaded(fileName)) {
            if (isShareClick) requireContext().shareFile(RAW_DOWNLOAD_PATH.plus(fileName))
            else "File already Downloaded ".toast(context)
            false
        } else true

}