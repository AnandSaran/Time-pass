package com.gregantech.timepass.view.category.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.gregantech.timepass.R
import com.gregantech.timepass.databinding.ActivityCategoryBinding
import com.gregantech.timepass.view.category.viewmodel.CategoryActivitySharedViewModel
import com.singtel.cast.utils.VIEW_MODEL_IN_ACCESSIBLE_MESSAGE

class CategoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCategoryBinding
    private lateinit var navController: NavController

    private lateinit var categoryActivitySharedViewModelFactory: CategoryActivitySharedViewModel.Factory

    private val categoryActivitySharedViewModel: CategoryActivitySharedViewModel by lazy {
        requireNotNull(this) {
            VIEW_MODEL_IN_ACCESSIBLE_MESSAGE
        }
        ViewModelProvider(
            this,
            categoryActivitySharedViewModelFactory
        ).get(CategoryActivitySharedViewModel::class.java)
    }

    companion object {
        fun present(context: Context) {
            val intent = Intent(context, CategoryActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initDataBinding()
        initView()
        setupDestinationChangedListener()
        initViewModelFactory()
    }

    override fun onSupportNavigateUp() =
        findNavController(this, R.id.nav_host_fragment).navigateUp()

    private fun initDataBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_category)
    }

    private fun initView() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        setSupportActionBar(binding.tbCategory.toolbar)
    }

    private fun initViewModelFactory() {
        categoryActivitySharedViewModelFactory = CategoryActivitySharedViewModel.Factory()
    }

    private fun setupDestinationChangedListener() {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {

            }
        }
    }

}