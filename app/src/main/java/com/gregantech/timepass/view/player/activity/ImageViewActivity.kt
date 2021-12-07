package com.gregantech.timepass.view.player.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.gregantech.timepass.R
import com.gregantech.timepass.databinding.ActivityImageViewBinding
import com.gregantech.timepass.general.bundklekey.ImageViewBundleKey
import com.gregantech.timepass.util.extension.loadUrl

class ImageViewActivity : AppCompatActivity() {

    companion object {
        fun present(context: Context, imageUrl: String) {
            val intent = Intent(context, ImageViewActivity::class.java)
            intent.putExtra(ImageViewBundleKey.IMAGE_URL.value, imageUrl)
            context.startActivity(intent)
        }
    }

    private lateinit var binding: ActivityImageViewBinding
    private val imageUrl: String by lazy {
        intent.getStringExtra(ImageViewBundleKey.IMAGE_URL.value)!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initDataBinding()
        setImage()
    }

    private fun initDataBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_image_view)
    }

    private fun setImage() {
        binding.ivPost.loadUrl(imageUrl)
    }
}