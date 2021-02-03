package com.gregantech.timepass.view.profile.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.gregantech.timepass.R
import com.gregantech.timepass.adapter.handler.rail.RailItemClickHandler
import com.gregantech.timepass.adapter.rail.InstagramAdapter
import com.gregantech.timepass.base.TimePassBaseResult
import com.gregantech.timepass.databinding.ActivityUserVideoListBinding
import com.gregantech.timepass.general.bundklekey.CategoryDetailBundleKeyEnum
import com.gregantech.timepass.general.bundklekey.UserVideoListActivityBundleKeyEnum
import com.gregantech.timepass.model.RailBaseItemModel
import com.gregantech.timepass.model.RailItemTypeTwoModel
import com.gregantech.timepass.network.repository.VideoListRepository
import com.gregantech.timepass.network.repository.bridge.toRailItemTypeTwoModelList
import com.gregantech.timepass.network.response.User
import com.gregantech.timepass.network.response.Video
import com.gregantech.timepass.util.PlayerViewAdapter
import com.gregantech.timepass.util.constant.*
import com.gregantech.timepass.util.extension.appendPostText
import com.gregantech.timepass.util.extension.toast
import com.gregantech.timepass.view.player.activity.PlayerActivity
import com.gregantech.timepass.view.profile.viewmodel.UserVideoListViewModel
import com.gregantech.timepass.widget.PaginationScrollListener

class UserVideoListActivity : AppCompatActivity() {
    private var user: User = User()
    private lateinit var binding: ActivityUserVideoListBinding
    private lateinit var viewModelFactory: UserVideoListViewModel.Factory
    var currentIndex = -1

    private val viewModel: UserVideoListViewModel by lazy {
        requireNotNull(this) {
            VIEW_MODEL_IN_ACCESSIBLE_MESSAGE
        }
        ViewModelProvider(this, viewModelFactory).get(UserVideoListViewModel::class.java)
    }

    companion object {
        fun present(
            context: Context,
            userId: String,
            videos: ArrayList<Video>,
            isLastData: Boolean,
            pageNo: Int,
            isLastPage: Boolean,
            scrollToPosition: Int,
            userName: String
        ) {
            val intent = Intent(context, UserVideoListActivity::class.java)
            intent.putExtra(UserVideoListActivityBundleKeyEnum.USER_ID.value, userId)
            intent.putParcelableArrayListExtra(
                UserVideoListActivityBundleKeyEnum.VIDEO_LIST.value,
                videos
            )
            intent.putExtra(UserVideoListActivityBundleKeyEnum.IS_LAST_DATA.value, isLastData)
            intent.putExtra(UserVideoListActivityBundleKeyEnum.PAGE_NO.value, pageNo)
            intent.putExtra(UserVideoListActivityBundleKeyEnum.IS_LAST_PAGE.value, isLastPage)
            intent.putExtra(
                UserVideoListActivityBundleKeyEnum.SCROLL_TO_POSITION.value,
                scrollToPosition
            )
            intent.putExtra(
                UserVideoListActivityBundleKeyEnum.USER_NAME.value,
                userName
            )

            context.startActivity(intent)
        }
    }

    private var userId = EMPTY_STRING
    private var userName = EMPTY_STRING

    private lateinit var railItemClickHandler: RailItemClickHandler
    private var railList: ArrayList<RailBaseItemModel> = arrayListOf()

    private var isLastData = EMPTY_BOOLEAN
    private var pageNo = EMPTY_INT

