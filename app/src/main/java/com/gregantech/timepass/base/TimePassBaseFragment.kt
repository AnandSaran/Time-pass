package com.gregantech.timepass.base

import android.content.Context
import android.util.Log
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.*
import com.gregantech.timepass.BuildConfig
import com.gregantech.timepass.model.RailItemTypeTwoModel
import com.gregantech.timepass.util.constant.RAW_DOWNLOAD_PATH
import com.gregantech.timepass.util.extension.isFileDownloaded
import com.gregantech.timepass.util.extension.shareFile
import com.gregantech.timepass.util.extension.testDeviceList
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


    protected fun initBannerAd(bannerContainer: FrameLayout?) {

        if (bannerContainer == null)
            return

        val adView = AdView(requireContext()).apply {
            adSize = AdSize.BANNER
            adUnitId = BuildConfig.AD_BANNER
            adListener = bannerAdListener
        }

        with(bannerContainer) {
            if (childCount > 0)
                removeAllViews()
            if (parent != null)
                (parent as ViewGroup).removeView(adView)

            addView(adView)
        }

        val requestConfiguration = RequestConfiguration.Builder()
            .setTestDeviceIds(testDeviceList)
            .build()
        MobileAds.setRequestConfiguration(requestConfiguration)
        adView.loadAd(AdRequest.Builder().build())
    }

    private val bannerAdListener = object : AdListener() {
        override fun onAdLoaded() {
            super.onAdLoaded()
            Log.d(TAG, "onAdLoaded: ")
        }

        override fun onAdFailedToLoad(error: LoadAdError?) {
            super.onAdFailedToLoad(error)
            Log.e(TAG, "onAdFailedToLoad: error $error")
        }
    }

}