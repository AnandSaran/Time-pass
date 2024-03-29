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
import android.view.*
import android.webkit.MimeTypeMap
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.SnapHelper
import com.gregantech.timepass.BuildConfig
import com.gregantech.timepass.R
import com.gregantech.timepass.adapter.handler.rail.RailItemClickHandler
import com.gregantech.timepass.adapter.live.LiveUserAdapter
import com.gregantech.timepass.adapter.rail.InstagramAdAdapter
import com.gregantech.timepass.base.TimePassBaseFragment
import com.gregantech.timepass.base.TimePassBaseResult
import com.gregantech.timepass.databinding.FragmentUserVideoListBinding
import com.gregantech.timepass.general.PostMoreOptionNavigationEnum
import com.gregantech.timepass.general.PostMoreOptionNavigationEnum.*
import com.gregantech.timepass.general.UserListScreenTitleEnum
import com.gregantech.timepass.general.UserListScreenTypeEnum
import com.gregantech.timepass.general.bundklekey.CategoryDetailBundleKeyEnum
import com.gregantech.timepass.general.bundklekey.CreateVideoBundleEnum
import com.gregantech.timepass.general.bundklekey.TikTokBundleKeyEnum
import com.gregantech.timepass.model.*
import com.gregantech.timepass.model.playback.PlaybackInfoModel
import com.gregantech.timepass.network.repository.BroadCastRepository
import com.gregantech.timepass.network.repository.FireStoreRepository
import com.gregantech.timepass.network.repository.VideoListRepository
import com.gregantech.timepass.network.repository.bridge.toRailItemTypeTwoModel
import com.gregantech.timepass.network.repository.bridge.toRailItemTypeTwoModelList
import com.gregantech.timepass.network.response.Video
import com.gregantech.timepass.util.CARD_HOME_VIDEO_LIST
import com.gregantech.timepass.util.NewPlayerViewAdapter
import com.gregantech.timepass.util.Run
import com.gregantech.timepass.util.URIPathHelper
import com.gregantech.timepass.util.constant.EMPTY_LONG
import com.gregantech.timepass.util.constant.EMPTY_STRING
import com.gregantech.timepass.util.constant.VIEW_MODEL_IN_ACCESSIBLE_MESSAGE
import com.gregantech.timepass.util.extension.*
import com.gregantech.timepass.util.log.LogUtil
import com.gregantech.timepass.util.sharedpreference.SharedPreferenceHelper
import com.gregantech.timepass.view.comment.fragment.CommentActivity
import com.gregantech.timepass.view.createvideo.activity.VideoTrimmerActivity
import com.gregantech.timepass.view.createvideo.activity.VideoUploadActivity
import com.gregantech.timepass.view.home.fragment.FilePickerBottomSheetFragment
import com.gregantech.timepass.view.live.activity.LiveVideoPlayerActivity
import com.gregantech.timepass.view.live.viewmodel.LiveBroadcastViewModel
import com.gregantech.timepass.view.live.viewmodel.LiveChatViewModel
import com.gregantech.timepass.view.player.activity.ImageViewActivity
import com.gregantech.timepass.view.profile.activity.UserProfileActivity
import com.gregantech.timepass.view.tiktok.activity.TikTokActivity
import com.gregantech.timepass.view.userlist.activity.UserListActivity
import com.gregantech.timepass.view.uservideolist.viewmodel.UserPostViewModel
import com.gregantech.timepass.view.uservideolist.viewmodel.UserVideoListViewModel
import com.gregantech.timepass.widget.PaginationScrollListener
import com.gregantech.timepass.widget.dialog.BottomSheetDialogPostMoreOption
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.yalantis.ucrop.UCrop
import java.io.File

class UserVideoListFragment : TimePassBaseFragment() {


    private lateinit var binding: FragmentUserVideoListBinding
    private lateinit var ctxt: Context
    private lateinit var viewModelFactory: UserVideoListViewModel.Factory
    private lateinit var bcViewModelFactory: LiveBroadcastViewModel.Factory
    private lateinit var userPostViewModelFactory: UserPostViewModel.Factory
    private lateinit var chatViewModelFactory: LiveChatViewModel.Factory

