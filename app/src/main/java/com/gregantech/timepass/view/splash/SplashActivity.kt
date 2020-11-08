package com.gregantech.timepass.view.splash

import android.os.Bundle
import android.os.Handler
import com.gregantech.timepass.R
import com.gregantech.timepass.base.TimePassBaseActivity
import com.gregantech.timepass.util.constant.SharedPreferenceKey.USER_ID
import com.gregantech.timepass.util.sharedpreference.SharedPref
import com.gregantech.timepass.view.category.activity.CategoryActivity
import com.gregantech.timepass.view.login.LoginActivity

class SplashActivity : TimePassBaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Handler().postDelayed({ navigateNextScreen() }, 0)
    }

    private fun navigateNextScreen() {
        if (SharedPref.instance.getStringValue(this, USER_ID) != null) {
            CategoryActivity.present(this)
        } else {
            LoginActivity.present(this)
        }
        finish()
    }
}