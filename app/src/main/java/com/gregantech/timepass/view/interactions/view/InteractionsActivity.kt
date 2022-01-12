package com.gregantech.timepass.view.interactions.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.gregantech.timepass.R
import com.gregantech.timepass.adapter.interaction.InteractionAdapter
import com.gregantech.timepass.base.TimePassBaseActivity
import com.gregantech.timepass.base.TimePassBaseResult
import com.gregantech.timepass.databinding.ActivityInteractionsBinding
import com.gregantech.timepass.model.PostItem
import com.gregantech.timepass.network.repository.InteractionsRepository
import com.gregantech.timepass.util.constant.VIEW_MODEL_IN_ACCESSIBLE_MESSAGE
import com.gregantech.timepass.view.interactions.viewmodel.InteractionsViewModel
import com.gregantech.timepass.view.profile.activity.UserProfileActivity

class InteractionsActivity : TimePassBaseActivity() {

    private lateinit var binding: ActivityInteractionsBinding
    private lateinit var interactionsViewModelFactory: InteractionsViewModel.Factory

    private val interactionsViewModel: InteractionsViewModel by lazy {
        requireNotNull(this) {
            VIEW_MODEL_IN_ACCESSIBLE_MESSAGE
        }
        ViewModelProvider(this, interactionsViewModelFactory).get(InteractionsViewModel::class.java)
    }

    companion object {
        fun present(context: Context) {
            context.startActivity(Intent(context, InteractionsActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_interactions)
        initVieModel()
        subscribeToObservers()
        setAssets()
    }

    private fun initVieModel() {
        interactionsViewModelFactory = InteractionsViewModel.Factory(InteractionsRepository())
    }

    private fun subscribeToObservers() {
        interactionsViewModel.getInteractions().observe(this, Observer {
            when (it.status) {
                TimePassBaseResult.Status.SUCCESS -> {
                    dismissProgressBar()
                    if (!it.data?.Post.isNullOrEmpty()) {
                        (binding.rvInteractions.adapter as InteractionAdapter).interactionList =
                            it.data?.Post as ArrayList<PostItem?>?
                        interactionsViewModel.updateState()
                    } else {
                        binding.vsActivity.setChildVisible()
                    }
                }
                TimePassBaseResult.Status.LOADING -> showProgressBar()
                TimePassBaseResult.Status.ERROR -> {
                    Log.d(TAG, "subscribeToObservers: message ${it.message}")
                }
            }
        })
    }

    private fun setAssets() {

        setSupportActionBar(binding.toolbarActivity)
        supportActionBar?.apply {
            title = getString(R.string.title_activity)
            setDisplayHomeAsUpEnabled(true)
        }

        binding.rvInteractions.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = InteractionAdapter(::callback)
        }
    }

    private fun callback(postItem: PostItem) {
        UserProfileActivity.present(this, postItem.user?.activityUserId!!)
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

}