package com.gregantech.timepass.view.categoryvideosearch.activity

import android.Manifest
import android.app.Activity
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.gregantech.timepass.R
import com.gregantech.timepass.adapter.handler.rail.RailItemClickHandler
import com.gregantech.timepass.adapter.rail.InstagramAdapter
import com.gregantech.timepass.base.TimePassBaseActivity
import com.gregantech.timepass.base.TimePassBaseResult
import com.gregantech.timepass.databinding.ActivitySearchVideoBinding
import com.gregantech.timepass.general.PostMoreOptionNavigationEnum
import com.gregantech.timepass.general.UserListScreenTitleEnum
import com.gregantech.timepass.general.UserListScreenTypeEnum
import com.gregantech.timepass.general.bundklekey.CategoryDetailBundleKeyEnum
import com.gregantech.timepass.general.bundklekey.UserListActivityBundleKeyEnum
import com.gregantech.timepass.model.RailBaseItemModel
import com.gregantech.timepass.model.RailItemTypeTwoModel
import com.gregantech.timepass.model.getStrippedFileName
import com.gregantech.timepass.network.repository.VideoListRepository
import com.gregantech.timepass.network.repository.bridge.toRailItemTypeTwoModelList
import com.gregantech.timepass.util.NewPlayerViewAdapter
import com.gregantech.timepass.util.Run
import com.gregantech.timepass.util.constant.EMPTY_LONG
import com.gregantech.timepass.util.constant.VIEW_MODEL_IN_ACCESSIBLE_MESSAGE
import com.gregantech.timepass.util.extension.shareDownloadedFile
import com.gregantech.timepass.util.extension.toast
import com.gregantech.timepass.util.sharedpreference.SharedPreferenceHelper
import com.gregantech.timepass.view.comment.fragment.CommentActivity
import com.gregantech.timepass.view.player.activity.ImageViewActivity
import com.gregantech.timepass.view.player.activity.PlayerActivity
import com.gregantech.timepass.view.uservideolist.viewmodel.UserPostViewModel
import com.gregantech.timepass.view.uservideolist.viewmodel.UserVideoListViewModel
import com.gregantech.timepass.widget.PaginationScrollListener
import com.gregantech.timepass.widget.dialog.BottomSheetDialogPostMoreOption
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission

class SearchVideoActivity : TimePassBaseActivity() {


    var currentIndex = -1
    var downloadID: Long? = null
    var isRegistered = false
    var isShareClick = false
    var isLastPage: Boolean = false
    var isLoading: Boolean = false

    private lateinit var binding: ActivitySearchVideoBinding

    var railModel = RailItemTypeTwoModel()
    private lateinit var railItemClickHandler: RailItemClickHandler
    private var railList: ArrayList<RailBaseItemModel> = arrayListOf()

    private val playerViewAdapter = NewPlayerViewAdapter()
    private lateinit var viewModelFactory: UserVideoListViewModel.Factory
    private lateinit var userPostViewModelFactory: UserPostViewModel.Factory

    private val viewModel: UserVideoListViewModel by lazy {
        requireNotNull(this) {
            VIEW_MODEL_IN_ACCESSIBLE_MESSAGE
        }
        ViewModelProvider(this, viewModelFactory).get(UserVideoListViewModel::class.java)
    }

    private val userPostViewModel: UserPostViewModel by lazy {
        requireNotNull(this) {
            VIEW_MODEL_IN_ACCESSIBLE_MESSAGE
        }
        ViewModelProvider(this, userPostViewModelFactory).get(UserPostViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initDataBinding()
        setupToolBar()
        setupViewModelFactory()
        setupViewModelObserver()
        onClickRailListItem()
        setupRecyclerView(railList)
        setupSearchTextWatcher()
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

    private val downloadStatusReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (downloadID == id && isShareClick) {
                shareDownloadedFile(downloadID!!)
            }
            if (isShareClick) {
                dismissProgressBar()
            } else
                getString(R.string.download_completed).toast(this@SearchVideoActivity)
        }
    }

