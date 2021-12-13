package com.gregantech.timepass.view.profile.activity

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.gregantech.timepass.R
import com.gregantech.timepass.base.TimePassBaseActivity
import com.gregantech.timepass.base.TimePassBaseResult
import com.gregantech.timepass.databinding.ActivityProfileBinding
import com.gregantech.timepass.general.bundklekey.ProfileActivityBundleKeyEnum
import com.gregantech.timepass.network.repository.ProfileRepository
import com.gregantech.timepass.network.repository.convertor.ProfileConverterFactory
import com.gregantech.timepass.network.repository.local.ProfileScreenRepository
import com.gregantech.timepass.util.constant.VIEW_MODEL_IN_ACCESSIBLE_MESSAGE
import com.gregantech.timepass.util.extension.isValidEmail
import com.gregantech.timepass.util.extension.loadUriCircle
import com.gregantech.timepass.util.extension.loadUrlCircle
import com.gregantech.timepass.util.extension.toast
import com.gregantech.timepass.util.log.LogUtil
import com.gregantech.timepass.util.sharedpreference.SharedPreferenceHelper
import com.gregantech.timepass.view.home.activity.HomeActivity
import com.gregantech.timepass.view.profile.viewmodel.ProfileViewModel
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.yalantis.ucrop.UCrop
import java.io.File
import java.util.regex.Pattern.compile


class ProfileActivity : TimePassBaseActivity() {

    private lateinit var binding: ActivityProfileBinding
    private var sharedPreferenceHelper = SharedPreferenceHelper
    private var filePath: Uri? = null
    private lateinit var viewModelFactory: ProfileViewModel.Factory

    private var permissionlistener: PermissionListener = object : PermissionListener {
        override fun onPermissionGranted() {
            callGalleryPic()
        }

        override fun onPermissionDenied(deniedPermissions: List<String>) {
        }
    }

    private val viewModel: ProfileViewModel by lazy {
        requireNotNull(this) {
            VIEW_MODEL_IN_ACCESSIBLE_MESSAGE
        }
        ViewModelProvider(this, viewModelFactory).get(ProfileViewModel::class.java)
    }

    companion object {
        fun present(context: Context, isUpdateProfile: Boolean = true) {
            val intent = Intent(context, ProfileActivity::class.java)
            intent.putExtra(ProfileActivityBundleKeyEnum.IS_UPDATE_PROFILE.value, isUpdateProfile)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initDataBinding()
        setupToolBar()
        setUserData()
        setupOnClick()
        setupViewModelFactory()
    }

    private fun setupViewModelFactory() {
        viewModelFactory = ProfileViewModel.Factory(
            ProfileRepository(),
            ProfileScreenRepository(ProfileConverterFactory(this), sharedPreferenceHelper)
        )
    }

    private fun setupOnClick() {
        binding.ivProfilePicture.setOnClickListener {
            askPermission()
        }
        binding.btnSubmit.setOnClickListener {
            onClickSubmit()
        }
    }

    private fun onClickSubmit() {
        if (isValidForm()) {
            viewModel.updateProfile(
                binding.edtName.text.toString(),
                binding.edtEmail.text.toString(),
                binding.edtBio.text.toString(),
                binding.edtYouTubeLink.text.toString(),
                filePath
            ).observe(this, Observer {
                when (it.status) {
                    TimePassBaseResult.Status.SUCCESS -> {
                        dismissProgressBar()
                        it.data?.user?.let { user ->
                            sharedPreferenceHelper.setUserData(user)
                            if (isUpdateProfile()) {
                                onBackPressed()
                            } else {
                                showHomePage()
                            }
                        }
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
    }

    private fun isValidForm(): Boolean {
        var count = 0
        if (binding.edtName.text.toString().isBlank()) {
            binding.tilName.error = getString(R.string.enter_name)
            count++
        }
        if (!binding.edtEmail.text.toString().isValidEmail()) {
            binding.tilEmail.error = getString(R.string.invalid_email)
            count++
        }
        if (!isValidYouTubeUrl(binding.edtYouTubeLink.text.toString())) {
            binding.tilYouTubeLink.error = getString(R.string.invalid_youtube_profile)
            count++
        }

        return count == 0
    }

    private fun isValidYouTubeUrl(youTubeUrl: String): Boolean {
        val success: Boolean
        val pattern = compile("^(http(s)?:\\/\\/)?((w){3}.)?youtu(be|.be)?(\\.com)?\\/.+")
        success = youTubeUrl.isBlank() || pattern.matcher(youTubeUrl).matches()
        return success
    }


    private fun askPermission() {
        TedPermission.with(this)
            .setPermissionListener(permissionlistener)
            .setDeniedMessage(getString(R.string.permission_denied_message))
            .setPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .check()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return super.onCreateOptionsMenu(menu)
        /*return if (isUpdateProfile()) {
            super.onCreateOptionsMenu(menu)
        } else {
            menuInflater.inflate(R.menu.menu_profile, menu)
            true
        }*/
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.miSkip -> {
                showHomePage()
                true
            }
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupToolBar() {
        setSupportActionBar(binding.tbProfile.toolbar)
        setToolbarTitle()
        setToolbarBackButton()
    }

    private fun setToolbarBackButton() {
        if (isUpdateProfile()) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)
        }
    }

    private fun setToolbarTitle() {
        supportActionBar?.title = if (isUpdateProfile()) {
            getString(R.string.label_edit_profile)
        } else {
            getString(R.string.label_update_profile)
        }
    }

    private fun isUpdateProfile() =
        intent.getBooleanExtra(ProfileActivityBundleKeyEnum.IS_UPDATE_PROFILE.value, false)

    private fun initDataBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile)
    }

    private fun setUserData() {
        binding.ivProfilePicture.loadUrlCircle(
            sharedPreferenceHelper.getUserProfileImage(),
            R.drawable.place_holder_profile
        )
        binding.edtName.setText(sharedPreferenceHelper.getUserName())
        binding.edtEmail.setText(sharedPreferenceHelper.getUserEmailId())
        binding.edtMobileNumber.setText(sharedPreferenceHelper.getUserMobileNumber())
        binding.edtBio.setText(sharedPreferenceHelper.getBio())
        binding.edtYouTubeLink.setText(sharedPreferenceHelper.getYouTubeProfileUrl())
    }

    private fun callGalleryPic() {
        val intent: Intent =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        imagePicActivityResultLauncher.launch(intent)
    }

    private var imagePicActivityResultLauncher = registerForActivityResult(
        StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.let { openCropImageScreen(it) }
        }
    }

    private fun openCropImageScreen(intent: Intent) {
        LogUtil.print(TAG, "File Path: " + intent.data)
        intent.data?.let { data ->
            UCrop.of(
                data,
                Uri.fromFile(File(cacheDir, System.currentTimeMillis().toString() + ".png"))
            ).withAspectRatio(1.0f, 1.0f).start(this)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            UCrop.REQUEST_CROP -> {
                if (resultCode == RESULT_OK) {
                    data?.let {
                        val resultUri = UCrop.getOutput(data)
                        resultUri?.let {
                            filePath = resultUri
                            setProfileImage()
                        }
                    }
                }
            }
        }
    }

    private fun setProfileImage() {
        filePath?.let { uri ->
            binding.ivProfilePicture.loadUriCircle(uri)
        }
    }

    private fun showHomePage() {
        HomeActivity.present(this)
        finish()
    }
}