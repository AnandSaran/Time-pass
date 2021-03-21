package com.gregantech.timepass.base

import android.util.Log
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.*
import com.gregantech.timepass.model.DownloadResult
import com.gregantech.timepass.model.RailItemTypeTwoModel
import com.gregantech.timepass.util.AdvertisementHandler
import com.gregantech.timepass.util.constant.RAW_DOWNLOAD_PATH
import com.gregantech.timepass.util.extension.*
import com.gregantech.timepass.widget.CustomProgressbar
import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject

/**
 * Created by anand on 2020-11-06.
 */

abstract class TimePassBaseActivity : AppCompatActivity() {
    private lateinit var customProgressbar: CustomProgressbar
    protected val TAG = this.javaClass.simpleName
    val ktor: HttpClient by inject()

    fun showProgressBar() {
        getProgressBar().show()
    }

    fun dismissProgressBar() {
        runOnUiThread {
            try {
                getProgressBar().dismissProgress()
            } catch (e: Exception) {

            }
        }
    }

    private fun getProgressBar(): CustomProgressbar {
        if (!::customProgressbar.isInitialized) {
            customProgressbar = CustomProgressbar(this)
        }
        return customProgressbar
    }

    fun isNotDownloaded(fileName: String, isShareClick: Boolean) =
        if (isFileDownloaded(fileName)) {
            if (isShareClick) shareFile(RAW_DOWNLOAD_PATH.plus(fileName))
            else "File already Downloaded ".toast(this)
            false
        } else true

    fun downloadWithFlow(model: RailItemTypeTwoModel) {
        CoroutineScope(Dispatchers.IO).launch {
            ktor.downloadFile(model.file, model.video).collect {
                withContext(Dispatchers.Main) {
                    when (it) {
                        is DownloadResult.Success -> {
                            shareVideoText(model.file)
                        }
                        is DownloadResult.Error -> {
                        }
                        is DownloadResult.Progress -> {
                        }
                    }
                }
            }
        }
    }

    protected fun initBannerAd(bannerContainer: FrameLayout?, adType: Int) {

        if (bannerContainer == null || !AdvertisementHandler.isAdEnabled(adType.toString()))
            return

        val adView = AdView(this).apply {
            adSize = AdSize.BANNER
            adUnitId = AdvertisementHandler.getAdUnitByType(adType)
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