    private fun initDataBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search_video)
        binding.tilSearchUser.editText?.requestFocus()
    }

    private fun setupToolBar() {
        setSupportActionBar(binding.tbVideoList.toolbar)
        supportActionBar?.apply {
            title = getString(R.string.search)
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
    }

    private fun setupViewModelFactory() {
        viewModelFactory =
            UserVideoListViewModel.Factory(VideoListRepository(), SharedPreferenceHelper)
        userPostViewModelFactory =
            UserPostViewModel.Factory(VideoListRepository(), SharedPreferenceHelper)
        setupViewModelObserver()
    }

    private fun setupViewModelObserver() {
        viewModel.downloadRequest.observe(this, Observer {
            downloadVideo(it)
        })
    }

    private fun setupSearchTextWatcher() {
        binding.tilSearchUser.editText?.doOnTextChanged { searchKey, start, before, count ->
            if (searchKey.toString().isEmpty())
                exitSearchMode()
            else fetchVideoList(searchKey.toString())
        }
    }

    private fun exitSearchMode() {
        railList.clear()
        playerViewAdapter.run {
            releaseAllPlayers()
            clearMap()
            resetCurrentPlayer()
        }
        binding.rvSearchVid.adapter?.notifyDataSetChanged()
    }

    private fun downloadVideo(request: DownloadManager.Request) {
        if (isShareClick) {
            showProgressBar()
        }
        getString(R.string.download_started).toast(this)
        val manager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadID = manager.enqueue(request)
    }


    private fun fetchVideoList(searchKey: String) {

        viewModel.getSearchVideoList(searchKey).observe(this, Observer { resultOf ->
            when (resultOf.status) {
                TimePassBaseResult.Status.LOADING -> {
                    Log.d(TAG, "fetchSearchUserList: loading")
                }
                TimePassBaseResult.Status.SUCCESS -> {

                    if (binding.tilSearchUser.editText?.text?.isEmpty() == true) {
                        exitSearchMode()
                        return@Observer
                    }

                    resultOf.data?.let {
                        railList.apply {
                            clear()
                            addAll(it.video.toRailItemTypeTwoModelList(isShowProfile = false))
                        }
                        playerViewAdapter.run {
                            releaseAllPlayers()
                            clearMap()
                            resetCurrentPlayer()
                        }
                        binding.rvSearchVid.apply {
                            smoothScrollToPosition(0)
                            adapter?.notifyDataSetChanged()
                        }
                    }
                }
                TimePassBaseResult.Status.ERROR -> {
                    Log.d(TAG, "fetchSearchUserList: error")
                    resultOf.message?.toast(this)
                }
            }
        })
    }

    private fun setupRecyclerView(categoryVideoList: ArrayList<RailBaseItemModel>) {
        binding.rvSearchVid.apply {
            setHasFixedSize(true)
            adapter = InstagramAdapter(
                modelList = categoryVideoList,
                railItemClickHandler = railItemClickHandler,
                playerViewAdapter = playerViewAdapter
            )
        }
        setupRecyclerViewScrollListener()
    }

    private fun setupRecyclerViewScrollListener() {
        binding.rvSearchVid.addOnScrollListener(object :
            PaginationScrollListener(binding.rvSearchVid.layoutManager as LinearLayoutManager) {
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
            }
        })
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

    }

    private fun setVideoLike(railItemTypeTwoModel: RailItemTypeTwoModel) {
        viewModel.setVideoLike(railItemTypeTwoModel.contentId, railItemTypeTwoModel.isLiked)
            .observe(this,
                Observer {
                })
    }

    private fun showCommentPage(railItemTypeTwoModel: RailItemTypeTwoModel) {
        CommentActivity.present(
            this,
            railItemTypeTwoModel.contentId, isUserPost = true
        )
    }

    private fun onClickComment(railItemTypeTwoModel: RailItemTypeTwoModel) {
        showCommentPage(railItemTypeTwoModel)
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

    private fun deleteUserPostData(railModel: RailBaseItemModel) {
        playerViewAdapter.releaseAllPlayers()
        playerViewAdapter.clearMap()
        playerViewAdapter.resetCurrentPlayer()
        val position = railList.indexOf(railModel)
        railList.remove(railModel)
        if (position > -1) {
            binding.rvSearchVid.adapter?.notifyItemRemoved(position)
        }
        Run.after(500) { binding.rvSearchVid.adapter?.notifyDataSetChanged() }
    }

    private var permissionListener: PermissionListener = object : PermissionListener {
        override fun onPermissionGranted() {
            onClickDownload()
        }

        override fun onPermissionDenied(deniedPermissions: List<String>) {
        }
    }

    private fun onClickDownload() {
        if (isNotDownloaded(railModel.getStrippedFileName(), isShareClick))
            viewModel.createDownloadRequest(railModel, getString(R.string.app_name))
    }

    companion object {
        fun present(
            context: Context,
            title: UserListScreenTitleEnum,
            screenType: UserListScreenTypeEnum
        ) {
            val intent = Intent(context, SearchVideoActivity::class.java)
            intent.putExtra(UserListActivityBundleKeyEnum.TITLE.value, title.value)
            intent.putExtra(UserListActivityBundleKeyEnum.SCREEN_TYPE.value, screenType.value)
            context.startActivity(intent)
        }
    }

}