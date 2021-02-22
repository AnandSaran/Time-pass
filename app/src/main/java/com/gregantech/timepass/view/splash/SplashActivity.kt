package com.gregantech.timepass.view.splash

import android.content.Context
import android.content.Intent
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

    companion object {
        fun present(context: Context) {
            val intent = Intent(context, SplashActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            context.startActivity(intent)
        }
    }
}