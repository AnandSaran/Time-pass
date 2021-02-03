package com.gregantech.timepass.view.createvideo.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.gregantech.timepass.R
import com.gregantech.timepass.base.TimePassBaseActivity
import com.gregantech.timepass.base.TimePassBaseResult
import com.gregantech.timepass.databinding.ActivityVideoUploadBinding
import com.gregantech.timepass.general.bundklekey.CreateVideoBundleEnum
import com.gregantech.timepass.network.repository.VideoListRepository
import com.gregantech.timepass.network.repository.convertor.ProfileConverterFactory
import com.gregantech.timepass.network.repository.local.VideoUploadScreenRepository
import com.gregantech.timepass.network.response.VideoUploadResponse
import com.gregantech.timepass.util.constant.EMPTY_BOOLEAN
import com.gregantech.timepass.util.constant.VIEW_MODEL_IN_ACCESSIBLE_MESSAGE
import com.gregantech.timepass.util.extension.toast
import com.gregantech.timepass.util.sharedpreference.SharedPreferenceHelper
import com.gregantech.timepass.view.createvideo.viewmodel.VideoUploadViewModel

class VideoUploadActivity : TimePassBaseActivity() {
    private lateinit var binding: ActivityVideoUploadBinding
    private lateinit var viewModelFactory: VideoUploadViewModel.Factory
    private var filePath: String? = null
    private var isImage = EMPTY_BOOLEAN

    private val viewModel: VideoUploadViewModel by lazy {
        requireNotNull(this) {
            VIEW_MODEL_IN_ACCESSIBLE_MESSAGE
        }
        ViewModelProvider(this, viewModelFactory).get(VideoUploadViewModel::class.java)
    }

    companion object {
        fun generateIntent(
            context: Context, videoPath: String, isImage: Boolean = false
        ): Intent {
            val intent = Intent(context, VideoUploadActivity::class.java)
            intent.putExtra(CreateVideoBundleEnum.VIDEO_PATH.value, videoPath)
            intent.putExtra(CreateVideoBundleEnum.IS_IMAGE.value, isImage)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initDataBinding()
        setData()
        setupToolBar()
        setupOnClick()
        setupViewModelFactory()
    }

    private fun setData() {
        filePath = intent.getStringExtra(CreateVideoBundleEnum.VIDEO_PATH.value)
        isImage = intent.getBooleanExtra(CreateVideoBundleEnum.IS_IMAGE.value, EMPTY_BOOLEAN)
    }

    private fun initDataBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_video_upload)
    }

    private fun setupToolBar() {
        setSupportActionBar(binding.tbUploadVideo.toolbar)
        setToolbarTitle()
        setToolbarBackButton()
    }

    private fun setupViewModelFactory() {
        viewModelFactory = VideoUploadViewModel.Factory(
            VideoListRepository(),
            VideoUploadScreenRepository(ProfileConverterFactory(this), SharedPreferenceHelper)
        )
    }

    private fun setToolbarTitle() {
        supportActionBar?.title =
            if (isImage) {
                getString(R.string.label_upload_image)
            } else {
                getString(R.string.label_upload_video)
            }
    }

    private fun setToolbarBackButton() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
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

    private fun setupOnClick() {
        binding.btnSubmit.setOnClickListener {
            onClickSubmit()
        }
    }

    private fun onClickSubmit() {
        if (isValidForm()) {
            if (isImage) {
                uploadImage()
            } else {
                uploadVideo()
            }
        }
    }

    private fun uploadImage() {
        viewModel.uploadImage(
            binding.edtTitle.text.toString(),
            binding.edtDescription.text.toString(),
            filePath.toString()
        ).observe(this, Observer {
            when (it.status) {
                TimePassBaseResult.Status.SUCCESS -> {
                    dismissProgressBar()
                    returnResult(it.data)
                }
                TimePassBaseResult.Status.LOADING -> {
                    showProgressBar()
                }
                else -> {
                    dismissProgressBar()
                    it.message?.toast(this)
                }
            }
        })
    }

    private fun uploadVideo() {
        viewModel.updateVideo(
            binding.edtTitle.text.toString(),
            binding.edtDescription.text.toString(),
            filePath.toString()
        ).observe(this, Observer {
            when (it.status) {
                TimePassBaseResult.Status.SUCCESS -> {
                    dismissProgressBar()
                    returnResult(it.data)
                }
                TimePassBaseResult.Status.LOADING -> {
                    showProgressBar()
                }
                else -> {
                    dismissProgressBar()
                    it.message?.toast(this)
                }
            }
        })
    }

    private fun returnResult(data: VideoUploadResponse?) {
        data?.video?.firstOrNull()?.let { video ->
            val data = Intent()
            val bundle = Bundle()
            bundle.putParcelable(CreateVideoBundleEnum.VIDEO_RESPONSE.value, video)
            data.putExtras(bundle)
            setResult(Activity.RESULT_OK, data)
            super.onBackPressed()
        }
    }

    private fun isValidForm(): Boolean {
        var count = 0
        if (binding.edtTitle.text.toString().isBlank()) {
            binding.tilTitle.error = getString(R.string.invalid_title)
            count++
        }
        if (filePath.isNullOrBlank()) {
            getString(R.string.invalid_file).toast(this)
            count++
        }

        return count == 0
    }
}