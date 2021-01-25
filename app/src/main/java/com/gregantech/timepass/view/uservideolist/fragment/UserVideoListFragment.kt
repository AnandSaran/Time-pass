package com.gregantech.timepass.view.uservideolist.fragment

import android.Manifest
import android.app.Activity
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.SnapHelper
import com.gregantech.timepass.R
import com.gregantech.timepass.adapter.handler.rail.RailItemClickHandler
import com.gregantech.timepass.adapter.rail.InstagramAdapter
import com.gregantech.timepass.base.TimePassBaseFragment
import com.gregantech.timepass.base.TimePassBaseResult
import com.gregantech.timepass.databinding.FragmentUserVideoListBinding
import com.gregantech.timepass.general.bundklekey.CategoryDetailBundleKeyEnum
import com.gregantech.timepass.general.bundklekey.CreateVideoBundleEnum
import com.gregantech.timepass.model.RailBaseItemModel
import com.gregantech.timepass.model.RailItemTypeTwoModel
import com.gregantech.timepass.network.repository.VideoListRepository
import com.gregantech.timepass.network.repository.bridge.toRailItemTypeTwoModel
import com.gregantech.timepass.network.repository.bridge.toRailItemTypeTwoModelList
import com.gregantech.timepass.network.response.Video
import com.gregantech.timepass.util.PlayerViewAdapter
import com.gregantech.timepass.util.PlayerViewAdapter.Companion.pauseCurrentPlayingVideo
import com.gregantech.timepass.util.PlayerViewAdapter.Companion.playIndexThenPausePreviousPlayer
import com.gregantech.timepass.util.URIPathHelper
import com.gregantech.timepass.util.constant.EMPTY_LONG
import com.gregantech.timepass.util.constant.EMPTY_STRING
import com.gregantech.timepass.util.constant.VIEW_MODEL_IN_ACCESSIBLE_MESSAGE
import com.gregantech.timepass.util.extension.shareDownloadedFile
import com.gregantech.timepass.util.extension.toast
import com.gregantech.timepass.util.sharedpreference.SharedPreferenceHelper
import com.gregantech.timepass.view.comment.fragment.CommentActivity
import com.gregantech.timepass.view.createvideo.activity.VideoTrimmerActivity
import com.gregantech.timepass.view.createvideo.activity.VideoUploadActivity
import com.gregantech.timepass.view.player.activity.PlayerActivity
import com.gregantech.timepass.view.profile.activity.UserProfileActivity
import com.gregantech.timepass.view.uservideolist.viewmodel.UserVideoListViewModel
import com.gregantech.timepass.widget.PaginationScrollListener
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission

class UserVideoListFragment : TimePassBaseFragment() {
    private lateinit var binding: FragmentUserVideoListBinding
    private lateinit var ctxt: Context
    private lateinit var viewModelFactory: UserVideoListViewModel.Factory
    private lateinit var railItemClickHandler: RailItemClickHandler

    private var railList: ArrayList<RailBaseItemModel> = arrayListOf()

    private var isLastData: Boolean = false
    private var pageNo: Int = 1

    var isLastPage: Boolean = false
    var isLoading: Boolean = false
    var railModel = RailItemTypeTwoModel()
    var currentIndex = -1
    var isShareClick = false
    var downloadID: Long? = null

    private var permissionlistenerCreateVideo: PermissionListener = object : PermissionListener {
        override fun onPermissionGranted() {
            callVideoPic()
        }

        override fun onPermissionDenied(deniedPermissions: List<String>) {
        }
    }

    private var permissionListener: PermissionListener = object : PermissionListener {
        override fun onPermissionGranted() {
            onClickDownload()
        }

        override fun onPermissionDenied(deniedPermissions: List<String>) {
        }
    }

    private val viewModel: UserVideoListViewModel by lazy {
        requireNotNull(this.activity) {
            VIEW_MODEL_IN_ACCESSIBLE_MESSAGE
        }
        ViewModelProvider(this, viewModelFactory).get(UserVideoListViewModel::class.java)
    }

    companion object {
        fun newInstance() = UserVideoListFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireContext().registerReceiver(
            downloadStatusReceiver,
            IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_user_video_list,
            container,
            false
        )
        context?.let {
            ctxt = it
        }

        onClickCreateVideo()
        onClickRailListItem()
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModelFactory =
            UserVideoListViewModel.Factory(
                VideoListRepository(),
                SharedPreferenceHelper
            )
        setupViewModelObserver()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        requireContext().unregisterReceiver(downloadStatusReceiver)
    }

