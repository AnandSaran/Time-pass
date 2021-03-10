package com.gregantech.timepass.view.profile.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.gregantech.timepass.R
import com.gregantech.timepass.adapter.handler.rail.RailItemClickHandler
import com.gregantech.timepass.adapter.rail.RailAdapter
import com.gregantech.timepass.base.TimePassBaseActivity
import com.gregantech.timepass.base.TimePassBaseResult
import com.gregantech.timepass.databinding.ActivityUserProfileBinding
import com.gregantech.timepass.general.bundklekey.UserProfileActivityBundleKeyEnum
import com.gregantech.timepass.model.RailBaseItemModel
import com.gregantech.timepass.network.repository.ProfileRepository
import com.gregantech.timepass.network.repository.VideoListRepository
import com.gregantech.timepass.network.repository.bridge.toRailItemTypeThreeModelList
import com.gregantech.timepass.network.repository.local.UserProfileScreenRepository
import com.gregantech.timepass.network.response.User
import com.gregantech.timepass.network.response.Video
import com.gregantech.timepass.util.constant.VIEW_MODEL_IN_ACCESSIBLE_MESSAGE
import com.gregantech.timepass.util.extension.*
import com.gregantech.timepass.util.recyclerview.itemdecoration.GridItemDecoration
import com.gregantech.timepass.util.share.ShareUtil
import com.gregantech.timepass.util.sharedpreference.SharedPreferenceHelper
import com.gregantech.timepass.view.profile.viewmodel.UserProfileViewModel
import com.gregantech.timepass.widget.GridPaginationScrollListener

class UserProfileActivity : TimePassBaseActivity() {

    private var user: User = User()
    private lateinit var binding: ActivityUserProfileBinding
    private var sharedPreferenceHelper = SharedPreferenceHelper
    private lateinit var viewModelFactory: UserProfileViewModel.Factory
    private lateinit var itemDecoration: GridItemDecoration

    private val viewModel: UserProfileViewModel by lazy {
        requireNotNull(this) {
            VIEW_MODEL_IN_ACCESSIBLE_MESSAGE
        }
        ViewModelProvider(this, viewModelFactory).get(UserProfileViewModel::class.java)
    }

    companion object {
        fun present(context: Context, followerId: String) {
            val intent = Intent(context, UserProfileActivity::class.java)
            intent.putExtra(UserProfileActivityBundleKeyEnum.FOLLOWER_ID.value, followerId)
            context.startActivity(intent)
        }
    }

    private val followerId: String by lazy {
        sharedPreferenceHelper.getUserId()
    }

    private val userId: String by lazy {
        intent.getStringExtra(UserProfileActivityBundleKeyEnum.FOLLOWER_ID.value)
    }

    private lateinit var railItemClickHandler: RailItemClickHandler
    private var railList: ArrayList<RailBaseItemModel> = arrayListOf()
    private var videoList: ArrayList<Video> = arrayListOf()

    private var isLastData: Boolean = false
    private var pageNo: Int = 1

