package com.gregantech.timepass.view.userlist.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.gregantech.timepass.R
import com.gregantech.timepass.adapter.handler.rail.RailItemClickHandler
import com.gregantech.timepass.adapter.rail.RailAdapter
import com.gregantech.timepass.base.TimePassBaseFragment
import com.gregantech.timepass.databinding.FragmentUserListBinding
import com.gregantech.timepass.model.RailBaseItemModel
import com.gregantech.timepass.util.constant.VIEW_MODEL_IN_ACCESSIBLE_MESSAGE
import com.gregantech.timepass.view.profile.activity.UserProfileActivity
import com.gregantech.timepass.view.userlist.viewmodel.UserListSharedViewModel

class UserListFragment : TimePassBaseFragment() {
    companion object {
        fun newInstance() = UserListFragment()
    }

    private lateinit var binding: FragmentUserListBinding
    private lateinit var ctxt: Context
    private lateinit var railItemClickHandler: RailItemClickHandler
    private lateinit var userListSharedViewModelFactory: UserListSharedViewModel.Factory
    private val railList = arrayListOf<RailBaseItemModel>()
    private val userListSharedViewModel: UserListSharedViewModel by lazy {
        requireNotNull(this.requireActivity()) {
            VIEW_MODEL_IN_ACCESSIBLE_MESSAGE
        }
        ViewModelProvider(this.requireActivity()).get(UserListSharedViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (!::binding.isInitialized) {
            binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_user_list,
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!::userListSharedViewModelFactory.isInitialized) {
            userListSharedViewModelFactory = UserListSharedViewModel.Factory()
            setupViewModelObserver()
            setupRecyclerView()
        }
    }

    private fun setupViewModelObserver() {
        userListSharedViewModel.railList.observe(viewLifecycleOwner, Observer {
            it?.let {
                railList.clear()
                railList.addAll(it)
                binding.rvUserList.adapter?.notifyDataSetChanged()
            }
        })
    }

    private fun setupRecyclerView() {
        binding.rvUserList.apply {
            adapter = RailAdapter(
                railListModel = railList,
                railItemClickHandler = railItemClickHandler
            )
        }
    }

    private fun onClickRailListItem() {
        railItemClickHandler = RailItemClickHandler()
        railItemClickHandler.clickPoster = { railBaseItemModel ->
            displayUserProfilePage(railBaseItemModel.contentId)
        }
    }

    private fun displayUserProfilePage(followerId: String) {
        UserProfileActivity.present(ctxt, followerId)
    }
}