package com.gregantech.timepass.view.tiktok.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.gregantech.timepass.BuildConfig.FULL_SCREEN_ADMIN_VIDEO_LIST
import com.gregantech.timepass.R
import com.gregantech.timepass.base.TimePassBaseActivity
import com.gregantech.timepass.base.TimePassBaseResult
import com.gregantech.timepass.databinding.ActivityTiktokBinding
import com.gregantech.timepass.general.bundklekey.CategoryDetailBundleKeyEnum
import com.gregantech.timepass.general.bundklekey.TikTokBundleKeyEnum
import com.gregantech.timepass.network.repository.VideoListRepository
import com.gregantech.timepass.network.response.Video
import com.gregantech.timepass.util.AdvertisementHandler
import com.gregantech.timepass.util.constant.VIEW_MODEL_IN_ACCESSIBLE_MESSAGE
import com.gregantech.timepass.util.extension.toast
import com.gregantech.timepass.util.sharedpreference.SharedPreferenceHelper
import com.gregantech.timepass.view.profile.viewmodel.UserVideoListActivityViewModel
import com.gregantech.timepass.view.tiktok.adapter.TikTokPagerAdapter
import com.gregantech.timepass.view.tiktok.work.PreCachingService

class TikTokActivity : TimePassBaseActivity() {

    private lateinit var activityViewModelFactory: UserVideoListActivityViewModel.Factory
    private lateinit var binding: ActivityTiktokBinding

    private val videoObj by lazy { intent?.extras?.getParcelable<Video>(TikTokBundleKeyEnum.VIDEO_DATA.value) }
    private val playingPosition by lazy { intent?.extras?.getLong(TikTokBundleKeyEnum.SEEK_POSITION.value) }

    private val tiktokPagerAdapter by lazy { TikTokPagerAdapter(this) }

    private var totalPages = 0
    private var currentPage = 1
    private var isLoading = false

    private val viewModel: UserVideoListActivityViewModel by lazy {
        requireNotNull(this) {
            VIEW_MODEL_IN_ACCESSIBLE_MESSAGE
        }
        ViewModelProvider(
            this,
            activityViewModelFactory
        ).get(UserVideoListActivityViewModel::class.java)
    }

    companion object {
        fun present(context: Context, video: Video, seekPosition: Long = -1) {
            val intent = Intent(context, TikTokActivity::class.java)
            intent.putExtra(TikTokBundleKeyEnum.VIDEO_DATA.value, video)
            intent.putExtra(TikTokBundleKeyEnum.SEEK_POSITION.value, seekPosition)
            context.startActivity(intent)
        }

        fun generateIntent(
            context: Context, video: Video, seekPosition: Long = -1
        ): Intent {
            val intent = Intent(context, TikTokActivity::class.java)
            intent.putExtra(TikTokBundleKeyEnum.VIDEO_DATA.value, video)
            intent.putExtra(TikTokBundleKeyEnum.SEEK_POSITION.value, seekPosition)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_tiktok)
        initVieModel()
        initPlayer()
        subscribeToObservers()
        setAssets()
    }

    private fun initVieModel() {
        activityViewModelFactory = UserVideoListActivityViewModel.Factory(
            VideoListRepository(),
            SharedPreferenceHelper
        )
    }

    private fun initPlayer() {
        val vidList = arrayListOf(videoObj!!)
        doBindVideoList(vidList)
        doFetchVideoList()
    }

    private fun subscribeToObservers() {

    }


    private fun setAssets() {
        binding.vpTikTok.apply {
            adapter = tiktokPagerAdapter
            registerOnPageChangeCallback(pageChangeCallBack)
        }
    }

    private val pageChangeCallBack = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            binding.vpTikTok.adapter?.let {
                val totalCount = it.itemCount
                Log.d("TikTok", "totalCount $totalCount position $position")
                if (position != 0 && position == totalCount - 1) { //last item
                    currentPage++
                    doFetchVideoList()
                }
            }
        }
    }

    private fun fetchCheckOk(): Boolean {
        return when {
            isLoading -> false
            ((currentPage != 1) && (currentPage >= totalPages)) -> {
                getString(R.string.no_more_videos).toast(this)
                false
            }
            else -> true
        }
    }

    private fun doFetchVideoList() {
        if (!fetchCheckOk())
            return
        Log.d(
            "doFetchVideoList",
            "currentPage $currentPage isLoading $isLoading userId ${videoObj?.followerId}"
        )

        isLoading = true
        "Loading More..Page $currentPage".toast(this)
        viewModel.getFullScreenVideos(videoObj?.followerId!!, currentPage)
            .observe(this, androidx.lifecycle.Observer { videoModel ->
                when (videoModel.status) {
                    TimePassBaseResult.Status.LOADING -> {
                    }
                    TimePassBaseResult.Status.SUCCESS -> {
                        videoModel.data?.let {
                            if (it.video.isNotEmpty()) {
                                totalPages = it.total_pages!!
                                doBindVideoList(it.video as ArrayList<Video>)
                            } else {
                                totalPages = currentPage
                            }
                        }
                        isLoading = false
                    }
                    TimePassBaseResult.Status.ERROR -> {
                        isLoading = false
                        videoModel.message?.toast(this)
                    }
                }
            })
    }

    private fun doBindVideoList(vidList: ArrayList<Video>) {
        startPreCaching(vidList)
        tiktokPagerAdapter.playPosition = playingPosition ?: 0L
        tiktokPagerAdapter.refresh(withAds(vidList))
    }

    private fun withAds(vidList: ArrayList<Video>): ArrayList<Video> {
        if (AdvertisementHandler.isAdEnabled(FULL_SCREEN_ADMIN_VIDEO_LIST)) {
            for (i in 1..vidList.size) {
                if (i % 4 == 0) // add after 3rd item
                    vidList.add(i - 1, Video(viewType = 1))
            }
        }
        return vidList
    }

    private fun startPreCaching(vidList: ArrayList<Video>) {
        val urlList = arrayOfNulls<String>(vidList.size)
        vidList.mapIndexed { index, model ->
            urlList[index] = model.videoName
        }
        val inputData =
            Data.Builder().putStringArray(TikTokBundleKeyEnum.VIDEO_URLS.value, urlList).build()
        val preCachingWork = OneTimeWorkRequestBuilder<PreCachingService>().setInputData(inputData)
            .build()
        WorkManager.getInstance(this)
            .enqueue(preCachingWork)
    }

    override fun onBackPressed() {
        val data = Intent().apply {
            putExtra(
                CategoryDetailBundleKeyEnum.VIDEO_POSITION.value,
                tiktokPagerAdapter.tikTokFragment.playingPosition
            )
            putExtra(
                TikTokBundleKeyEnum.VIDEO_DATA.value,
                tiktokPagerAdapter.tikTokFragment.currentItem
            )
        }
        setResult(Activity.RESULT_OK, data)
        super.onBackPressed()
    }


    override fun onDestroy() {
        super.onDestroy()
        binding.vpTikTok.unregisterOnPageChangeCallback(pageChangeCallBack)
    }

    fun moveToNextPage() {
        binding.vpTikTok.apply {
            currentItem += 1
        }
    }

}