    private var isLastPage = EMPTY_BOOLEAN
    var isLoading: Boolean = false
    private var scrollToPosition = EMPTY_INT

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initDataBinding()
        setupIntentData()
        setupToolBar()
        setupViewModelFactory()
        onClickRailListItem()
        setupRecyclerView(railList)
    }

    override fun onPause() {
        super.onPause()
        PlayerViewAdapter.pauseCurrentPlayingVideo()
    }

    override fun onResume() {
        super.onResume()
        if (currentIndex != -1) {
            PlayerViewAdapter.playIndexThenPausePreviousPlayer(currentIndex)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        PlayerViewAdapter.releaseAllPlayers()
    }

    private fun setupViewModelFactory() {
        viewModelFactory = UserVideoListViewModel.Factory(
            VideoListRepository()
        )
    }

    private fun initDataBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_video_list)
    }

    private fun setupIntentData() {
        userId =
            intent.getStringExtra(UserVideoListActivityBundleKeyEnum.USER_ID.value) ?: EMPTY_STRING
        userName =
            intent.getStringExtra(UserVideoListActivityBundleKeyEnum.USER_NAME.value)
                ?: EMPTY_STRING
        railList =
            (intent.getParcelableArrayListExtra<Video>(UserVideoListActivityBundleKeyEnum.VIDEO_LIST.value)
                ?: arrayListOf())
                .toRailItemTypeTwoModelList(false)
        isLastData = intent.getBooleanExtra(
            UserVideoListActivityBundleKeyEnum.IS_LAST_DATA.value, EMPTY_BOOLEAN
        )
        isLastPage = intent.getBooleanExtra(
            UserVideoListActivityBundleKeyEnum.IS_LAST_PAGE.value, EMPTY_BOOLEAN
        )
        pageNo = intent.getIntExtra(UserVideoListActivityBundleKeyEnum.PAGE_NO.value, EMPTY_INT)
        scrollToPosition = intent.getIntExtra(
            UserVideoListActivityBundleKeyEnum.SCROLL_TO_POSITION.value,
            EMPTY_INT
        )
    }


    private fun setupToolBar() {
        setSupportActionBar(binding.tbProfile.toolbar)
        setToolbarBackButton()
        setToolbarTitle()
    }

    private fun setToolbarBackButton() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    private fun setToolbarTitle() {
        supportActionBar?.title = userName.appendPostText()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun onClickRailListItem() {
        railItemClickHandler = RailItemClickHandler()
        railItemClickHandler.clickPoster = { railModel ->
            displayPlayerPage((railModel as RailItemTypeTwoModel).video)
        }
    }

    private fun setupRecyclerView(categoryVideoList: ArrayList<RailBaseItemModel>) {
        binding.rvUserVideoList.apply {
            adapter = InstagramAdapter(
                categoryVideoList,
                railItemClickHandler
            )
            smoothScrollToPosition(scrollToPosition)
            setupRecyclerViewScrollListener()
        }
    }

    private fun setupRecyclerViewScrollListener() {
        binding.rvUserVideoList.addOnScrollListener(object :
            PaginationScrollListener(binding.rvUserVideoList.layoutManager as LinearLayoutManager) {
            override fun isLastPage(): Boolean {
                return isLastPage
            }

            override fun isLoading(): Boolean {
                return isLoading
            }

            override fun onItemIsFirstVisibleItem(index: Int) {
                currentIndex = index
                if (index != -1) {
                    PlayerViewAdapter.playIndexThenPausePreviousPlayer(index)
                }
            }

            override fun loadMoreItems() {
                isLoading = true
                getMoreCategoryVideo()
            }
        })
    }

    private fun getMoreCategoryVideo() {
        if (isLastData) return
        viewModel.getMoreCategoryVideoList(userId, ++pageNo)
            .observe(this, Observer { categoryListResponse ->
                when (categoryListResponse.status) {
                    TimePassBaseResult.Status.LOADING -> {
                    }
                    TimePassBaseResult.Status.SUCCESS -> {
                        isLoading = false

                        categoryListResponse.data?.let {
                            isLastData = it.is_last
                            addMoreVideoList(it.video.toRailItemTypeTwoModelList(false))
                        }
                    }
                    TimePassBaseResult.Status.ERROR -> {
                        categoryListResponse.message?.toast(this)
                    }
                }
            })

    }

    private fun addMoreVideoList(newList: ArrayList<RailBaseItemModel>) {
        val startPosition = railList.size
        val endPosition = railList.size + newList.size
        railList.addAll(newList)
        binding.rvUserVideoList.adapter?.notifyItemRangeInserted(startPosition, endPosition)
    }


    private fun displayPlayerPage(videoUrl: String) {
        startForResult.launch(
            PlayerActivity.generateIntent(
                this,
                videoUrl,
                PlayerViewAdapter.getCurrentPlayerPosition()
            )
        )
    }

    val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.let {

                    val playerCurrentPosition = it.getLongExtra(
                        CategoryDetailBundleKeyEnum.VIDEO_POSITION.value,
                        EMPTY_LONG
                    )
                    changePlayerCurrentPosition(playerCurrentPosition)
                }
            }
        }

    private fun changePlayerCurrentPosition(playerCurrentPosition: Long) {
        PlayerViewAdapter.changePlayerCurrentPosition(playerCurrentPosition)
    }
}