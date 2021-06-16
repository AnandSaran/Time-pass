package com.gregantech.timepass.view.home.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.gregantech.timepass.BuildConfig
import com.gregantech.timepass.R
import com.gregantech.timepass.base.TimePassBaseActivity
import com.gregantech.timepass.base.TimePassBaseResult
import com.gregantech.timepass.databinding.ActivityCategoryBinding
import com.gregantech.timepass.model.AppConfigResponse
import com.gregantech.timepass.network.repository.AppConfigRepository
import com.gregantech.timepass.util.constant.APP_PLAYSTORE_LINK
import com.gregantech.timepass.util.constant.VIEW_MODEL_IN_ACCESSIBLE_MESSAGE
import com.gregantech.timepass.util.extension.openWebLink
import com.gregantech.timepass.util.extension.toast
import com.gregantech.timepass.util.sharedpreference.SharedPreferenceHelper
import com.gregantech.timepass.view.home.fragment.FilePickerBottomSheetFragment
import com.gregantech.timepass.view.home.viewmodel.AppConfigViewModel
import com.gregantech.timepass.view.live.activity.LiveBroadCastActivity
import com.gregantech.timepass.view.uservideolist.fragment.UserVideoListFragment


class HomeActivity : TimePassBaseActivity(), FilePickerBottomSheetFragment.ItemClickListener {
    private lateinit var binding: ActivityCategoryBinding
    private lateinit var navController: NavController
    private var selectedIndex: Int = -1
    private lateinit var appConfigViewModelFactory: AppConfigViewModel.Factory

    companion object {
        fun present(context: Context) {
            val intent = Intent(context, HomeActivity::class.java)
            context.startActivity(intent)
        }
    }

    private val appConfigViewModel: AppConfigViewModel by lazy {
        requireNotNull(this) {
            VIEW_MODEL_IN_ACCESSIBLE_MESSAGE
        }
        ViewModelProvider(this, appConfigViewModelFactory).get(AppConfigViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initDataBinding()
        setupViewModelFactory()
        initView()
        setupDestinationChangedListener()
        initViewModelFactory()
        onClickBottomNavigation()
    }

    override fun onResume() {
        super.onResume()
        doFetchAppConfig()
    }

    private fun setupViewModelFactory() {
        appConfigViewModelFactory = AppConfigViewModel.Factory(AppConfigRepository())
    }


    private fun doFetchAppConfig() {
        appConfigViewModel.getAppConfig().observe(this, Observer { resultOf ->
            when (resultOf.status) {
                TimePassBaseResult.Status.LOADING -> {
                }
                TimePassBaseResult.Status.SUCCESS -> {
                    resultOf.data?.App?.get(0)?.run {
                        val newVersion = appVersion?.toInt() ?: 0
                        if (newVersion > BuildConfig.VERSION_CODE)
                            showUpdateDialog(this)
                    }
                }
                TimePassBaseResult.Status.ERROR -> {
                    resultOf.message?.toast(this)
                }
            }
        })
    }

    private fun showUpdateDialog(appItem: AppConfigResponse.AppItem) {
        with(AlertDialog.Builder(this)) {
            setCancelable(false)
            setTitle(appItem.title)
            setMessage(appItem.message)
            setPositiveButton(getString(R.string.update)) { dialog, which ->
                openWebLink(APP_PLAYSTORE_LINK)
                dialog.dismiss()
            }
            show()
        }
    }

    override fun onSupportNavigateUp() =
        findNavController(this, R.id.nav_host_fragment).navigateUp()

    private fun setupBottomNavMenu() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        findViewById<BottomNavigationView>(R.id.bottomNavigation)?.run {
            if (!SharedPreferenceHelper.isLiveEnabled()) {
                menu.removeItem(R.id.categoryBroadcast)
            }
            setupWithNavController(navController)
        }
    }

    private fun initDataBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_category)
    }

    private fun initView() {
        setupBottomNavMenu()
        setSupportActionBar(binding.tbCategory.toolbar)
        binding.tbCategory.toolbar.setTitleTextAppearance(this, R.style.logo_font)
    }

    private fun initViewModelFactory() {
    }

    private fun setupDestinationChangedListener() {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            setToolbarTitle(destination.id)
            when (destination.id) {
            }
        }
    }

    private fun setToolbarTitle(destinationId: Int) {
        title = when (destinationId) {
            R.id.userVideoListFragment -> getString(R.string.app_name)
            R.id.categoryFragment -> getString(R.string.app_name)
            R.id.userProfileFragment -> getString(R.string.profile)
            else -> getString(R.string.app_name)
        }
    }

    private fun onClickBottomNavigation() {
        binding.bottomNavigation.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.userVideoListFragment -> {
                    navController.popBackStack(R.id.userVideoListFragment, true)
                    onSelectTab(R.id.userVideoListFragment)
                }
                R.id.categoryBroadcast -> {
                    LiveBroadCastActivity.present(this)
                    //LiveVideoBroadCastActivity.present(this)
                }
                R.id.categoryFragment -> {
                    navController.popBackStack(R.id.categoryFragment, true)
                    onSelectTab(R.id.categoryFragment)
                }
                R.id.userProfileFragment -> {
                    navController.popBackStack(R.id.userProfileFragment, true)
                    onSelectTab(R.id.userProfileFragment)
                }
            }
            true
        }
    }

    private fun onSelectTab(destination: Int): Boolean {
        navController.popBackStack(R.id.categoryDetailFragment, true)
        return when {
            navController.popBackStack(destination, false) -> {
                true
            }
            selectedIndex != destination -> {
                navController.navigate(destination)
                true
            }
            else -> {
                false
            }
        }
    }

    override fun onItemClick(item: String) {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val fragment = navHostFragment.childFragmentManager.fragments[0]
        if (fragment is UserVideoListFragment) {
            fragment.onFilePickItemClick(item)
        }
    }
}