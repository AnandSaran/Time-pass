package com.gregantech.timepass.view.category.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.gregantech.timepass.R
import com.gregantech.timepass.adapter.handler.rail.RailItemClickHandler
import com.gregantech.timepass.adapter.rail.RailAdapter
import com.gregantech.timepass.base.TimePassBaseFragment
import com.gregantech.timepass.databinding.FragmentCategoryBinding
import com.gregantech.timepass.model.RailBaseItemModel
import com.gregantech.timepass.model.RailItemDecorationTypeEnum
import com.gregantech.timepass.network.repository.CategoryRepository
import com.gregantech.timepass.network.repository.bridge.toRailItemTypeOneModelList
import com.gregantech.timepass.util.extension.generateRailItemDecoration
import com.gregantech.timepass.view.category.viewmodel.CategoryFragmentViewModel
import com.singtel.cast.utils.VIEW_MODEL_IN_ACCESSIBLE_MESSAGE

class CategoryFragment : TimePassBaseFragment() {
    private lateinit var ctxt: Context
    private lateinit var binding: FragmentCategoryBinding
    private lateinit var viewModelFactory: CategoryFragmentViewModel.Factory

    private lateinit var railItemClickHandler: RailItemClickHandler

    private val viewModel: CategoryFragmentViewModel by lazy {
        requireNotNull(this.activity) {
            VIEW_MODEL_IN_ACCESSIBLE_MESSAGE
        }
        ViewModelProvider(this, viewModelFactory).get(CategoryFragmentViewModel::class.java)
    }

    companion object {
        fun newInstance() = CategoryFragment()
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
            setupViewModelObserver()
        }

    }

    private fun setupViewModelObserver() {
        viewModel.getCategory().observe(viewLifecycleOwner, Observer { categoryResponse ->
            categoryResponse?.data?.category?.let { categoryList ->
                setupRecyclerView(categoryList.toRailItemTypeOneModelList())
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

    private fun onClickRailListItem() {
        railItemClickHandler = RailItemClickHandler()
        railItemClickHandler.clickPoster = {
            showCategoryDetailFragment()
        }
    }

    private fun showCategoryDetailFragment() {
        NavHostFragment.findNavController(this)
            .navigate(R.id.action_categoryFragment_to_categoryDetailFragment)
    }

    private fun initGlide(): RequestManager {
        val options: RequestOptions = RequestOptions()
            .placeholder(R.drawable.white_background)
            .error(R.drawable.white_background)
        return Glide.with(this)
            .setDefaultRequestOptions(options)
    }
}