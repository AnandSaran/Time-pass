package com.gregantech.timepass.view.profile.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.gregantech.timepass.R
import com.gregantech.timepass.adapter.handler.rail.RailItemClickHandler
import com.gregantech.timepass.adapter.rail.InstagramAdapter
import com.gregantech.timepass.base.TimePassBaseActivity
import com.gregantech.timepass.base.TimePassBaseResult
import com.gregantech.timepass.databinding.ActivityUserProfileBinding
import com.gregantech.timepass.general.bundklekey.UserProfileActivityBundleKeyEnum
import com.gregantech.timepass.model.RailBaseItemModel
import com.gregantech.timepass.model.RailItemTypeTwoModel
import com.gregantech.timepass.network.repository.ProfileRepository
import com.gregantech.timepass.network.repository.VideoListRepository
import com.gregantech.timepass.network.repository.bridge.toRailItemTypeTwoModelList
import com.gregantech.timepass.network.repository.local.UserProfileScreenRepository
import com.gregantech.timepass.network.response.User
import com.gregantech.timepass.util.PlayerViewAdapter
import com.gregantech.timepass.util.constant.VIEW_MODEL_IN_ACCESSIBLE_MESSAGE
import com.gregantech.timepass.util.extension.*
import com.gregantech.timepass.util.sharedpreference.SharedPreferenceHelper
import com.gregantech.timepass.view.profile.viewmodel.UserProfileViewModel
import com.gregantech.timepass.widget.PaginationScrollListener

class UserProfileActivity : TimePassBaseActivity() {

    private lateinit var binding: ActivityUserProfileBinding
    private var sharedPreferenceHelper = SharedPreferenceHelper
    private lateinit var viewModelFactory: UserProfileViewModel.Factory
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
        intent.getStringExtra(UserProfileActivityBundleKeyEnum.FOLLOWER_ID.value)
    }

    private lateinit var railItemClickHandler: RailItemClickHandler
    private var railList: ArrayList<RailBaseItemModel> = arrayListOf()

    private var isLastData: Boolean = false
    private var pageNo: Int = 1

    var isLastPage: Boolean = false
    var isLoading: Boolean = false
    var railModel = RailItemTypeTwoModel()
    var currentIndex = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initDataBinding()
        setupToolBar()
        setupViewModelFactory()
        getUserData()
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
    }

    private fun setUserData(user: User) {
        binding.ivProfilePicture.loadUrlCircle(
            user.profileImage,
            R.drawable.place_holder_profile
        )
        binding.tvName.text = user.userName
        setToolbarTitle(user.userName)
        binding.tvTotalPost.text = user.posts.appendPost()
        binding.tvTotalFollowers.text = user.followers.appendFollowers()
        binding.tvTotalFollowing.text = user.following.appendFollowing()
    }

    private fun getUserData() {
        viewModel.getUserProfile(followerId).observe(this, Observer {
            when (it.status) {
                TimePassBaseResult.Status.SUCCESS -> {
                    dismissProgressBar()
                    it.data?.user?.let { user ->
                        setUserData(user)
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
        viewModel.getUserVideoList(followerId, pageNo)
            .observe(this, Observer { categoryListResponse ->
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
                        categoryListResponse.message?.toast(this)
                    }
                }
            })
    }

    private fun setupRecyclerView(categoryVideoList: ArrayList<RailBaseItemModel>) {
        binding.rvUserVideoList.apply {
            setHasFixedSize(true)
            adapter = InstagramAdapter(
                modelList = categoryVideoList,
                railItemClickHandler = railItemClickHandler
            )
        }
        // setUpSnapShot()
        setupRecyclerViewScrollListener()
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
        viewModel.getMoreCategoryVideoList(followerId, pageNo)
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

    private fun onClickRailListItem() {
        railItemClickHandler = RailItemClickHandler()
        railItemClickHandler.clickPoster = { railModel ->
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        PlayerViewAdapter.releaseAllPlayers()
    }

}
