package com.gregantech.timepass.view.categorydetail.fragment

import android.Manifest
import android.app.Activity
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.DOWNLOAD_SERVICE
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.gregantech.timepass.databinding.FragmentCategoryDetailBinding
import com.gregantech.timepass.general.bundklekey.CategoryDetailBundleKeyEnum
import com.gregantech.timepass.model.RailBaseItemModel
import com.gregantech.timepass.model.RailItemTypeTwoModel
import com.gregantech.timepass.network.repository.VideoListRepository
import com.gregantech.timepass.network.repository.bridge.toRailItemTypeTwoModelList
import com.gregantech.timepass.util.PlayerViewAdapter
import com.gregantech.timepass.util.PlayerViewAdapter.Companion.getCurrentPlayerPosition
import com.gregantech.timepass.util.PlayerViewAdapter.Companion.pauseCurrentPlayingVideo
import com.gregantech.timepass.util.PlayerViewAdapter.Companion.playIndexThenPausePreviousPlayer
import com.gregantech.timepass.util.PlayerViewAdapter.Companion.releaseAllPlayers
import com.gregantech.timepass.util.constant.EMPTY_LONG
import com.gregantech.timepass.util.constant.EMPTY_STRING
import com.gregantech.timepass.util.constant.VIEW_MODEL_IN_ACCESSIBLE_MESSAGE
import com.gregantech.timepass.util.extension.shareDownloadedFile
import com.gregantech.timepass.util.extension.shareVideoText
import com.gregantech.timepass.util.extension.toast
import com.gregantech.timepass.util.sharedpreference.SharedPreferenceHelper
import com.gregantech.timepass.view.categorydetail.viewmodel.CategoryDetailFragmentViewModel
import com.gregantech.timepass.view.comment.fragment.CommentActivity
import com.gregantech.timepass.view.player.activity.PlayerActivity
import com.gregantech.timepass.widget.PaginationScrollListener
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission


class CategoryDetailFragment : TimePassBaseFragment() {
    private lateinit var ctxt: Context
    private lateinit var binding: FragmentCategoryDetailBinding
    private lateinit var viewModelFactory: CategoryDetailFragmentViewModel.Factory

    private lateinit var railItemClickHandler: RailItemClickHandler

    private var railList: ArrayList<RailBaseItemModel> = arrayListOf()

    var isShareClick = false
    private var categoryId: String = EMPTY_STRING
    private var isLastData: Boolean = false
    private var pageNo: Int = 1

    var isLastPage: Boolean = false
    var isLoading: Boolean = false
    var railModel = RailItemTypeTwoModel()
    var currentIndex = -1
    var downloadID: Long? = null
    private var permissionListener: PermissionListener = object : PermissionListener {
        override fun onPermissionGranted() {
            onClickDownload()
        }

        override fun onPermissionDenied(deniedPermissions: List<String>) {
        }
    }

    private val viewModel: CategoryDetailFragmentViewModel by lazy {
        requireNotNull(this.activity) {
            VIEW_MODEL_IN_ACCESSIBLE_MESSAGE
        }
        ViewModelProvider(this, viewModelFactory).get(CategoryDetailFragmentViewModel::class.java)
    }

    companion object {
        fun newInstance() =
            CategoryDetailFragment()
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
            R.layout.fragment_category_detail,
            container,
            false
        )
        context?.let {
            ctxt = it
        }

