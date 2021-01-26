package com.gregantech.timepass.view.profile.fragment

import android.Manifest
import android.app.Activity
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
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
import com.gregantech.timepass.databinding.FragmentProfileBinding
import com.gregantech.timepass.general.bundklekey.CategoryDetailBundleKeyEnum
import com.gregantech.timepass.model.RailBaseItemModel
import com.gregantech.timepass.model.RailItemTypeTwoModel
import com.gregantech.timepass.network.repository.LoginRepository
import com.gregantech.timepass.network.repository.VideoListRepository
import com.gregantech.timepass.network.repository.bridge.toRailItemTypeTwoModelList
import com.gregantech.timepass.util.PlayerViewAdapter
import com.gregantech.timepass.util.PlayerViewAdapter.Companion.playIndexThenPausePreviousPlayer
import com.gregantech.timepass.util.constant.EMPTY_LONG
import com.gregantech.timepass.util.constant.VIEW_MODEL_IN_ACCESSIBLE_MESSAGE
import com.gregantech.timepass.util.extension.*
import com.gregantech.timepass.util.sharedpreference.SharedPreferenceHelper
import com.gregantech.timepass.view.comment.fragment.CommentActivity
import com.gregantech.timepass.view.player.activity.PlayerActivity
import com.gregantech.timepass.view.profile.activity.ProfileActivity
import com.gregantech.timepass.view.profile.viewmodel.ProfileFragmentViewModel
import com.gregantech.timepass.widget.PaginationScrollListener
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission


/**
 * A simple [Fragment] subclass.
 */
class ProfileFragment : TimePassBaseFragment() {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var ctxt: Context
    private var sharedPreferenceHelper = SharedPreferenceHelper
    private lateinit var viewModelFactory: ProfileFragmentViewModel.Factory
    private lateinit var railItemClickHandler: RailItemClickHandler
    private var railList: ArrayList<RailBaseItemModel> = arrayListOf()

    var isShareClick = false
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

    private val viewModel: ProfileFragmentViewModel by lazy {
        requireNotNull(this.activity) {
            VIEW_MODEL_IN_ACCESSIBLE_MESSAGE
        }
        ViewModelProvider(this, viewModelFactory).get(ProfileFragmentViewModel::class.java)
    }

    companion object {
        fun newInstance() = ProfileFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (!::binding.isInitialized) {
            binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_profile,
                container,
                false
            )
            context?.let {
                ctxt = it
            }

        }
        onClickRailListItem()
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        requireContext().registerReceiver(
            downloadStatusReceiver,
            IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        )
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_user_profile, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.miEdi -> showProfileScreen()
        }
        return true
    }

    private fun showProfileScreen() {
        ProfileActivity.present(ctxt)
    }

    override fun onResume() {
        super.onResume()
        fetchLogin()
        if (currentIndex != -1) {
            playIndexThenPausePreviousPlayer(currentIndex)
        }
    }

    private fun fetchLogin() {
        viewModel.fetchLogin().observe(this, Observer {
            when (it.status) {
                TimePassBaseResult.Status.SUCCESS -> {
                    dismissProgressBar()
                    it.data?.user?.let { user ->
                        sharedPreferenceHelper.setUserData(user)
                        setUserData()
                    }
                }
                TimePassBaseResult.Status.LOADING -> {
                }
                else -> {
                }
            }
        })
    }

    private fun setUserData() {
        binding.ivProfilePicture.loadUrlCircle(
            sharedPreferenceHelper.getUserProfileImage(),
            R.drawable.place_holder_profile
        )
        binding.tvName.text = sharedPreferenceHelper.getUserName()
        binding.tvTotalPost.text = sharedPreferenceHelper.getTotalPost().appendPost()
        binding.tvTotalFollowers.text = sharedPreferenceHelper.getFollowers().appendFollowers()
        binding.tvTotalFollowing.text = sharedPreferenceHelper.getFollowing().appendFollowing()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModelFactory =
            ProfileFragmentViewModel.Factory(
                VideoListRepository(),
                LoginRepository(),
                SharedPreferenceHelper
            )
        setupViewModelObserver()
        setUserData()
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
            it.let {
                downloadVideo(it)
            }
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

    private fun onClickShare(railItemTypeTwoModel: RailItemTypeTwoModel) {
        //ctxt.shareVideoText(railItemTypeTwoModel.video)
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
        binding.rvUserVideoList.adapter?.notifyItemRangeInserted(startPosition, endPosition)
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
        PlayerViewAdapter.pauseCurrentPlayingVideo()
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

    private fun onClickComment(railItemTypeTwoModel: RailItemTypeTwoModel) {
        showCommentPage(railItemTypeTwoModel)
    }

    private fun showCommentPage(railItemTypeTwoModel: RailItemTypeTwoModel) {
        CommentActivity.present(
            ctxt,
            railItemTypeTwoModel.contentId, isUserPost = true
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        requireContext().unregisterReceiver(downloadStatusReceiver)
    }
}