package com.gregantech.timepass.view.userlist.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.gregantech.timepass.R
import com.gregantech.timepass.base.TimePassBaseActivity
import com.gregantech.timepass.base.TimePassBaseResult
import com.gregantech.timepass.databinding.ActivityUserListBinding
import com.gregantech.timepass.general.UserListScreenTitleEnum
import com.gregantech.timepass.general.UserListScreenTypeEnum
import com.gregantech.timepass.general.UserListScreenTypeEnum.*
import com.gregantech.timepass.general.bundklekey.UserListActivityBundleKeyEnum
import com.gregantech.timepass.network.repository.UserListRepository
import com.gregantech.timepass.network.repository.bridge.toRailItemTypeFourModelList
import com.gregantech.timepass.network.response.userlist.Following
import com.gregantech.timepass.util.constant.EMPTY_STRING
import com.gregantech.timepass.util.constant.VIEW_MODEL_IN_ACCESSIBLE_MESSAGE
import com.gregantech.timepass.util.extension.toast
import com.gregantech.timepass.util.extension.visible
import com.gregantech.timepass.util.sharedpreference.SharedPreferenceHelper
import com.gregantech.timepass.view.userlist.fragment.UserListFragment
import com.gregantech.timepass.view.userlist.viewmodel.UserListActivityViewModel
import com.gregantech.timepass.view.userlist.viewmodel.UserListSharedViewModel
import com.singtel.cast.utils.navigation.FragmentNavigationUtil

class UserListActivity : TimePassBaseActivity() {
    companion object {
        fun present(
            context: Context,
            title: UserListScreenTitleEnum,
            screenType: UserListScreenTypeEnum
        ) {
            val intent = Intent(context, UserListActivity::class.java)
            intent.putExtra(UserListActivityBundleKeyEnum.TITLE.value, title.value)
            intent.putExtra(UserListActivityBundleKeyEnum.SCREEN_TYPE.value, screenType.value)
            context.startActivity(intent)
        }
    }

    private lateinit var binding: ActivityUserListBinding
    private val toolbarTitle: String by lazy {
        intent.getStringExtra(UserListActivityBundleKeyEnum.TITLE.value) ?: EMPTY_STRING
    }

    private lateinit var viewModelFactory: UserListActivityViewModel.Factory
    private lateinit var userListSharedViewModelFactory: UserListSharedViewModel.Factory

    private val viewModel: UserListActivityViewModel by lazy {
        requireNotNull(this) {
            VIEW_MODEL_IN_ACCESSIBLE_MESSAGE
        }
        ViewModelProvider(this, viewModelFactory).get(UserListActivityViewModel::class.java)
    }

    private val userListSharedViewModel: UserListSharedViewModel by lazy {
        requireNotNull(this) {
            VIEW_MODEL_IN_ACCESSIBLE_MESSAGE
        }
        ViewModelProvider(this).get(UserListSharedViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initDataBinding()
        showUserListFragment()
        setupToolBar()
        setupViewModelFactory()
        handleScreenType()
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

    private fun setupViewModelFactory() {
        viewModelFactory = UserListActivityViewModel.Factory(
            UserListRepository(),
            SharedPreferenceHelper
        )
        userListSharedViewModelFactory = UserListSharedViewModel.Factory()

    }

    private fun initDataBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_list)
    }

    private fun setupToolBar() {
        setSupportActionBar(binding.tbUserList.toolbar)
        setToolbarTitle()
        setToolbarBackButton()
    }

    private fun handleScreenType() {
        when (getScreenType()) {
            SEARCH.value -> {
                binding.tilSearchUser.visible(true)
                setupSearchTextWatcher()
            }
            FOLLOWING.value -> {
                fetchUserFollowingList()
            }
            FOLLOWERS.value -> {
                fetchUserFollowerList()
            }
        }
    }

    private fun setupSearchTextWatcher() {
        binding.tilSearchUser.editText?.doOnTextChanged { searchName, start, before, count ->
            if (searchName.toString().isNotBlank()) {
                fetchSearchUserList(searchName.toString())
            }
        }
    }

    private fun fetchSearchUserList(searchName: String) {
        viewModel.getSearchUserList(searchName).observe(this, Observer {
            when (it.status) {
                TimePassBaseResult.Status.LOADING -> {
                }
                TimePassBaseResult.Status.SUCCESS -> {
                    it.data?.let {
                        generateRailList(it.List)
                    }
                }
                TimePassBaseResult.Status.ERROR -> {
                    it.message?.toast(this)
                }
            }
        })
    }

    private fun fetchUserFollowingList() {
        viewModel.getUserFollowingList().observe(this, Observer {
            when (it.status) {
                TimePassBaseResult.Status.LOADING -> {
                }
                TimePassBaseResult.Status.SUCCESS -> {
                    it.data?.let {
                        generateRailList(it.List)
                    }
                }
                TimePassBaseResult.Status.ERROR -> {
                    it.message?.toast(this)
                }
            }
        })
    }

    private fun generateRailList(follows: List<Following>) {
        userListSharedViewModel.setRailList(follows.toRailItemTypeFourModelList())
    }

    private fun fetchUserFollowerList() {
        viewModel.getUserFollowerList().observe(this, Observer {
            when (it.status) {
                TimePassBaseResult.Status.LOADING -> {
                }
                TimePassBaseResult.Status.SUCCESS -> {
                    it.data?.let {
                        generateRailList(it.List)
                    }
                }
                TimePassBaseResult.Status.ERROR -> {
                    it.message?.toast(this)
                }
            }
        })
    }

    private fun getScreenType() =
        intent.getStringExtra(UserListActivityBundleKeyEnum.SCREEN_TYPE.value) ?: EMPTY_STRING

    private fun setToolbarBackButton() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    private fun setToolbarTitle() {
        supportActionBar?.title = toolbarTitle
    }

    private fun showUserListFragment() {
        val userListFragment = UserListFragment.newInstance()
        FragmentNavigationUtil.commitFragment(
            userListFragment,
            supportFragmentManager,
            binding.container.id
        )
    }
}