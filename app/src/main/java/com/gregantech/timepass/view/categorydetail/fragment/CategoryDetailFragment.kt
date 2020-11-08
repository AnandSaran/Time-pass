package com.gregantech.timepass.view.categorydetail.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.gregantech.timepass.R
import com.gregantech.timepass.adapter.handler.rail.RailItemClickHandler
import com.gregantech.timepass.adapter.rail.RailAdapter
import com.gregantech.timepass.databinding.FragmentCategoryDetailBinding
import com.gregantech.timepass.model.RailBaseItemModel
import com.gregantech.timepass.model.RailItemDecorationTypeEnum
import com.gregantech.timepass.repository.factory.RailDataFactory
import com.gregantech.timepass.util.extension.generateRailItemDecoration
import com.gregantech.timepass.view.categorydetail.viewmodel.CategoryDetailFragmentViewModel
import com.singtel.cast.utils.VIEW_MODEL_IN_ACCESSIBLE_MESSAGE

class CategoryDetailFragment : Fragment() {
    private lateinit var ctxt: Context
    private lateinit var binding: FragmentCategoryDetailBinding
    private lateinit var viewModelFactory: CategoryDetailFragmentViewModel.Factory

    private lateinit var railItemClickHandler: RailItemClickHandler

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
        viewModelFactory = CategoryDetailFragmentViewModel.Factory(RailDataFactory())
        setupViewModelObserver()
    }

    override fun onDestroy() {
        binding.rvCategoryVideoList.releasePlayer()
        super.onDestroy()
    }

    private fun setupViewModelObserver() {
        viewModel.categoryVideoList.observe(viewLifecycleOwner, Observer { categoryList ->
            categoryList?.let {
                setupRecyclerView(categoryList)
            }
        })
    }

    private fun setupRecyclerView(categoryVideoList: ArrayList<RailBaseItemModel>) {
        binding.rvCategoryVideoList.setMediaObjects(categoryVideoList)
        binding.rvCategoryVideoList.apply {
            generateRailItemDecoration(RailItemDecorationTypeEnum.TYPE_RAIL_ITEM_DECORATION_TWO)
            adapter = RailAdapter(
                railListModel = categoryVideoList,
                railItemClickHandler = railItemClickHandler
            )
            scrollTo(0,0)
        }
    }

    private fun onClickRailListItem() {
        railItemClickHandler = RailItemClickHandler()
        railItemClickHandler.clickPoster = {
        }
    }
}