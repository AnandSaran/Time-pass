package com.gregantech.timepass.base

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.*
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.gregantech.timepass.R
import com.gregantech.timepass.databinding.ItemAdsBinding
import com.gregantech.timepass.util.AdvertisementHandler
import com.gregantech.timepass.util.extension.testDeviceList

abstract class TimePassBaseAdAdapter(val adType: Int) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class AdViewHolder(private val binding: ItemAdsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun onBind() {

            if (!AdvertisementHandler.isAdEnabled(adType.toString())) {
                Log.e("TimePassBaseAdAdapter", "onBind: Ad is not enabled $adType")
                return
            }


            var currentNativeAd: NativeAd? = null

            val builder =
                AdLoader.Builder(itemView.context, AdvertisementHandler.getAdUnitByType(adType))
                    .forNativeAd { nativeAd ->
                        // You must call destroy on old ads when you are done with them,
                        // otherwise you will have a memory leak.
                        currentNativeAd?.destroy()
                        currentNativeAd = nativeAd
                        val layoutInflater = LayoutInflater.from(itemView.context)
                        val adView = layoutInflater
                            .inflate(R.layout.item_native_adview, null) as NativeAdView
                        populateNativeAdView(nativeAd, adView)
                        binding.frameAdContainer.removeAllViews()
                    binding.frameAdContainer.addView(adView)

                }

            val adLoader = builder.withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    val error =
                        "domain: ${loadAdError.domain}, code: ${loadAdError.code}, message: ${loadAdError.message}"
                    Log.e("TimePassBaseAdAdapter", "onAdFailedToLoad: error $error")
                }
            }).build()

            val requestConfiguration = RequestConfiguration.Builder()
                .setTestDeviceIds(testDeviceList)
                .build()
            MobileAds.setRequestConfiguration(requestConfiguration)
            adLoader.loadAd(AdRequest.Builder().build())
        }

    }


    private fun populateNativeAdView(nativeAd: NativeAd, adView: NativeAdView) {
        // Set the media view.
        adView.mediaView = adView.findViewById(R.id.adMedia)

        // Set other ad assets.
        adView.headlineView = adView.findViewById(R.id.tvAdTitle)
        adView.bodyView = adView.findViewById(R.id.tvAdBody)
        adView.callToActionView = adView.findViewById(R.id.btnAdAction)
        adView.iconView = adView.findViewById(R.id.ivAdLogo)
        adView.priceView = adView.findViewById(R.id.tvAdPrice)
        adView.starRatingView = adView.findViewById(R.id.rbAd)
        adView.storeView = adView.findViewById(R.id.tvAdStore)
        //adView.advertiserView = adView.findViewById(R.id.ad_advertiser)

        // The headline and media content are guaranteed to be in every UnifiedNativeAd.
        (adView.headlineView as TextView).text = nativeAd.headline
        adView.mediaView.setMediaContent(nativeAd.mediaContent)

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.body == null) {
            adView.bodyView.visibility = View.INVISIBLE
        } else {
            adView.bodyView.visibility = View.VISIBLE
            (adView.bodyView as TextView).text = nativeAd.body
        }

        if (nativeAd.callToAction == null) {
            adView.callToActionView.visibility = View.INVISIBLE
        } else {
            adView.callToActionView.visibility = View.VISIBLE
            (adView.callToActionView as Button).text = nativeAd.callToAction
        }

        if (nativeAd.icon == null) {
            adView.iconView.visibility = View.GONE
        } else {
            (adView.iconView as ImageView).setImageDrawable(
                nativeAd.icon.drawable
            )
            adView.iconView.visibility = View.VISIBLE
        }

        if (nativeAd.price == null) {
            adView.priceView.visibility = View.INVISIBLE
        } else {
            adView.priceView.visibility = View.VISIBLE
            (adView.priceView as TextView).text = nativeAd.price
        }

        if (nativeAd.store == null) {
            adView.storeView.visibility = View.INVISIBLE
        } else {
            adView.storeView.visibility = View.VISIBLE
            (adView.storeView as TextView).text = nativeAd.store
        }

        if (nativeAd.starRating == null) {
            adView.starRatingView.visibility = View.INVISIBLE
        } else {
            (adView.starRatingView as RatingBar).rating = nativeAd.starRating!!.toFloat()
            adView.starRatingView.visibility = View.VISIBLE
        }
        adView.setNativeAd(nativeAd)
    }

}