package com.gregantech.timepass.view.interactions.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.gregantech.timepass.R
import com.gregantech.timepass.base.TimePassBaseActivity
import com.gregantech.timepass.databinding.ActivityInteractionsBinding

class InteractionsActivity : TimePassBaseActivity() {

    private lateinit var binding: ActivityInteractionsBinding

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

    }

    private fun subscribeToObservers() {

    }

    private fun setAssets() {

    }

}