package com.gregantech.timepass.view.home.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.gregantech.timepass.R
import com.gregantech.timepass.base.TimePassBaseActivity
import com.gregantech.timepass.databinding.ActivityCategoryBinding
import com.gregantech.timepass.util.AdvertisementHandler
import com.gregantech.timepass.util.constant.VIEW_MODEL_IN_ACCESSIBLE_MESSAGE
import com.gregantech.timepass.view.home.fragment.FilePickerBottomSheetFragment
import com.gregantech.timepass.view.home.viewmodel.HomeSharedViewModel
import com.gregantech.timepass.view.uservideolist.fragment.UserVideoListFragment


class HomeActivity : TimePassBaseActivity(), FilePickerBottomSheetFragment.ItemClickListener {
    private lateinit var binding: ActivityCategoryBinding
    private lateinit var navController: NavController
    private var selectedIndex: Int = -1

    companion object {
        fun present(context: Context) {
            val intent = Intent(context, HomeActivity::class.java)
            context.startActivity(intent)
        }
    }

    private val homeSharedViewModel: HomeSharedViewModel by lazy {
        requireNotNull(this) {
            VIEW_MODEL_IN_ACCESSIBLE_MESSAGE
        }
        ViewModelProvider(this).get(HomeSharedViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initDataBinding()
        initView()
        setupDestinationChangedListener()
        initViewModelFactory()
        onClickBottomNavigation()

    }

    override fun onSupportNavigateUp() =
        findNavController(this, R.id.nav_host_fragment).navigateUp()

    private fun setupBottomNavMenu() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNav?.setupWithNavController(navController)
    }

    private fun initDataBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_category)
    }

    private fun initView() {
        Log.d(TAG, "initView: adVisibilityResponse ${AdvertisementHandler.isAdEnabled("2")}")
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