    var isLastPage: Boolean = false
    var isLoading: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initDataBinding()
        generateItemDecoration()
        setupToolBar()
        setupViewModelFactory()
        getUserData()
        onClick()
        onClickRailListItem()
        setupViewModelObserver()
    }

    private fun setupViewModelFactory() {
        viewModelFactory = UserProfileViewModel.Factory(
            ProfileRepository(),
            UserProfileScreenRepository(sharedPreferenceHelper),
            VideoListRepository()
        )
    }

    private fun setupToolBar() {
        setSupportActionBar(binding.tbProfile.toolbar)
        setToolbarBackButton()
    }

    private fun setToolbarBackButton() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    private fun setToolbarTitle(name: String) {
        supportActionBar?.title = name
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

    private fun initDataBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_profile)
        initBannerAd(binding.frmUsrProfileBannerContainer)
    }

    private fun setUserData() {
        binding.ivProfilePicture.loadUrlCircle(
            user.profileImage,
            R.drawable.place_holder_profile
        )
        binding.tvName.text = user.userName
        setToolbarTitle(user.userName)
        binding.tvTotalPost.text = user.posts.appendPost()
        binding.tvTotalFollowers.text = user.followers.appendFollowers()
        binding.tvTotalFollowing.text = user.following.appendFollowing()
        binding.tvBio.text = user.bio
        binding.tvBio.visible(user.bio.isNotBlank())
        binding.tvYouTubeProfileUrl.text = getString(R.string.label_youtube_profile)
        binding.tvYouTubeProfileUrl.visible(user.youtube.isNotBlank())
        setFollowButton()
    }

    private fun setFollowButton() {
        user.isFollowed?.let {
            val isNotSameUser = user.userID != sharedPreferenceHelper.getUserId()
            binding.btnFollow.visible(isNotSameUser)
            changeFollowIcon(it)
            when (it) {
                true -> {
                    binding.btnFollow.text = getString(R.string.following)
                }
                false -> {
                    binding.btnFollow.text = getString(R.string.follow)
                }
            }
        }
    }

    private fun getUserData() {
        viewModel.getUserProfile(followerId, userId).observe(this, Observer {
            when (it.status) {
                TimePassBaseResult.Status.SUCCESS -> {
                    dismissProgressBar()
                    it.data?.user?.let { user ->
                        this.user = user
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

    private fun setupViewModelObserver() {
        viewModel.getUserVideoList(userId, pageNo)
            .observe(this, Observer { categoryListResponse ->
                when (categoryListResponse.status) {
                    TimePassBaseResult.Status.LOADING -> {
                    }
                    TimePassBaseResult.Status.SUCCESS -> {
                        categoryListResponse.data?.let {
                            isLastData = it.is_last
                            addVideoList(it.video)
                            railList.addAll(it.video.toRailItemTypeThreeModelList())
                            setupRecyclerView(railList)
                        }
                    }
                    TimePassBaseResult.Status.ERROR -> {
                        categoryListResponse.message?.toast(this)
                    }
                }
            })
    }

    private fun setupRecyclerView(categoryVideoList: ArrayList<RailBaseItemModel>) {
        if (::itemDecoration.isInitialized) {
            binding.rvUserVideoList.removeItemDecoration(itemDecoration)
        }
        binding.rvUserVideoList.apply {
            adapter = RailAdapter(
                categoryVideoList,
                railItemClickHandler
            )
            addItemDecoration(itemDecoration)
            setupRecyclerViewScrollListener()
        }
    }

    private fun setupRecyclerViewScrollListener() {
        binding.rvUserVideoList.addOnScrollListener(object :
            GridPaginationScrollListener(binding.rvUserVideoList.layoutManager as GridLayoutManager) {
            override fun isLastPage(): Boolean {
                return isLastPage
            }

            override fun isLoading(): Boolean {
                return isLoading
            }

            override fun onItemIsFirstVisibleItem(index: Int) {
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
                            addVideoList(it.video)
                            addMoreVideoList(it.video.toRailItemTypeThreeModelList())
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

    private fun onClickRailListItem() {
        railItemClickHandler = RailItemClickHandler()
        railItemClickHandler.clickPoster = { railModel ->
            displayUserVideoListPage(railModel.contentId)
        }
    }

    private fun onClick() {
        binding.btnFollow.setOnClickListener {
            onClickFollow()
        }
        binding.tvYouTubeProfileUrl.setOnClickListener {
            ShareUtil.openYoutube(this, user.youtube)
        }
    }

    private fun onClickFollow() {
        when (user.isFollowed) {
            true -> {
                user.isFollowed = false
            }
            false -> {
                user.isFollowed = true
            }
        }

        setFollowButton()
        setUserFollow()
    }

    private fun setUserFollow() {
        user.isFollowed?.let {
            viewModel.setUserFollow(it, followerId, userId)
                .observe(this,
                    Observer {
                    })
        }
    }

    private fun changeFollowIcon(isFollowed: Boolean) {
        binding.btnFollow.setCompoundDrawablesWithIntrinsicBounds(
            getFollowDrawable(isFollowed),
            0,
            0,
            0
        )
    }

    private fun getFollowDrawable(isFollowed: Boolean): Int {
        return if (isFollowed) {
            R.drawable.ic_followed
        } else {
            R.drawable.ic_follow_black
        }
    }

    private fun displayUserVideoListPage(contentId: String) {
        val scrollToPosition = videoList.indexOfFirst { it.Id == contentId }
        UserVideoListActivity.present(
            this,
            userId,
            videoList,
            isLastData,
            pageNo,
            isLastPage,
            scrollToPosition,
            user.userName
        )
    }

    private fun addVideoList(video: List<Video>) {
        videoList.addAll(video)
    }

    private fun generateItemDecoration() {
        itemDecoration = GridItemDecoration(
            spacingHorizontal = resources.getDimensionPixelOffset(R.dimen.dp_4),
            spacingVertical = resources.getDimensionPixelOffset(R.dimen.dp_2),
            spanCount = resources.getInteger(R.integer.span_count_user_profile_post)
        )
    }
}
