package com.gregantech.timepass.base

import android.content.Context
import android.util.Log
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.gregantech.timepass.model.RailItemTypeTwoModel
import com.gregantech.timepass.util.AdvertisementHandler
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

    private var mInterstitialAd: InterstitialAd? = null

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


    protected fun initBannerAd(bannerContainer: FrameLayout?, adName: String) {

        if (bannerContainer == null || !AdvertisementHandler.isAdEnabled(adName))
            return

        val adView = AdView(requireContext()).apply {
            adSize = AdSize.BANNER
            adUnitId = AdvertisementHandler.getAdUnitByType(adName)
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


    protected fun loadInterstitial(adName: String) {
        if (AdvertisementHandler.isAdEnabled(adName)) {
            InterstitialAd.load(
                requireContext(),
                AdvertisementHandler.getAdUnitByType(adName),
                AdRequest.Builder().build(),
                interstitialListener
            )
        } else
            Log.e(TAG, "loadInterstitial: Full screen ad is not enabled")
    }

    private val interstitialListener = object : InterstitialAdLoadCallback() {
        override fun onAdLoaded(@NonNull interstitialAd: InterstitialAd) {
            Log.d(TAG, "onAdLoaded: ")
            mInterstitialAd = interstitialAd
            mInterstitialAd?.run {
                fullScreenContentCallback = fullScreenCallback
                show(requireActivity())
            }
        }

        override fun onAdFailedToLoad(loadError: LoadAdError) {
            super.onAdFailedToLoad(loadError)
            Log.e(TAG, "onAdFailedToLoad: loadError ${loadError.message}")
            mInterstitialAd = null
        }
    }

    private val fullScreenCallback = object :
        FullScreenContentCallback() {
        override fun onAdDismissedFullScreenContent() {
            super.onAdDismissedFullScreenContent()
            mInterstitialAd = null
        }

        override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
            super.onAdFailedToShowFullScreenContent(adError)
            mInterstitialAd = null
        }

        override fun onAdShowedFullScreenContent() {
            super.onAdShowedFullScreenContent()
            mInterstitialAd = null
        }
    }

    protected fun releaseInterstitialAd() {
        mInterstitialAd = null
    }

}