    private fun setupViewModelObserver() {
        viewModel.getUserVideoList(pageNo)
            .observe(viewLifecycleOwner, Observer { categoryListResponse ->
                when (categoryListResponse.status) {
                    TimePassBaseResult.Status.LOADING -> {
                    }
                    TimePassBaseResult.Status.SUCCESS -> {
                        categoryListResponse.data?.let {
                            isLastData = it.is_last
                            railList.addAll(it.video.toRailItemTypeTwoModelList(isShowProfile = true))
                            setupRecyclerView(railList)
                        }
                    }
                    TimePassBaseResult.Status.ERROR -> {
                        categoryListResponse.message?.toast(ctxt)
                    }
                }
            })
        viewModel.downloadRequest.observe(viewLifecycleOwner, Observer {
            downloadVideo(it)
        })

    }

    private fun setupRecyclerView(categoryVideoList: ArrayList<RailBaseItemModel>) {
        // binding.rvCategoryVideoList.setMediaObjects(categoryVideoList)
        binding.rvUserVideoList.apply {
            //generateRailItemDecoration(RailItemDecorationTypeEnum.TYPE_RAIL_ITEM_DECORATION_TWO)
            setHasFixedSize(true)
            adapter = InstagramAdapter(
                modelList = categoryVideoList,
                railItemClickHandler = railItemClickHandler
            )
        }
        // setUpSnapShot()
        setupRecyclerViewScrollListener()
    }

