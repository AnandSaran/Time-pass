package com.gregantech.timepass.widget

import android.app.Dialog
import android.content.Context
import android.view.Window
import android.view.WindowManager
import android.widget.LinearLayout.LayoutParams
import com.gregantech.timepass.R

class CustomProgressbar(context: Context) : Dialog(context) {
    init {
        createProgressBar()
    }

    private fun createProgressBar() {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.progress_bar)
        window?.let {
            it.setLayout(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            it.setBackgroundDrawableResource(android.R.color.transparent)
            setCanceledOnTouchOutside(false)
            setCancelable(false)
        }
    }

    fun dismissProgress() {
        super.dismiss()
    }
}