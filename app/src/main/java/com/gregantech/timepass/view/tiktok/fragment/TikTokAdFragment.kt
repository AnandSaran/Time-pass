package com.gregantech.timepass.view.tiktok.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.databinding.DataBindingUtil
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.gregantech.timepass.R
import com.gregantech.timepass.base.TimePassBaseFragment
import com.gregantech.timepass.databinding.FragmentTikTokAdBinding
import com.gregantech.timepass.util.FULL_SCREEN_ADMIN_VIDEO_LIST
import com.gregantech.timepass.view.tiktok.activity.TikTokActivity


class TikTokAdFragment : TimePassBaseFragment() {

    private lateinit var binding: FragmentTikTokAdBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_tik_tok_ad, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        loadTikTokInterstitial(FULL_SCREEN_ADMIN_VIDEO_LIST, adListener)
    }

    private val adListener = object : InterstitialAdLoadCallback() {
        override fun onAdLoaded(@NonNull interstitialAd: InterstitialAd) {
            Log.d(TAG, "onAdLoaded: ")
            interstitialAd.run {
                fullScreenContentCallback = object :
                    FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        super.onAdDismissedFullScreenContent()
                        moveToNextPage()
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
                        super.onAdFailedToShowFullScreenContent(adError)
                        moveToNextPage()
                    }

                    override fun onAdShowedFullScreenContent() {
                        super.onAdShowedFullScreenContent()
                        Log.d(TAG, "onAdShowedFullScreenContent ")
                    }
                }
                show(requireActivity())
            }
        }

        override fun onAdFailedToLoad(loadError: LoadAdError) {
            super.onAdFailedToLoad(loadError)
            Log.e(TAG, "onAdFailedToLoad: loadError ${loadError.message}")
            moveToNextPage()
        }
    }

    private fun moveToNextPage() {
        (requireActivity() as TikTokActivity).moveToNextPage()
    }


}