    private fun setUpSnapShot() {
        val snapHelper: SnapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(binding.rvUserVideoList)
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

    private fun onClickCreateVideo() {
        binding.fabCreateVideo.setOnClickListener {
            askVideoPermission()
        }
    }

    private fun onClickRailListItem() {
        railItemClickHandler = RailItemClickHandler()
        railItemClickHandler.clickPoster = { railModel ->
            displayPlayerPage((railModel as RailItemTypeTwoModel).video)
        }
        railItemClickHandler.clickFollow = { railModel ->
            onClickFollow(railModel as RailItemTypeTwoModel)
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

        railItemClickHandler.clickDownload = { railModel ->
            isShareClick = false
            this.railModel = railModel as RailItemTypeTwoModel
            askPermission()
        }
        railItemClickHandler.clickProfile = { railModel ->
            onClickProfile(railModel as RailItemTypeTwoModel)
        }
    }

    private fun onClickShare(railItemTypeTwoModel: RailItemTypeTwoModel) {
        downloadWithFlow(railItemTypeTwoModel)
    }

    private fun onClickProfile(railItemTypeTwoModel: RailItemTypeTwoModel) {
        dispalyUserProfilePage(railItemTypeTwoModel.followerId)
    }

    private fun onClickFollow(railItemTypeTwoModel: RailItemTypeTwoModel) {
        when (railItemTypeTwoModel.isFollowed) {
            true -> {
                railItemTypeTwoModel.isFollowed = false
            }
            false -> {
                railItemTypeTwoModel.isFollowed = true
            }
        }

        setUserFollow(railItemTypeTwoModel)
        //  notifyDataPosition(railList.indexOf(railItemTypeTwoModel), railItemTypeTwoModel)

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
            ctxt,
            railItemTypeTwoModel.contentId, isUserPost = true
        )
    }

    private fun onClickDownload() {
        if (isShareClick) {

            viewModel.createDownloadRequest(railModel, getString(R.string.app_name))

            // onClickShare(railModel)
        } else {
            viewModel.createDownloadRequest(railModel, getString(R.string.app_name))
        }
    }

    private fun setUserFollow(railItemTypeTwoModel: RailItemTypeTwoModel) {
        viewModel.setUserFollow(railItemTypeTwoModel.isFollowed, railItemTypeTwoModel.followerId)
            .observe(viewLifecycleOwner,
                Observer {
                })
    }

    private fun setVideoLike(railItemTypeTwoModel: RailItemTypeTwoModel) {
        viewModel.setVideoLike(railItemTypeTwoModel.contentId, railItemTypeTwoModel.isLiked)
            .observe(viewLifecycleOwner,
                Observer {
                })
    }

    private fun displayPlayerPage(videoUrl: String) {
        startForResult.launch(
            PlayerActivity.generateIntent(
                ctxt,
                videoUrl,
                PlayerViewAdapter.getCurrentPlayerPosition()
            )
        )
    }

    private fun getMoreCategoryVideo() {
        if (isLastData) return
        viewModel.getMoreCategoryVideoList(++pageNo)
            .observe(viewLifecycleOwner, Observer { categoryListResponse ->
                when (categoryListResponse.status) {
                    TimePassBaseResult.Status.LOADING -> {
                    }
                    TimePassBaseResult.Status.SUCCESS -> {
                        isLoading = false

                        categoryListResponse.data?.let {
                            isLastData = it.is_last
                            addMoreVideoList(it.video.toRailItemTypeTwoModelList(isShowProfile = true))
                        }
                    }
                    TimePassBaseResult.Status.ERROR -> {
                        categoryListResponse.message?.toast(ctxt)
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

    private val downloadStatusReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (downloadID == id) {
                requireContext().shareDownloadedFile(downloadID!!)
            }
            dismissProgressBar()
        }
    }

    private fun downloadVideo(request: DownloadManager.Request) {
        showProgressBar()
        getString(R.string.download_started).toast(ctxt)
        val manager = requireActivity().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadID = manager.enqueue(request)
    }

    override fun onPause() {
        super.onPause()
        pauseCurrentPlayingVideo()
    }

    override fun onResume() {
        super.onResume()
        if (currentIndex != -1) {
            playIndexThenPausePreviousPlayer(currentIndex)
        }
    }

    private fun askPermission() {
        TedPermission.with(ctxt)
            .setPermissionListener(permissionListener)
            .setDeniedMessage(getString(R.string.permission_denied_message))
            .setPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .check()
    }

    override fun onDestroy() {
        super.onDestroy()
        PlayerViewAdapter.releaseAllPlayers()
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

    private fun askVideoPermission() {
        TedPermission.with(ctxt)
            .setPermissionListener(permissionlistenerCreateVideo)
            .setDeniedMessage(getString(R.string.permission_denied_message))
            .setPermissions(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .check()
    }

    private fun callVideoPic() {
        val intent: Intent =
            Intent(Intent.ACTION_GET_CONTENT, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
        intent.type = "video/*"
        videoPicActivityResultLauncher.launch(intent)
    }

    private var videoPicActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                displayCreateVideoPage(uri)
            }
        }
    }

    private fun displayCreateVideoPage(uri: Uri) {

        val path = URIPathHelper().getPath(ctxt, uri)

        val fileExt = MimeTypeMap.getFileExtensionFromUrl(uri.toString())
        if (path != null && path.isNotBlank()) {
            startCreateVideoForResult.launch(
                VideoTrimmerActivity.generateIntent(ctxt, path.toString())
            )
        } else {
            (getString(R.string.file_format) + " ," + fileExt).toast(ctxt)
        }
    }

    private val startCreateVideoForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.let { intent ->
                    val videoPath: String =
                        intent.extras?.getString("INTENT_VIDEO_FILE") ?: EMPTY_STRING
                    displayVideoUploadPage(videoPath)
                }
            }
        }

    private fun displayVideoUploadPage(videoPath: String) {
        if (videoPath.isNotBlank()) {
            startUploadVideoActivityForResult.launch(
                VideoUploadActivity.generateIntent(ctxt, videoPath)
            )
        } else {
            getString(R.string.invalid_video).toast(ctxt)
        }
    }

    private val startUploadVideoActivityForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.let {
                    val video =
                        it.getParcelableExtra<Video>(CreateVideoBundleEnum.VIDEO_RESPONSE.value)
                    addVideoToList(video)
                }
            }
        }

    private fun addVideoToList(video: Video?) {
        video?.let {
            val positionAdd = 0
            railList.add(positionAdd, it.toRailItemTypeTwoModel())
            binding.rvUserVideoList.adapter?.notifyItemInserted(positionAdd)
            binding.rvUserVideoList.smoothScrollToPosition(positionAdd)
        }
    }

    private fun dispalyUserProfilePage(followerId: String) {
        UserProfileActivity.present(ctxt, followerId)
    }

}