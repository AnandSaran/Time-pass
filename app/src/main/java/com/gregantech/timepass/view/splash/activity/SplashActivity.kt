package com.gregantech.timepass.view.splash.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.installations.FirebaseInstallations
import com.google.gson.Gson
import com.gregantech.timepass.BuildConfig
import com.gregantech.timepass.R
import com.gregantech.timepass.base.TimePassBaseActivity
import com.gregantech.timepass.base.TimePassBaseResult
import com.gregantech.timepass.fcm.*
import com.gregantech.timepass.model.playback.PlaybackInfoModel
import com.gregantech.timepass.network.repository.AdvertisementRepository
import com.gregantech.timepass.network.repository.LoginRepository
import com.gregantech.timepass.util.AdvertisementHandler
import com.gregantech.timepass.util.constant.VIEW_MODEL_IN_ACCESSIBLE_MESSAGE
import com.gregantech.timepass.util.sharedpreference.SharedPreferenceHelper
import com.gregantech.timepass.view.home.activity.HomeActivity
import com.gregantech.timepass.view.live.activity.LiveVideoPlayerActivity
import com.gregantech.timepass.view.login.activity.LoginActivity
import com.gregantech.timepass.view.login.viewmodel.LoginViewModel
import com.gregantech.timepass.view.profile.activity.UserProfileActivity
import com.gregantech.timepass.view.splash.viewmodel.AdvertisementViewModel
import org.json.JSONObject

class SplashActivity : TimePassBaseActivity() {

    private lateinit var viewModelFactory: AdvertisementViewModel.Factory
    private lateinit var loginViewModelFactory: LoginViewModel.Factory

    private val viewModel: AdvertisementViewModel by lazy {
        requireNotNull(this) {
            VIEW_MODEL_IN_ACCESSIBLE_MESSAGE
        }
        ViewModelProvider(this, viewModelFactory).get(AdvertisementViewModel::class.java)
    }

    private val loginViewModel: LoginViewModel by lazy {
        requireNotNull(this) {
            VIEW_MODEL_IN_ACCESSIBLE_MESSAGE
        }
        ViewModelProvider(this, loginViewModelFactory).get(LoginViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        subscribeToTopic()
        setupViewModelFactory()
        subscribeToObservers()
    }

    private fun subscribeToTopic() {
        CloudMessageTopicHelper().subscribeDefaultTopics()
    }

    private fun subscribeToObservers() {

        FirebaseInstallations.getInstance().getToken(false).addOnCompleteListener {
            if (it.isComplete) {
                Log.d(TAG, "subscribeToObservers: token ${it.result.token}")
                SharedPreferenceHelper.setFCMToken(it.result.token)
            } else
                Log.e(TAG, "subscribeToObservers: error fetching FCM token")
        }


        viewModel.getAdStatus().observe(this, Observer { result ->
            when (result.status) {
                TimePassBaseResult.Status.SUCCESS -> {
                    AdvertisementHandler.advertisementResponse = result.data
                    if (SharedPreferenceHelper.isUserLoggedIn())
                        doFetchLiveStatus()
                    else
                        Handler().post { navigateNextScreen() }
                }
                TimePassBaseResult.Status.LOADING -> {
                }
                TimePassBaseResult.Status.ERROR -> Handler().post { navigateNextScreen() }
            }
        })
    }

    private fun doFetchLiveStatus() {
        loginViewModel.fetchLogin(SharedPreferenceHelper.getUserMobileNumber())
            .observe(this, Observer {
                when (it.status) {
                    TimePassBaseResult.Status.SUCCESS -> {
                        dismissProgressBar()
                        it.data?.user?.let { user ->
                            SharedPreferenceHelper.setUserData(user)
                            Handler().post { navigateNextScreen() }
                        }
                    }
                    TimePassBaseResult.Status.LOADING -> {
                    }
                    TimePassBaseResult.Status.ERROR -> Handler().post { navigateNextScreen() }
                }
            })
    }

    private fun setupViewModelFactory() {
        viewModelFactory = AdvertisementViewModel.Factory(
            AdvertisementRepository()
        )
        loginViewModelFactory = LoginViewModel.Factory(LoginRepository())
    }

    private fun isFromFCM(): Boolean {
        intent?.extras?.run {
            //toString("SplashScreen")
            getString("from")?.let {
                when (it) {
                    "/topics/Live" -> {
                        get("content")?.let { content ->
                            val fcmData =
                                Gson().fromJson(content.toString(), FCMDataModel::class.java)
                            if (fcmData != null) {
                                if (SharedPreferenceHelper.getUserId() != fcmData.user?.userID) {
                                    if (fcmData.liveStatus == true) {
                                        val playbackInfoModel = PlaybackInfoModel(
                                            fcmData.message ?: "Streaming Live",
                                            BuildConfig.ANT_URL + fcmData.user?.streamID,
                                            fcmData.user?.streamID!!,
                                            true
                                        )
                                        LiveVideoPlayerActivity.present(
                                            this@SplashActivity,
                                            playbackInfoModel
                                        )
                                    }
                                }
                                return true
                            }
                        }
                    }
                    "/topics/Post" -> {
                        get("content")?.let { content ->
                            val jsonObj = JSONObject(content.toString())
                            val videoJsonArr = jsonObj.getJSONArray("video")
                            val videoObj = videoJsonArr[0]
                            if (videoObj.toString().isNotEmpty()) {
                                val videoData = Gson().fromJson(
                                    videoObj.toString(),
                                    FCMPostResponse::class.java
                                )
                                videoData?.followerId?.let { id ->
                                    if (SharedPreferenceHelper.getUserId() != id) {
                                        UserProfileActivity.present(this@SplashActivity, id)
                                        return true
                                    }
                                }
                            }
                        }
                    }
                    else -> Log.e("Splash", "from key required is missing")
                }
            }
        }
        return false
    }

    private fun navigateNextScreen() {
        if (SharedPreferenceHelper.isUserLoggedIn()) {
            if (!isFromFCM())
                HomeActivity.present(this)
        } else {
            LoginActivity.present(this)
        }
        finish()
    }

    companion object {
        fun present(context: Context) {
            val intent = Intent(context, SplashActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            context.startActivity(intent)
        }
    }
}