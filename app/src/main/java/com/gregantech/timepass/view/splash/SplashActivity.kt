package com.gregantech.timepass.view.splash

import android.os.Bundle
import android.os.Handler
import com.gregantech.timepass.R
import com.gregantech.timepass.base.TimePassBaseActivity
import com.gregantech.timepass.util.sharedpreference.SharedPreferenceHelper
import com.gregantech.timepass.view.home.activity.HomeActivity
import com.gregantech.timepass.view.login.activity.LoginActivity

class SplashActivity : TimePassBaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Handler().postDelayed({ navigateNextScreen() }, 0)
    }

    private fun navigateNextScreen() {
        if (SharedPreferenceHelper.isUserLoggedIn()) {
            HomeActivity.present(this)
        } else {
            LoginActivity.present(this)
        }
        finish()
    }
}