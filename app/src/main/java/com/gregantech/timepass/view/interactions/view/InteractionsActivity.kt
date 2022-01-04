package com.gregantech.timepass.view.interactions.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.gregantech.timepass.R
import com.gregantech.timepass.base.TimePassBaseActivity
import com.gregantech.timepass.databinding.ActivityInteractionsBinding
import com.gregantech.timepass.network.repository.InteractionsRepository
import com.gregantech.timepass.util.constant.VIEW_MODEL_IN_ACCESSIBLE_MESSAGE
import com.gregantech.timepass.view.interactions.viewmodel.InteractionsViewModel

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

    }

    private fun setAssets() {

    }

}