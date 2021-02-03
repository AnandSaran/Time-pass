package com.gregantech.timepass.view.profile.fragment

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.SnapHelper
import com.gregantech.timepass.R
import com.gregantech.timepass.adapter.handler.rail.RailItemClickHandler
import com.gregantech.timepass.adapter.rail.RailAdapter
import com.gregantech.timepass.base.TimePassBaseFragment
import com.gregantech.timepass.base.TimePassBaseResult
import com.gregantech.timepass.databinding.FragmentProfileBinding
import com.gregantech.timepass.model.RailBaseItemModel
import com.gregantech.timepass.model.RailItemTypeTwoModel
import com.gregantech.timepass.network.repository.LoginRepository
import com.gregantech.timepass.network.repository.VideoListRepository
import com.gregantech.timepass.network.repository.bridge.toRailItemTypeThreeModelList
import com.gregantech.timepass.network.response.Video
import com.gregantech.timepass.util.constant.VIEW_MODEL_IN_ACCESSIBLE_MESSAGE
import com.gregantech.timepass.util.extension.*
import com.gregantech.timepass.util.sharedpreference.SharedPreferenceHelper
import com.gregantech.timepass.view.profile.activity.ProfileActivity
import com.gregantech.timepass.view.profile.activity.UserVideoListActivity
import com.gregantech.timepass.view.profile.viewmodel.ProfileFragmentViewModel
import com.gregantech.timepass.widget.PaginationScrollListener


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
    private var videoList: ArrayList<Video> = arrayListOf()

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
                            addVideoList(it.video)
                            railList.addAll(it.video.toRailItemTypeThreeModelList())
                            setupRecyclerView(railList)
                        }
                    }
                    TimePassBaseResult.Status.ERROR -> {
                        categoryListResponse.message?.toast(ctxt)
                    }
                }
            })
    }

    private fun setupRecyclerView(categoryVideoList: ArrayList<RailBaseItemModel>) {
        // binding.rvCategoryVideoList.setMediaObjects(categoryVideoList)
        binding.rvUserVideoList.apply {
            //generateRailItemDecoration(RailItemDecorationTypeEnum.TYPE_RAIL_ITEM_DECORATION_TWO)
            setHasFixedSize(true)
            adapter = RailAdapter(
                categoryVideoList,
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
            displayUserVideoListPage(railModel.contentId)
        }
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
                            addVideoList(it.video)
                            addMoreVideoList(it.video.toRailItemTypeThreeModelList())
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

    private fun displayUserVideoListPage(contentId: String) {
        val scrollToPosition = videoList.indexOfFirst { it.Id == contentId }
        UserVideoListActivity.present(
            ctxt,
            SharedPreferenceHelper.getUserId(),
            videoList,
            isLastData,
            pageNo,
            isLastPage,
            scrollToPosition,
            SharedPreferenceHelper.getUserName()
        )
    }

    private fun addVideoList(video: List<Video>) {
        videoList.addAll(video)
    }
}