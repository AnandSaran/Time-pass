package com.gregantech.timepass.view.tiktok.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.gregantech.timepass.R
import com.gregantech.timepass.base.TimePassBaseResult
import com.gregantech.timepass.databinding.ActivityTiktokBinding
import com.gregantech.timepass.general.bundklekey.TikTokBundleKeyEnum
import com.gregantech.timepass.network.repository.VideoListRepository
import com.gregantech.timepass.network.response.Video
import com.gregantech.timepass.util.constant.VIEW_MODEL_IN_ACCESSIBLE_MESSAGE
import com.gregantech.timepass.util.extension.toast
import com.gregantech.timepass.util.sharedpreference.SharedPreferenceHelper
import com.gregantech.timepass.view.profile.viewmodel.UserVideoListActivityViewModel
import com.gregantech.timepass.view.tiktok.adapter.TikTokPagerAdapter
import com.gregantech.timepass.view.tiktok.work.PreCachingService
import java.util.*

class TikTokActivity : FragmentActivity() {


    private lateinit var activityViewModelFactory: UserVideoListActivityViewModel.Factory
    private lateinit var binding: ActivityTiktokBinding
    private val videoObj by lazy { intent?.extras?.getParcelable<Video>(TikTokBundleKeyEnum.VIDEO_DATA.value) }
    private val tiktokPagerAdapter by lazy { TikTokPagerAdapter(this) }

    private var playingPosition = -1
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
        fun present(context: Context, video: Video) {
            val intent = Intent(context, TikTokActivity::class.java)
            intent.putExtra(TikTokBundleKeyEnum.VIDEO_DATA.value, video)
            context.startActivity(intent)
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
        //doFetchVideoList()
    }

    private fun subscribeToObservers() {

    }


    private fun setAssets() {
        binding.vpTikTok.adapter = tiktokPagerAdapter
    }

    private fun doFetchVideoList() {

        isLoading = true
        "Loading More..".toast(this)
        viewModel.getFullScreenVideos("27", ++currentPage)
            .observe(this, androidx.lifecycle.Observer { videoModel ->
                when (videoModel.status) {
                    TimePassBaseResult.Status.LOADING -> {
                    }
                    TimePassBaseResult.Status.SUCCESS -> {
                        videoModel.data?.let {
                            totalPages = it.total_pages!!
                            doBindVideoList(it.video as ArrayList<Video>)
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
        tiktokPagerAdapter.refresh(vidList)
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

}