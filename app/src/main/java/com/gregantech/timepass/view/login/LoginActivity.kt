package com.gregantech.timepass.view.login

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.databinding.DataBindingUtil
import com.gregantech.timepass.R
import com.gregantech.timepass.databinding.ActivityLoginBinding
import com.gregantech.timepass.util.extension.visible
import com.gregantech.timepass.view.category.activity.CategoryActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private var hasAnimationStarted = false

    companion object {
        fun present(context: Context) {
            val intent = Intent(context, LoginActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initDataBinding()
        setupOnClickListener()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus && !hasAnimationStarted) {
            hasAnimationStarted = true
            val metrics = resources.displayMetrics
            val translationY: ObjectAnimator = ObjectAnimator.ofFloat(
                binding.ivLogo,
                View.TRANSLATION_Y,
                -450f
            ) // metrics.heightPixels or root.getHeight()
            translationY.duration = 1000
            translationY.start()
            translationY.doOnEnd {
                binding.edtMobileNumber.visible(true)
                binding.btnSubmit.visible(true)
            }
        }
    }

    private fun initDataBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
    }

    private fun setupOnClickListener() {
        binding.btnSubmit.setOnClickListener {
            onClickButtonSubmit()
        }
    }

    private fun onClickButtonSubmit() {
        CategoryActivity.present(this)
        finish()
    }
}