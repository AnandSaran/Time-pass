package com.gregantech.timepass.view.profile.activity

import android.Manifest
import android.app.Activity
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.gregantech.timepass.R
import com.gregantech.timepass.adapter.handler.rail.RailItemClickHandler
import com.gregantech.timepass.adapter.rail.InstagramAdAdapter
import com.gregantech.timepass.base.TimePassBaseActivity
import com.gregantech.timepass.base.TimePassBaseResult
import com.gregantech.timepass.databinding.ActivityUserVideoListBinding
import com.gregantech.timepass.general.PostMoreOptionNavigationEnum
import com.gregantech.timepass.general.bundklekey.CategoryDetailBundleKeyEnum
import com.gregantech.timepass.general.bundklekey.UserVideoListActivityBundleKeyEnum
import com.gregantech.timepass.model.RailBaseItemModel
import com.gregantech.timepass.model.RailItemTypeTwoModel
import com.gregantech.timepass.model.getStrippedFileName
import com.gregantech.timepass.network.repository.VideoListRepository
import com.gregantech.timepass.network.repository.bridge.toRailItemTypeTwoModelList
import com.gregantech.timepass.network.response.User
import com.gregantech.timepass.network.response.Video
import com.gregantech.timepass.util.CARD_OTHER_USER_VIDEO_LIST
import com.gregantech.timepass.util.NewPlayerViewAdapter
import com.gregantech.timepass.util.Run
import com.gregantech.timepass.util.constant.*
import com.gregantech.timepass.util.extension.appendPostText
import com.gregantech.timepass.util.extension.shareDownloadedFile
import com.gregantech.timepass.util.extension.toast
import com.gregantech.timepass.util.sharedpreference.SharedPreferenceHelper
import com.gregantech.timepass.view.comment.fragment.CommentActivity
import com.gregantech.timepass.view.player.activity.ImageViewActivity
import com.gregantech.timepass.view.player.activity.PlayerActivity
import com.gregantech.timepass.view.profile.viewmodel.UserVideoListActivityViewModel
import com.gregantech.timepass.view.uservideolist.viewmodel.UserPostViewModel
import com.gregantech.timepass.widget.PaginationScrollListener
import com.gregantech.timepass.widget.dialog.BottomSheetDialogPostMoreOption
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission

class UserVideoListActivity : TimePassBaseActivity() {
    private var user: User = User()
    private lateinit var binding: ActivityUserVideoListBinding
    private lateinit var activityViewModelFactory: UserVideoListActivityViewModel.Factory
    var currentIndex = -1
    private val playerViewAdapter = NewPlayerViewAdapter()
    var downloadID: Long? = null
    var isRegistered = false

    private lateinit var userPostViewModelFactory: UserPostViewModel.Factory

