package com.dicoding.mystoryapp.ui.login_activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.dicoding.mystoryapp.R
import com.dicoding.mystoryapp.databinding.ActivityLoginBinding
import com.dicoding.mystoryapp.db.UserAuth
import com.dicoding.mystoryapp.db.UserPreference
import com.dicoding.mystoryapp.helper.ViewModelFactory
import com.dicoding.mystoryapp.models.LoginViewModel
import com.dicoding.mystoryapp.ui.MainActivity
import com.dicoding.mystoryapp.ui.welcome_activity.WelcomeActivity

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var userAuth: UserAuth

    private lateinit var loginViewModel: LoginViewModel

    private var _activityLoginBinding: ActivityLoginBinding? = null
    private val binding get() = _activityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _activityLoginBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        // Proses Pembuatan Login View Model
        loginViewModel = obtainViewModel(this@LoginActivity)

        loginViewModel.isLoading.observe(this) { showLoading(it) }

        loginViewModel.loginResponse.observe(this) { response ->
            if (response.error == false) {
                val userName = response.loginResult?.name.toString()
                val userId = response.loginResult?.userId.toString()
                val userToken = response.loginResult?.token.toString()

                saveUserAuth(userName, userId, userToken)
                moveToHome()
            }
        }

        loginViewModel.responseMessage.observe(this) {
            it.getContentIfNotHandled()?.let { message ->
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }


        setupView()
        binding?.btnLogin?.setOnClickListener(this)
    }

    private fun saveUserAuth(name: String, id: String, token: String) {
        val userPreference = UserPreference(this)
        val userDataResponse = UserAuth()

        userDataResponse.name = name
        userDataResponse.id = id
        userDataResponse.token = token

        userAuth = userDataResponse

        userPreference.setUserLogin(userAuth)
        Log.e("A", userPreference.getUser().toString())
    }

    private fun moveToHome() {
        val intentToHome = Intent(this@LoginActivity, MainActivity::class.java)
        startActivity(intentToHome)
    }

    private fun showLoading(state: Boolean) {
        binding?.progressBar?.visibility = if (state) View.VISIBLE else View.GONE
    }

    private fun obtainViewModel(activity: AppCompatActivity): LoginViewModel {
        val factory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory).get(LoginViewModel::class.java)
    }

    private fun warnEmptyEd(email: String, password: String) {
        if (email.isEmpty()) {
            binding?.edLoginEmail?.error = "Please Input Your Email"
        }

        if (password.isEmpty()) {
            binding?.edLoginPassword?.error = "Please Input Your Password"
        }
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, WelcomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
        finish()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_login -> {
                val edEmailText = binding?.edLoginEmail
                val edPasswordText = binding?.edLoginPassword

                if (edEmailText?.error.isNullOrEmpty() || edPasswordText?.error.isNullOrEmpty()) {
                    warnEmptyEd(edEmailText?.text.toString(), edPasswordText?.text.toString())
                }

                if (edEmailText?.text?.isNotEmpty()!! && edPasswordText?.text?.isNotEmpty()!! && edPasswordText.text.toString().length >= 8) {
                    loginViewModel.postLoginUser(edEmailText.text.toString(), edPasswordText.text.toString())
                }
            }
        }
    }
}