    private val liveUserAdapter by lazy {
        LiveUserAdapter(::onLiveUserClicked)
    }
    private lateinit var railItemClickHandler: RailItemClickHandler
    private val playerViewAdapter = NewPlayerViewAdapter()
    private var isRegistered = false
    private var railList: ArrayList<RailBaseItemModel> = arrayListOf()

    private var videoList: List<Video>? = null

    private var isLastData: Boolean = false
    private var pageNo: Int = 1

    var isLastPage: Boolean = false
    var isLoading: Boolean = false
    var railModel = RailItemTypeTwoModel()
    var currentIndex = -1
    var isShareClick = false
    var downloadID: Long? = null
    var currentPos = 0

    private val viewModel: UserVideoListViewModel by lazy {
        requireNotNull(this.activity) {
            VIEW_MODEL_IN_ACCESSIBLE_MESSAGE
        }
        ViewModelProvider(this, viewModelFactory).get(UserVideoListViewModel::class.java)
    }

    private val bcViewModel: LiveBroadcastViewModel by lazy {
        requireNotNull(this.activity) {
            VIEW_MODEL_IN_ACCESSIBLE_MESSAGE
        }
        ViewModelProvider(this, bcViewModelFactory).get(LiveBroadcastViewModel::class.java)
    }

    private val chatViewModel: LiveChatViewModel by lazy {
        requireNotNull(this) {
            VIEW_MODEL_IN_ACCESSIBLE_MESSAGE
        }
        ViewModelProvider(this, chatViewModelFactory).get(LiveChatViewModel::class.java)
    }

    private var permissionlistenerCreateVideo: PermissionListener = object : PermissionListener {
        override fun onPermissionGranted() {
            openFilePicker()
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

    private val userPostViewModel: UserPostViewModel by lazy {
        requireNotNull(this.activity) {
            VIEW_MODEL_IN_ACCESSIBLE_MESSAGE
        }
        ViewModelProvider(this, userPostViewModelFactory).get(UserPostViewModel::class.java)
    }

    companion object {
        fun newInstance() = UserVideoListFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onStart() {
        super.onStart()
        requireContext().registerReceiver(
            downloadStatusReceiver,
            IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        )
        isRegistered = true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (!::binding.isInitialized) {
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
        }
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        bcViewModelFactory = LiveBroadcastViewModel.Factory(BroadCastRepository())
        chatViewModelFactory = LiveChatViewModel.Factory(FireStoreRepository())
        if (!::viewModelFactory.isInitialized) {
            viewModelFactory =
                UserVideoListViewModel.Factory(
                    VideoListRepository(),
                    SharedPreferenceHelper
                )

            userPostViewModelFactory =
                UserPostViewModel.Factory(
                    VideoListRepository(),
                    SharedPreferenceHelper
                )
            setupViewModelObserver()
        }
    }

    private fun initView() {
        binding.rvLiveUserList.apply {
            setHasFixedSize(true)
            horizontalView(requireContext())
            adapter = liveUserAdapter

        }
    }

    override fun onStop() {
        super.onStop()
        if (isRegistered)
            requireContext().unregisterReceiver(downloadStatusReceiver)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_user_video_list, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.miSearch -> showSearchScreen()
        }
        return true
    }

    private fun showSearchScreen() {
        UserListActivity.present(
            ctxt,
            UserListScreenTitleEnum.SEARCH,
            UserListScreenTypeEnum.SEARCH
        )
    }

    private fun setupViewModelObserver() {
        viewModel.getUserVideoList(pageNo)
            .observe(viewLifecycleOwner, Observer { categoryListResponse ->
                when (categoryListResponse.status) {
                    TimePassBaseResult.Status.LOADING -> {
                    }
                    TimePassBaseResult.Status.SUCCESS -> {
                        categoryListResponse.data?.let {
                            videoList = it.video
                            isLastData = it.is_last
                            railList.addAll(
                                it.video.toRailItemTypeTwoModelList(
                                    isShowProfile = true,
                                    isShowFollow = false,
                                    advertisementName = CARD_HOME_VIDEO_LIST
                                )
                            )
                            setupRecyclerView(railList)
                        }
                    }
                    TimePassBaseResult.Status.ERROR -> {
                        categoryListResponse.message?.toast(ctxt)
                    }
                }
            })

        bcViewModel.obLiveUserList.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                TimePassBaseResult.Status.SUCCESS -> {
                    setRecyclerLiveList(it.data)
                }
                TimePassBaseResult.Status.ERROR -> it.message?.toast(ctxt)
                TimePassBaseResult.Status.LOADING -> {
                }
            }
        })