    private val userPostViewModel: UserPostViewModel by lazy {
        requireNotNull(this) {
            VIEW_MODEL_IN_ACCESSIBLE_MESSAGE
        }
        ViewModelProvider(this, userPostViewModelFactory).get(UserPostViewModel::class.java)
    }

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
    var isShareClick = false
    var railModel = RailItemTypeTwoModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initDataBinding()
        setupIntentData()
        setupToolBar()
        setupViewModelFactory()
        onClickRailListItem()
        setupRecyclerView(railList)
        setupViewModelObserver()
    }

    override fun onPause() {
        super.onPause()
        playerViewAdapter.pauseCurrentPlayingVideo()
    }

    override fun onResume() {
        super.onResume()
        if (currentIndex != -1) {
            playerViewAdapter.playIndexThenPausePreviousPlayer(currentIndex)
        }
    }


    override fun onStart() {
        super.onStart()
        registerReceiver(
            downloadStatusReceiver,
            IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        )
        isRegistered = true
    }


    override fun onDestroy() {
        super.onDestroy()
        playerViewAdapter.releaseAllPlayers()
        if (isRegistered) {
            unregisterReceiver(downloadStatusReceiver)
        }
    }

    private val downloadStatusReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (downloadID == id && isShareClick) {
                shareDownloadedFile(downloadID!!)
            }
            if (isShareClick) {
                dismissProgressBar()
            } else
                getString(R.string.download_completed).toast(this@UserVideoListActivity)
        }
    }

    private fun setupViewModelFactory() {
        activityViewModelFactory = UserVideoListActivityViewModel.Factory(
            VideoListRepository(),
            SharedPreferenceHelper
        )
        userPostViewModelFactory =
            UserPostViewModel.Factory(
                VideoListRepository(),
                SharedPreferenceHelper
            )
    }

    private fun initDataBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_video_list)
    }

    private fun setupViewModelObserver() {
        viewModel.downloadRequest.observe(this, Observer {
            downloadVideo(it)
        })

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
                .toRailItemTypeTwoModelList(false, advertisementName = CARD_OTHER_USER_VIDEO_LIST)
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
            val railModel = railModel as RailItemTypeTwoModel
            val isImage = railModel.isImage
            if (isImage != null && isImage) {
                displayImagePage(railModel.image)
            } else {
                displayPlayerPage(railModel.video)
            }
        }
        railItemClickHandler.clickLike = { railModel ->
            onClickLike(railModel as RailItemTypeTwoModel)
        }

        railItemClickHandler.clickComment = { railModel ->
            onClickComment(railModel as RailItemTypeTwoModel)
        }

        railItemClickHandler.clickShare = { railModel ->
            isShareClick = true
            this.railModel = railModel as RailItemTypeTwoModel
            askPermission()
        }
        railItemClickHandler.clickMore = { railModel ->
            showPostMoreDialog(railModel as RailItemTypeTwoModel)
        }
    }

    private fun setupRecyclerView(categoryVideoList: ArrayList<RailBaseItemModel>) {
        binding.rvUserVideoList.apply {
            adapter = InstagramAdAdapter(
                categoryVideoList,
                railItemClickHandler,
                playerViewAdapter,
                CARD_OTHER_USER_VIDEO_LIST
            )
            layoutManager?.scrollToPosition(scrollToPosition)
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
                    playerViewAdapter.playIndexThenPausePreviousPlayer(index)
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
                            addMoreVideoList(
                                it.video.toRailItemTypeTwoModelList(
                                    false,
                                    advertisementName = CARD_OTHER_USER_VIDEO_LIST
                                )
                            )
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
                playerViewAdapter.getCurrentPlayerPosition()
            )
        )
    }

    private fun displayImagePage(imageUrl: String) {
        ImageViewActivity.present(this, imageUrl)
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
        playerViewAdapter.changePlayerCurrentPosition(playerCurrentPosition)
    }

    private fun onClickLike(railItemTypeTwoModel: RailItemTypeTwoModel) {
        when (railItemTypeTwoModel.isLiked) {
            true -> {
                railItemTypeTwoModel.isLiked = false
            }
            false -> {
                railItemTypeTwoModel.isLiked = true
            }
        }

        setVideoLike(railItemTypeTwoModel)
        //  notifyDataPosition(railList.indexOf(railItemTypeTwoModel), railItemTypeTwoModel)

    }

    private fun onClickComment(railItemTypeTwoModel: RailItemTypeTwoModel) {
        showCommentPage(railItemTypeTwoModel)
    }

    private fun showCommentPage(railItemTypeTwoModel: RailItemTypeTwoModel) {
        CommentActivity.present(
            this,
            railItemTypeTwoModel.contentId, isUserPost = true
        )
    }

    private fun onClickDownload() {
        if (isNotDownloaded(railModel.getStrippedFileName(), isShareClick))
            viewModel.createDownloadRequest(railModel, getString(R.string.app_name))
    }

    private fun setVideoLike(railItemTypeTwoModel: RailItemTypeTwoModel) {
        viewModel.setVideoLike(railItemTypeTwoModel.contentId, railItemTypeTwoModel.isLiked)
            .observe(this,
                Observer {
                })
    }

    private fun askPermission() {
        TedPermission.with(this)
            .setPermissionListener(permissionListener)
            .setDeniedMessage(getString(R.string.permission_denied_message))
            .setPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .check()
    }

    private var permissionListener: PermissionListener = object : PermissionListener {
        override fun onPermissionGranted() {
            onClickDownload()
        }

        override fun onPermissionDenied(deniedPermissions: List<String>) {
        }
    }

    private fun downloadVideo(request: DownloadManager.Request) {
        if (isShareClick) {
            showProgressBar()
        }
        getString(R.string.download_started).toast(this)
        val manager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadID = manager.enqueue(request)
    }

    private fun showPostMoreDialog(railModel: RailItemTypeTwoModel) {
        BottomSheetDialogPostMoreOption(
            this,
            userPostViewModel.generatePostMoreOptionContentModel(railModel),
            onPostMoreOptionClick(),
            railModel
        ).show()
    }

    private fun onPostMoreOptionClick(): (Any, PostMoreOptionNavigationEnum) -> Unit =
        { data: Any, postMoreOptionNavigationEnum: PostMoreOptionNavigationEnum ->
            val railModel = data as RailBaseItemModel
            when (postMoreOptionNavigationEnum) {
                PostMoreOptionNavigationEnum.NAVIGATION_EDIT -> {

                }
                PostMoreOptionNavigationEnum.NAVIGATION_DELETE -> {
                    deletePost(railModel)
                }
                PostMoreOptionNavigationEnum.NAVIGATION_DOWNLOAD -> {
                    isShareClick = false
                    this.railModel = railModel as RailItemTypeTwoModel
                    askPermission()
                }

            }
        }

    private fun deleteUserPostData(railModel: RailBaseItemModel) {
        playerViewAdapter.releaseAllPlayers()
        playerViewAdapter.clearMap()
        playerViewAdapter.resetCurrentPlayer()
        val position = railList.indexOf(railModel)
        railList.remove(railModel)
        if (position > -1) {
            binding.rvUserVideoList.adapter?.notifyItemRemoved(position)
        }
        Run.after(500) { binding.rvUserVideoList.adapter?.notifyDataSetChanged() }
    }

    private fun deletePost(railModel: RailBaseItemModel) {
        userPostViewModel.deleteUserPost(railModel)
            .observe(this, Observer { userPostDeleteResponse ->
                when (userPostDeleteResponse.status) {
                    TimePassBaseResult.Status.LOADING -> {
                        showProgressBar()
                    }
                    TimePassBaseResult.Status.SUCCESS -> {
                        dismissProgressBar()
                        deleteUserPostData(railModel)
                    }
                    TimePassBaseResult.Status.ERROR -> {
                        dismissProgressBar()
                        userPostDeleteResponse.message?.toast(this)
                    }
                }
            })
    }

}