        onClickRailListItem()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModelFactory =
            CategoryDetailFragmentViewModel.Factory(
                VideoListRepository(),
                SharedPreferenceHelper
            )
        arguments?.apply {
            categoryId = getString(CategoryDetailBundleKeyEnum.CATEGORY_ID.value) ?: EMPTY_STRING
        }
        setupViewModelObserver()

    }

    private fun setupViewModelObserver() {
        viewModel.getCategoryVideoList(categoryId, pageNo)
            .observe(viewLifecycleOwner, Observer { categoryListResponse ->
                when (categoryListResponse.status) {
                    TimePassBaseResult.Status.LOADING -> {
                    }
                    TimePassBaseResult.Status.SUCCESS -> {
                        categoryListResponse.data?.let {
                            isLastData = it.is_last
                            railList.addAll(it.video.toRailItemTypeTwoModelList(false))
                            setupRecyclerView(railList)
                        }
                    }
                    TimePassBaseResult.Status.ERROR -> {
                        categoryListResponse.message?.toast(ctxt)
                    }
                }
            })
        viewModel.downloadRequest.observe(viewLifecycleOwner, Observer {
            it?.let {
                downloadVideo(it)
            }
        })
    }

    private fun setupRecyclerView(categoryVideoList: ArrayList<RailBaseItemModel>) {
        // binding.rvCategoryVideoList.setMediaObjects(categoryVideoList)
        binding.rvCategoryVideoList.apply {
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
        snapHelper.attachToRecyclerView(binding.rvCategoryVideoList)
    }

    private fun setupRecyclerViewScrollListener() {
        binding.rvCategoryVideoList.addOnScrollListener(object :
            PaginationScrollListener(binding.rvCategoryVideoList.layoutManager as LinearLayoutManager) {
            override fun isLastPage(): Boolean {
                return isLastPage
            }

            override fun isLoading(): Boolean {
                return isLoading
            }

            override fun onItemIsFirstVisibleItem(index: Int) {
                currentIndex = index
                if (index != -1) {
                    playIndexThenPausePreviousPlayer(index)
                }
            }

            override fun loadMoreItems() {
                isLoading = true
                getMoreCategoryVideo()
            }
        })
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

        railItemClickHandler.clickShare = { railModel ->
            isShareClick = true
            onClickShare(railModel as RailItemTypeTwoModel)
        }
        railItemClickHandler.clickComment = { railModel ->
            onClickComment(railModel as RailItemTypeTwoModel)
        }
        railItemClickHandler.clickDownload = { railModel ->
            isShareClick = false
            this.railModel = railModel as RailItemTypeTwoModel
            askPermission()
        }
    }

    private val downloadStatusReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (downloadID == id && isShareClick) {
                requireContext().shareDownloadedFile(downloadID!!)
            }
            if(isShareClick){
                dismissProgressBar()
            }
        }
    }

    private fun onClickShare(railItemTypeTwoModel: RailItemTypeTwoModel) {
        railModel = railItemTypeTwoModel
        onClickDownload()
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

    private fun onClickDownload() {
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

    private fun notifyDataPosition(index: Int, railItemTypeTwoModel: RailItemTypeTwoModel) {
        binding.rvCategoryVideoList.adapter?.notifyItemChanged(index, railItemTypeTwoModel)
    }

    private fun displayPlayerPage(videoUrl: String) {
        startForResult.launch(
            PlayerActivity.generateIntent(
                ctxt,
                videoUrl,
                getCurrentPlayerPosition()
            )
        )
    }

    private fun getMoreCategoryVideo() {
        if (isLastData) return
        viewModel.getMoreCategoryVideoList(categoryId, ++pageNo)
            .observe(viewLifecycleOwner, Observer { categoryListResponse ->
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
                        categoryListResponse.message?.toast(ctxt)
                    }
                }
            })

    }

    private fun addMoreVideoList(newList: ArrayList<RailBaseItemModel>) {
        val startPosition = railList.size
        val endPosition = railList.size + newList.size
        railList.addAll(newList)
        binding.rvCategoryVideoList.adapter?.notifyItemRangeInserted(startPosition, endPosition)
    }

    private fun downloadVideo(request: DownloadManager.Request) {
        if(isShareClick){
            showProgressBar()
        }
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
        releaseAllPlayers()
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

    private fun onClickComment(railItemTypeTwoModel: RailItemTypeTwoModel) {
        showCommentPage(railItemTypeTwoModel)
    }

    private fun showCommentPage(railItemTypeTwoModel: RailItemTypeTwoModel) {
        CommentActivity.present(
            ctxt,
            railItemTypeTwoModel.contentId, isAdminPost = true
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        requireContext().unregisterReceiver(downloadStatusReceiver)
    }
}