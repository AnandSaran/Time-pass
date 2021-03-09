package com.gregantech.timepass.adapter.rail

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.*
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.gregantech.timepass.BuildConfig
import com.gregantech.timepass.R
import com.gregantech.timepass.adapter.handler.rail.RailItemClickHandler
import com.gregantech.timepass.databinding.ItemAdsBinding
import com.gregantech.timepass.databinding.ItemInstagramBinding
import com.gregantech.timepass.model.RailBaseItemModel
import com.gregantech.timepass.model.RailItemTypeTwoModel
import com.gregantech.timepass.util.NewPlayerViewAdapter
import com.gregantech.timepass.util.extension.*
import java.util.*


/**
 * A custom adapter to use with the RecyclerView widget.
 */
class InstagramAdapter(
    private var modelList: ArrayList<RailBaseItemModel>,
    private val railItemClickHandler: RailItemClickHandler,
    private val playerViewAdapter: NewPlayerViewAdapter
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        Log.d("InstagramAdapter", "onCreateViewHolder: viewType $viewType")
        val layoutInflater = LayoutInflater.from(viewGroup.context)
        return if (viewType == 1) AdViewHolder(
            ItemAdsBinding.inflate(
                layoutInflater,
                viewGroup,
                false
            )
        )
        else VideoPlayerViewHolder(ItemInstagramBinding.inflate(layoutInflater, viewGroup, false))

    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        val model = getItem(position)
        when (holder) {
            is VideoPlayerViewHolder -> holder.onBind(model)
            is AdViewHolder -> holder.onBind(model)
        }
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        val position = holder.adapterPosition
        playerViewAdapter.releaseRecycledPlayers(position)
        super.onViewRecycled(holder)
    }

    override fun getItemCount(): Int {
        return modelList.size
    }

    private fun getItem(position: Int): RailItemTypeTwoModel {
        return modelList[position] as RailItemTypeTwoModel
    }

    override fun getItemViewType(position: Int) =
        (modelList[position] as RailItemTypeTwoModel).viewType

    inner class AdViewHolder(private val binding: ItemAdsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun onBind(model: RailItemTypeTwoModel) {

            var currentNativeAd: NativeAd? = null

            val builder = AdLoader.Builder(itemView.context, BuildConfig.AD_NATIVE)
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
                    Log.e("InstaAdapter", "onAdFailedToLoad: error $error")
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

    inner class VideoPlayerViewHolder(private val binding: ItemInstagramBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(model: RailItemTypeTwoModel) {
            binding.apply {
                dataModel = model
                setupOnClick(model)
                setupFollow(model.isShowFollow)
                setupProfile(model)
                setupMediaContent(model)
                executePendingBindings()
            }
        }

        private fun setupFollow(isShowFollow: Boolean) {
            binding.ivFollow.visible(isShowFollow)
        }

        private fun setupProfile(model: RailItemTypeTwoModel) {
            binding.clProfile.visible(model.isShowProfile)
            if (model.isShowProfile) {
                binding.ivUserProfile.loadUrlCircle(
                    model.userImage,
                    R.drawable.place_holder_profile
                )
            }
        }

        private fun setupOnClick(railItem: RailItemTypeTwoModel) {
            binding.root.setOnClickListener {
                if (railItemClickHandler.isInitializedForPosterClicking()) {
                    railItemClickHandler.clickPoster(railItem)
                }
            }

            binding.ivFollow.setOnClickListener {
                onClickFollow(railItemClickHandler, railItem)
            }

            binding.ivLike.setOnClickListener {
                onClickLike(railItemClickHandler, railItem)
            }

            binding.ivComment.setOnClickListener {
                onClickComment(railItemClickHandler, railItem)
            }

            binding.tvTotalLike.setOnClickListener {
                onClickLike(railItemClickHandler, railItem)
            }

            binding.ivShare.setOnClickListener {
                onClickShare(railItemClickHandler, railItem)
            }
            /* binding.ivDownload.setOnClickListener {
                 onClickDownload(railItemClickHandler, railItem)
             }*/

            binding.clProfile.setOnClickListener {
                onClickProfile(railItemClickHandler, railItem)
            }
            binding.ivMore.setOnClickListener {
                onClickMore(railItemClickHandler, railItem)
            }
        }

        private fun onClickFollow(
            railItemClickHandler: RailItemClickHandler,
            railItem: RailItemTypeTwoModel
        ) {
            if (railItemClickHandler.isInitializedForFollowClicking()) {
                changeFollowIcon(railItem.isFollowed)
                railItemClickHandler.clickFollow(railItem)
            }
        }

        private fun onClickLike(
            railItemClickHandler: RailItemClickHandler,
            railItem: RailItemTypeTwoModel
        ) {
            if (railItemClickHandler.isInitializedForLikeClicking()) {
                changeLikeIcon(railItem.isLiked)
                changeTotalLike(railItem)
                railItemClickHandler.clickLike(railItem)
            }
        }

        private fun onClickComment(
            railItemClickHandler: RailItemClickHandler,
            railItem: RailItemTypeTwoModel
        ) {
            railItemClickHandler.clickComment(railItem)
        }

        private fun changeFollowIcon(isFollowed: Boolean) {
            binding.ivFollow.loadDrawable(getFollowDrawable(!isFollowed))
        }

        private fun changeLikeIcon(isLiked: Boolean) {
            binding.ivLike.loadDrawable(getLikeDrawable(!isLiked))
        }

        private fun changeTotalLike(railItem: RailItemTypeTwoModel) {
            binding.tvTotalLike.text = generateTotalLike(railItem)
        }

        private fun setupMediaContent(model: RailItemTypeTwoModel) {
            val isImage = model.isImage
            if (isImage != null && isImage) {
                binding.clPlayer.visible(false)
                binding.ivPoster.visible(true)
                binding.ivPoster.loadUrl(model.image)
            } else {
                binding.ivPoster.visible(false)
                binding.clPlayer.visible(true)
                playerViewAdapter.loadVideo(
                    model.video,
                    binding.progressBar,
                    adapterPosition,
                    binding.vvPlayer
                )
            }
        }

    }

    private fun generateTotalLike(railItem: RailItemTypeTwoModel): String {
        return when (railItem.isLiked) {
            true -> {
                --railItem.totalLike
            }
            false -> {
                ++railItem.totalLike
            }
        }.toString()

    }

    /*  private fun onClickDownload(
          railItemClickHandler: RailItemClickHandler,
          railItem: RailItemTypeTwoModel
      ) {
          if (railItemClickHandler.isInitializedForDownloadClicking()) {
              railItemClickHandler.clickDownload(railItem)
          }
      }*/

    private fun onClickProfile(
        railItemClickHandler: RailItemClickHandler,
        railItem: RailItemTypeTwoModel
    ) {
        if (railItemClickHandler.isInitializedForProfileClicking()) {
            railItemClickHandler.clickProfile(railItem)
        }
    }

    private fun onClickMore(
        railItemClickHandler: RailItemClickHandler,
        railItem: RailItemTypeTwoModel
    ) {
        if (railItemClickHandler.isInitializedForMoreClicking()) {
            railItemClickHandler.clickMore(railItem)
        }
    }

    private fun onClickShare(
        railItemClickHandler: RailItemClickHandler,
        railItem: RailItemTypeTwoModel
    ) {
        if (railItemClickHandler.isInitializedForShareClicking()) {
            railItemClickHandler.clickShare(railItem)
        }
    }

    private fun getFollowDrawable(isFollowed: Boolean): Int {
        return if (isFollowed) {
            R.drawable.ic_followed
        } else {
            R.drawable.ic_follow_black
        }

    }

    private fun getLikeDrawable(isLiked: Boolean): Int {
        return if (isLiked) {
            R.drawable.ic_like
        } else {
            R.drawable.ic_un_like_black
        }

    }
}