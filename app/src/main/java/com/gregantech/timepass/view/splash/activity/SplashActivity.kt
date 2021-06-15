package com.gregantech.timepass.view.splash.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.gregantech.timepass.R
import com.gregantech.timepass.base.TimePassBaseActivity
import com.gregantech.timepass.base.TimePassBaseResult
import com.gregantech.timepass.network.repository.AdvertisementRepository
import com.gregantech.timepass.network.repository.LoginRepository
import com.gregantech.timepass.util.AdvertisementHandler
import com.gregantech.timepass.util.constant.VIEW_MODEL_IN_ACCESSIBLE_MESSAGE
import com.gregantech.timepass.util.sharedpreference.SharedPreferenceHelper
import com.gregantech.timepass.view.home.activity.HomeActivity
import com.gregantech.timepass.view.login.activity.LoginActivity
import com.gregantech.timepass.view.login.viewmodel.LoginViewModel
import com.gregantech.timepass.view.splash.viewmodel.AdvertisementViewModel

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
        setupViewModelFactory()
        subscribeToObservers()
    }

    private fun subscribeToObservers() {
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

    private fun navigateNextScreen() {
        if (SharedPreferenceHelper.isUserLoggedIn()) {
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