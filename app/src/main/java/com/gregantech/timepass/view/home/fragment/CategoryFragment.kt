package com.gregantech.timepass.view.home.fragment

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.gregantech.timepass.R
import com.gregantech.timepass.adapter.handler.rail.RailItemClickHandler
import com.gregantech.timepass.adapter.rail.RailAdapter
import com.gregantech.timepass.base.TimePassBaseFragment
import com.gregantech.timepass.base.TimePassBaseResult
import com.gregantech.timepass.databinding.FragmentCategoryBinding
import com.gregantech.timepass.general.UserListScreenTitleEnum
import com.gregantech.timepass.general.UserListScreenTypeEnum
import com.gregantech.timepass.general.bundklekey.CategoryDetailBundleKeyEnum
import com.gregantech.timepass.model.RailBaseItemModel
import com.gregantech.timepass.model.RailItemDecorationTypeEnum
import com.gregantech.timepass.network.repository.CategoryRepository
import com.gregantech.timepass.network.repository.bridge.toRailItemTypeOneModelList
import com.gregantech.timepass.util.constant.VIEW_MODEL_IN_ACCESSIBLE_MESSAGE
import com.gregantech.timepass.util.extension.generateRailItemDecoration
import com.gregantech.timepass.view.categoryvideosearch.activity.SearchVideoActivity
import com.gregantech.timepass.view.home.viewmodel.CategoryFragmentViewModel
import com.gregantech.timepass.view.home.viewmodel.HomeSharedViewModel


class CategoryFragment : TimePassBaseFragment() {
    private lateinit var ctxt: Context
    private lateinit var binding: FragmentCategoryBinding
    private lateinit var viewModelFactory: CategoryFragmentViewModel.Factory

    private lateinit var railItemClickHandler: RailItemClickHandler
    private lateinit var homeSharedViewModelFactory: HomeSharedViewModel.Factory

    private val viewModel: CategoryFragmentViewModel by lazy {
        requireNotNull(this.activity) {
            VIEW_MODEL_IN_ACCESSIBLE_MESSAGE
        }
        ViewModelProvider(this, viewModelFactory).get(CategoryFragmentViewModel::class.java)
    }
    private val homeSharedViewModel: HomeSharedViewModel by lazy {
        requireNotNull(this.activity) {
            VIEW_MODEL_IN_ACCESSIBLE_MESSAGE
        }
        ViewModelProvider(this.requireActivity()).get(HomeSharedViewModel::class.java)
    }

    companion object {
        fun newInstance() = CategoryFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (!::binding.isInitialized) {
            binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_category,
                container,
                false
            )
            context?.let {
                ctxt = it
            }

            onClickRailListItem()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!::viewModelFactory.isInitialized) {
            viewModelFactory = CategoryFragmentViewModel.Factory(CategoryRepository())
            homeSharedViewModelFactory = HomeSharedViewModel.Factory()
            setupViewModelObserver()
        }

    }

    private fun setupViewModelObserver() {
        viewModel.getCategory().observe(viewLifecycleOwner, Observer { categoryResponse ->
            when (categoryResponse.status) {
                TimePassBaseResult.Status.SUCCESS -> {
                    categoryResponse?.data?.category?.let { categoryList ->
                        setupRecyclerView(categoryList.toRailItemTypeOneModelList())
                    }
                    dismissProgressBar()
                }
                TimePassBaseResult.Status.LOADING -> {
                    showProgressBar()
                }
                else -> {
                    dismissProgressBar()
                }
            }
        })
    }

    private fun setupRecyclerView(categoryList: ArrayList<RailBaseItemModel>) {
        binding.rvCategory.apply {
            generateRailItemDecoration(RailItemDecorationTypeEnum.TYPE_RAIL_ITEM_DECORATION_ONE)
            adapter = RailAdapter(
                railListModel = categoryList,
                railItemClickHandler = railItemClickHandler
            )
        }
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
        SearchVideoActivity.present(
            ctxt,
            UserListScreenTitleEnum.SEARCH,
            UserListScreenTypeEnum.SEARCH
        )
    }

    private fun onClickRailListItem() {
        railItemClickHandler = RailItemClickHandler()
        railItemClickHandler.clickPoster = { railBaseItemModel ->
            showCategoryDetailFragment(railBaseItemModel.contentId)
        }
    }

    private fun showCategoryDetailFragment(contentId: String) {
        val bundle = Bundle().apply {
            putString(CategoryDetailBundleKeyEnum.CATEGORY_ID.value, contentId)
        }

        NavHostFragment.findNavController(this)
            .navigate(R.id.action_categoryFragment_to_categoryDetailFragment, bundle)
    }
}