        chatViewModel.obActiveLiveCountCollection().observe(viewLifecycleOwner, Observer {
            when (it.status) {
                TimePassBaseResult.Status.SUCCESS -> if (isAdded) doFetchLiveUserList()
                TimePassBaseResult.Status.ERROR -> it.message?.toast(ctxt)
                TimePassBaseResult.Status.LOADING -> {
                }
            }
        })

        viewModel.downloadRequest.observe(viewLifecycleOwner, Observer {
            downloadVideo(it)
        })

    }

    private fun doFetchLiveUserList() {
        bcViewModel.getLiveUserList(LiveUserListRequest(SharedPreferenceHelper.getUserId()))
    }

    private fun setupRecyclerView(categoryVideoList: ArrayList<RailBaseItemModel>) {
        // binding.rvCategoryVideoList.setMediaObjects(categoryVideoList)
        binding.rvUserVideoList.apply {
            //generateRailItemDecoration(RailItemDecorationTypeEnum.TYPE_RAIL_ITEM_DECORATION_TWO)
            setHasFixedSize(true)
            adapter = InstagramAdAdapter(
                modelList = categoryVideoList,
                railItemClickHandler = railItemClickHandler,
                playerViewAdapter = playerViewAdapter,
                advertisementName = CARD_HOME_VIDEO_LIST
            )
        }
        // setUpSnapShot()
        setupRecyclerViewScrollListener()
    }

    private fun setRecyclerLiveList(data: LiveUserListResponse?) {
        if (data?.list?.isEmpty() == true)
            binding.rvLiveUserList.gone()
        else
            liveUserAdapter.refresh(data?.list)
    }

    private fun onLiveUserClicked(listItem: ListItem) {
        LiveVideoPlayerActivity.present(
            requireContext(), PlaybackInfoModel(
                "Streaming Live",
                BuildConfig.ANT_URL + listItem.streamId,
                listItem.streamId!!,
                true
            )
        )
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
                    playerViewAdapter.playIndexThenPausePreviousPlayer(index)
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
            val railModel = railModel as RailItemTypeTwoModel
            val isImage = railModel.isImage
            if (isImage != null && isImage) {
                displayImagePage(railModel.image)
            } else {
                displayPlayerPage(videoList?.get(railModel.position)!!, railModel.position)
            }
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

        railItemClickHandler.clickProfile = { railModel ->
            onClickProfile(railModel as RailItemTypeTwoModel)
        }

        railItemClickHandler.clickMore = { railModel ->
            showPostMoreDialog(railModel as RailItemTypeTwoModel)
        }
    }

    private fun onClickShare(railItemTypeTwoModel: RailItemTypeTwoModel) {
        downloadWithFlow(railItemTypeTwoModel)
    }

    private fun onClickProfile(railItemTypeTwoModel: RailItemTypeTwoModel) {
        displayUserProfilePage(railItemTypeTwoModel.followerId)
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
        if (isNotDownloaded(requireContext(), railModel.getStrippedFileName(), isShareClick))
            viewModel.createDownloadRequest(railModel, getString(R.string.app_name))
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

    private fun displayPlayerPage(video: Video?, position: Int) {
        currentPos = position
        playerViewAdapter.pauseCurrentPlayingVideo()
        startForResult.launch(
            TikTokActivity.generateIntent(
                ctxt,
                video!!,
                playerViewAdapter.getCurrentPlayerPosition(),
                "UserVideoListFragment"
            )
        )
    }

    private fun displayImagePage(imageUrl: String) {
        ImageViewActivity.present(ctxt, imageUrl)
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
                            addMoreVideoList(
                                it.video.toRailItemTypeTwoModelList(
                                    isShowProfile = true,
                                    isShowFollow = false,
                                    advertisementName = CARD_HOME_VIDEO_LIST
                                )
                            )
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
            if (downloadID == id && isShareClick) {
                requireContext().shareDownloadedFile(downloadID!!)
            }
            if (isShareClick) {
                dismissProgressBar()
            } else
                getString(R.string.download_completed).toast(requireContext())
        }
    }

    private fun downloadVideo(request: DownloadManager.Request) {
        if (isShareClick) {
            showProgressBar()
        }
        getString(R.string.download_started).toast(ctxt)
        val manager =
            requireActivity().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadID = manager.enqueue(request)
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
        playerViewAdapter.releaseAllPlayers()
    }

    val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.let {
                    val playerCurrentPosition = it.getLongExtra(
                        CategoryDetailBundleKeyEnum.VIDEO_POSITION.value,
                        EMPTY_LONG
                    )
                    it.getParcelableExtra<RailItemTypeTwoModel?>(TikTokBundleKeyEnum.VIDEO_DATA.value)
                        ?.let { vid ->
                            /*(binding.rvUserVideoList.adapter as InstagramAdAdapter).changeAtPos(
                                vid,
                                currentIndex
                            )*/
                            changePlayerCurrentPosition(playerCurrentPosition)
                        }
                }
            }
        }

    private fun changePlayerCurrentPosition(playerCurrentPosition: Long) {
        playerViewAdapter.changePlayerCurrentPosition(playerCurrentPosition)
    }

    private fun askVideoPermission() {
        TedPermission.with(ctxt)
            .setPermissionListener(permissionlistenerCreateVideo)
            .setDeniedMessage(getString(R.string.permission_denied_message))
            .setPermissions(
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

    private fun displayVideoUploadPage(videoPath: String, isImage: Boolean = false) {
        if (videoPath.isNotBlank()) {
            startUploadVideoActivityForResult.launch(
                VideoUploadActivity.generateIntent(ctxt, videoPath, isImage)
            )
        } else {
            getString(R.string.invalid_file).toast(ctxt)
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
            binding.rvUserVideoList.smoothSnapToPosition(positionAdd)
        }
    }

    private fun displayUserProfilePage(followerId: String) {
        UserProfileActivity.present(ctxt, followerId)
    }

    fun onFilePickItemClick(item: String) {
        when (item) {
            getString(R.string.image) -> {
                callGalleryPic()
            }
            getString(R.string.video) -> {
                callVideoPic()
            }
            else -> {
            }
        }
    }

    private fun callGalleryPic() {
        val intent: Intent =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        imagePicActivityResultLauncher.launch(intent)
    }

    private var imagePicActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.let { openCropImageScreen(it) }
        }
    }

    private fun openCropImageScreen(intent: Intent) {
        LogUtil.print(TAG, "File Path: " + intent.data)
        intent.data?.let { data ->
            activity?.let {
                UCrop.of(
                    data,
                    Uri.fromFile(
                        File(
                            ctxt.cacheDir,
                            System.currentTimeMillis().toString() + ".png"
                        )
                    )
                ).start(it, this)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            UCrop.REQUEST_CROP -> {
                if (resultCode == AppCompatActivity.RESULT_OK) {
                    data?.let {
                        val resultUri = UCrop.getOutput(data)
                        LogUtil.print(TAG, "File Path: " + resultUri)
                        displayVideoUploadPage(resultUri.toString(), isImage = true)
                    }
                }
            }
        }
    }

    fun openFilePicker() {
        childFragmentManager.let {
            FilePickerBottomSheetFragment.newInstance(Bundle()).apply {
                show(it, tag)
            }
        }
    }

    private fun showPostMoreDialog(railModel: RailItemTypeTwoModel) {
        BottomSheetDialogPostMoreOption(
            ctxt,
            userPostViewModel.generatePostMoreOptionContentModel(railModel),
            onPostMoreOptionClick(),
            railModel
        ).show()
    }

    private fun onPostMoreOptionClick(): (Any, PostMoreOptionNavigationEnum) -> Unit =
        { data: Any, postMoreOptionNavigationEnum: PostMoreOptionNavigationEnum ->
            val railModel = data as RailBaseItemModel
            when (postMoreOptionNavigationEnum) {
                NAVIGATION_EDIT -> {

                }
                NAVIGATION_DELETE -> {
                    deletePost(railModel)
                }
                NAVIGATION_DOWNLOAD -> {
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
            .observe(viewLifecycleOwner, Observer { userPostDeleteResponse ->
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
                        userPostDeleteResponse.message?.toast(ctxt)
                    }
                }
            })
    }
}