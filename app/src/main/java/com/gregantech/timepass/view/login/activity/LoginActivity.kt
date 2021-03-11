package com.gregantech.timepass.view.login.activity

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.animation.doOnEnd
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.gregantech.timepass.R
import com.gregantech.timepass.base.TimePassBaseActivity
import com.gregantech.timepass.base.TimePassBaseResult
import com.gregantech.timepass.databinding.ActivityLoginBinding
import com.gregantech.timepass.network.repository.LoginRepository
import com.gregantech.timepass.util.constant.VIEW_MODEL_IN_ACCESSIBLE_MESSAGE
import com.gregantech.timepass.util.extension.applyTextHyperLink
import com.gregantech.timepass.util.extension.openWebLink
import com.gregantech.timepass.util.extension.toast
import com.gregantech.timepass.util.extension.visible
import com.gregantech.timepass.util.sharedpreference.SharedPreferenceHelper
import com.gregantech.timepass.view.home.activity.HomeActivity
import com.gregantech.timepass.view.login.viewmodel.LoginViewModel
import com.gregantech.timepass.view.profile.activity.ProfileActivity
import kotlinx.android.synthetic.main.activity_login.*
import java.util.concurrent.TimeUnit

class LoginActivity : TimePassBaseActivity() {

    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var verificationId: String
    private lateinit var binding: ActivityLoginBinding
    private var hasAnimationStarted = false
    private lateinit var phoneAuthCallBacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private val firebaseAuth = Firebase.auth

    private lateinit var viewModelFactory: LoginViewModel.Factory
    private var sharedPreferenceHelper = SharedPreferenceHelper

    private val viewModel: LoginViewModel by lazy {
        requireNotNull(this) {
            VIEW_MODEL_IN_ACCESSIBLE_MESSAGE
        }
        ViewModelProvider(this, viewModelFactory).get(LoginViewModel::class.java)
    }

    companion object {
        fun present(context: Context) {
            val intent = Intent(context, LoginActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initDataBinding()
        setupPhoneAuthCallBacks()
        setupOnClickListener()
        setupViewModelFactory()
    }

    private fun setupViewModelFactory() {
        viewModelFactory = LoginViewModel.Factory(LoginRepository())
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
                showMobileNumberView()
            }
        }
    }

    private fun setupPhoneAuthCallBacks() {
        phoneAuthCallBacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // verification completed
                // fetchLogin()
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                dismissProgressBar()
                showToast("Verification Failed")

                if (e is FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    binding.tilMobileNumber.error = getString(R.string.invalid_phone_number)
                } else if (e is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    showToast(getString(R.string.quoto_exceed))
                }
                binding.btnGetOtp.isEnabled = true
            }

            override fun onCodeSent(
                verifyId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                super.onCodeSent(verifyId, token)
                dismissProgressBar()
                showMobileNumberView(false)
                showOtpView()
                verificationId = verifyId
                resendToken = token
            }
        }
    }

    private fun showOtpView(isShow: Boolean = true) {
        binding.btnVerifyOtp.visible(isShow)
        binding.llOtp.visible(isShow)
    }

    private fun showMobileNumberView(isShow: Boolean = true) {
        binding.btnGetOtp.visible(isShow)
        binding.llMobileNumber.visible(isShow)
        binding.llTermsAndPrivacy.visible(isShow)
    }

    private fun showToast(message: String) {
        message.toast(this)
    }

    private fun initDataBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        initHyperLinks()
    }

    private fun initHyperLinks() {
        tvTermsAndPrivacy.applyTextHyperLink(
            arrayOf(getString(R.string.terms_of_service), getString(R.string.privacy_policy)),
            R.color.colorAccent,
            true,
            R.font.roboto_medium
        ) {
            when (it) {
                getString(R.string.terms_of_service) -> openWebLink(getString(R.string.terms_and_conditions_url))
                getString(R.string.privacy_policy) -> openWebLink(getString(R.string.privacy_policy_url))
            }
        }
    }

    private fun setupOnClickListener() {
        binding.btnGetOtp.setOnClickListener {
            onClickGenerateOtp()
        }
        binding.btnVerifyOtp.setOnClickListener {
            onClickButtonVerifyOtp()
        }
    }

    private fun onClickGenerateOtp() {
        if (isValidated()) {
            binding.btnGetOtp.isEnabled = false
            startPhoneNumberVerification(getMobileNumber())
        }
    }

    private fun isValidated(): Boolean {
        return when {
            binding.edtMobileNumber.text.toString().isEmpty() -> {
                binding.tilMobileNumber.error = getString(R.string.enter_mobile_number)
                false
            }
            binding.edtMobileNumber.text.toString().length <= 6 -> {
                binding.tilMobileNumber.error = getString(R.string.invalid_phone_number)
                false
            }
            !binding.chkTermsAndPrivacy.isChecked -> {
                binding.tilMobileNumber.error = null
                showToast(getString(R.string.agree_to_terms_and_privacy))
                false
            }
            else -> {
                binding.tilMobileNumber.error = null
                true
            }
        }
    }

    private fun getMobileNumber() =
        binding.countryPicker.selectedCountryCodeWithPlus.plus(binding.edtMobileNumber.text)

    private fun onClickButtonVerifyOtp() {
        if (binding.edtOtp.text.toString().isNotBlank()) {
            binding.btnVerifyOtp.isEnabled = false
            val credential =
                PhoneAuthProvider.getCredential(verificationId, binding.edtOtp.text.toString())
            signInWithPhoneAuthCredential(credential)
        } else {
            binding.tilOtp.error = getString(R.string.enter_otp)
        }
    }

    private fun fetchLogin() {
        showProgressBar()
        viewModel.fetchLogin(getMobileNumber()).observe(this, Observer {
            when (it.status) {
                TimePassBaseResult.Status.SUCCESS -> {
                    dismissProgressBar()
                    it.data?.user?.let { user ->
                        sharedPreferenceHelper.setUserData(user)
                        navigateNextScreen(it.data.new_user ?: false)
                    }
                }
                TimePassBaseResult.Status.LOADING -> {
                }
                else -> {
                    dismissProgressBar()
                    it.message?.toast(this)
                }
            }
        })
    }

    private fun navigateNextScreen(isNewUser: Boolean) {
        if (isNewUser) {
            showProfilePage()
        } else {
            showHomePage()
        }
    }

    private fun showProfilePage() {
        ProfileActivity.present(this, false)
        finish()
    }

    private fun showHomePage() {
        HomeActivity.present(this)
        finish()
    }

    private fun startPhoneNumberVerification(phoneNumber: String) {
        showProgressBar()
        val options = PhoneAuthOptions
            .newBuilder(firebaseAuth)
            .setActivity(this)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setCallbacks(phoneAuthCallBacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        showProgressBar()
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                dismissProgressBar()
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    fetchLogin()
                } else {
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                        binding.tilOtp.error = getString(R.string.invalid_otp)
                    }
                    binding.btnVerifyOtp.isEnabled = true
                }
            }
    }
}