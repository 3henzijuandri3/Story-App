package com.dicoding.mystoryapp.ui.register_activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.dicoding.mystoryapp.R
import com.dicoding.mystoryapp.databinding.ActivityRegisterBinding
import com.dicoding.mystoryapp.helper.ViewModelFactory
import com.dicoding.mystoryapp.models.RegisterViewModel
import com.dicoding.mystoryapp.ui.login_activity.LoginActivity
import com.dicoding.mystoryapp.ui.welcome_activity.WelcomeActivity

@Suppress("DEPRECATION")
class RegisterActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var registerViewModel: RegisterViewModel

    private var _activityRegisterBinding: ActivityRegisterBinding? = null
    private val binding get() = _activityRegisterBinding

    private lateinit var registerMessage: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _activityRegisterBinding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        // Pembuatan View Model Register
        registerViewModel = obtainViewModel(this@RegisterActivity)
        registerViewModel.isLoading.observe(this) { showLoading(it) }

        registerViewModel.registerResponse.observe(this) { isSuccess ->
            if (isSuccess.error == false) moveToLogin()
        }

        registerViewModel.responseMessage.observe(this) {
            it.getContentIfNotHandled()?.let { message ->
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }

        setupView()
        binding?.btnRegister?.setOnClickListener(this)
    }

    private fun obtainViewModel(activity: AppCompatActivity): RegisterViewModel {
        val factory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory).get(RegisterViewModel::class.java)
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

    private fun warnEmptyEd(name: String, email: String, password: String) {
        if (name.isEmpty()) {
            binding?.edRegisterName?.error = "Please Input Your Name"
        }

        if (email.isEmpty()) {
            binding?.edRegisterEmail?.error = "Please Input Your Email"
        }

        if (password.isEmpty()) {
            binding?.edRegisterPassword?.error = "Please Input Your Password"
        }
    }

    private fun showLoading(state: Boolean) {
        binding?.progressBar?.visibility = if (state) View.VISIBLE else View.GONE
    }

    private fun moveToLogin() {
        val intentToLogin = Intent(this, LoginActivity::class.java)
        startActivity(intentToLogin)
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
            R.id.btn_register -> {
                val edNamaText = binding?.edRegisterName
                val edEmailText = binding?.edRegisterEmail
                val edPasswordText = binding?.edRegisterPassword

                if (edNamaText?.error.isNullOrEmpty() || edEmailText?.error.isNullOrEmpty() || edPasswordText?.error.isNullOrEmpty()) {
                    warnEmptyEd(edNamaText?.text.toString(), edEmailText?.text.toString(), edPasswordText?.text.toString())

                }

                if (edNamaText?.text?.isNotEmpty()!! && edEmailText?.text?.isNotEmpty()!! && edPasswordText?.text?.isNotEmpty()!! && edPasswordText.text.toString().length >= 8) {
                    registerViewModel.postRegisterUser(edNamaText.text.toString(), edEmailText.text.toString(), edPasswordText.text.toString())
                }
            }
        }

    }
}