package com.dicoding.mystoryapp.ui.welcome_activity

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.mystoryapp.R
import com.dicoding.mystoryapp.databinding.ActivityWelcomeBinding
import com.dicoding.mystoryapp.ui.login_activity.LoginActivity
import com.dicoding.mystoryapp.ui.register_activity.RegisterActivity

class WelcomeActivity : AppCompatActivity(), View.OnClickListener {

    private var _welcomeActivityBinding: ActivityWelcomeBinding? = null
    private val binding get() = _welcomeActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _welcomeActivityBinding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setupView()
        playWelcomeAnimation()

        binding?.btnLogin?.setOnClickListener(this)
        binding?.btnRegister?.setOnClickListener(this)
    }

    private fun playWelcomeAnimation() {
        val title =
            ObjectAnimator.ofFloat(binding?.welcomeTitle, View.ALPHA, 1f).apply { duration = 800 }
        val welcome_Text =
            ObjectAnimator.ofFloat(binding?.welcomeText, View.ALPHA, 1f).apply { duration = 800 }

        val btn_loginAlpha =
            ObjectAnimator.ofFloat(binding?.btnLogin, View.ALPHA, 1f).apply { duration = 800 }
        val btn_loginTranslate =
            ObjectAnimator.ofFloat(binding?.btnLogin, View.TRANSLATION_Y, 200f, 0f)
                .apply { duration = 800 }
        val animateBtnLogin = AnimatorSet().apply {
            playTogether(btn_loginAlpha, btn_loginTranslate)
        }

        val btn_registerAlpha =
            ObjectAnimator.ofFloat(binding?.btnRegister, View.ALPHA, 1f).apply { duration = 800 }
        val btn_registerTranslate =
            ObjectAnimator.ofFloat(binding?.btnRegister, View.TRANSLATION_Y, 200f, 0f)
                .apply { duration = 800 }
        val animateBtnRegister = AnimatorSet().apply {
            playTogether(btn_registerAlpha, btn_registerTranslate)
        }

        val imgOpacity =
            ObjectAnimator.ofFloat(binding?.logoWelcome, View.ALPHA, 1f).apply { duration = 1000 }
        val imgTranslate =
            ObjectAnimator.ofFloat(binding?.logoWelcome, View.TRANSLATION_Y, -200f, 0f)
                .apply { duration = 1000 }
        val animateImg = AnimatorSet().apply {
            playTogether(imgOpacity, imgTranslate)
        }

        AnimatorSet().apply {
            playSequentially(animateImg, title, welcome_Text, animateBtnLogin, animateBtnRegister)
            startDelay = 300
            start()
        }
    }

    private fun moveToLogin() {
        val intentLogin = Intent(this, LoginActivity::class.java)
        startActivity(intentLogin)
    }

    private fun moveToRegister() {
        val intentRegister = Intent(this, RegisterActivity::class.java)
        startActivity(intentRegister)
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

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_login -> moveToLogin()
            R.id.btn_register -> moveToRegister()
        }
    }
}