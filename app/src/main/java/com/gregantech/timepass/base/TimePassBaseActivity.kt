package com.gregantech.timepass.base

import androidx.appcompat.app.AppCompatActivity
import com.gregantech.timepass.widget.CustomProgressbar

/**
 * Created by anand on 2020-11-06.
 */

abstract class TimePassBaseActivity : AppCompatActivity() {
    private lateinit var customProgressbar: CustomProgressbar

    fun showProgressBar() {
        getProgressBar().show()
    }

    fun dismissProgressBar() {
        runOnUiThread {
            try {
                getProgressBar().dismissProgress()
            } catch (e: Exception) {

            }
        }
    }

    private fun getProgressBar(): CustomProgressbar {
        if (!::customProgressbar.isInitialized) {
            customProgressbar = CustomProgressbar(this)
        }
        return customProgressbar
    }
}