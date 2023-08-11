package com.dicoding.mystoryapp.ui.splash_screen

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.mystoryapp.databinding.ActivitySplashScreenBinding
import com.dicoding.mystoryapp.db.UserPreference
import com.dicoding.mystoryapp.ui.MainActivity
import com.dicoding.mystoryapp.ui.welcome_activity.WelcomeActivity

@Suppress("DEPRECATION")
class SplashScreen : AppCompatActivity() {

    private var _splashScreenBinding: ActivitySplashScreenBinding? = null
    private val binding get() = _splashScreenBinding
    private val SPLASH_SCREEN_LENGTH: Long = 3200

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _splashScreenBinding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        playSplashAnimation()
        moveNext()
    }

    private fun playSplashAnimation() {
        val titleStory_opacity = ObjectAnimator.ofFloat(binding?.titleStory, View.ALPHA, 0f, 1f)
            .apply { duration = 2000 }
        val titleStory_moveX =
            ObjectAnimator.ofFloat(binding?.titleStory, View.TRANSLATION_X, -300f, 0f)
                .apply { duration = 2000 }
        val animateTitleStory = AnimatorSet().apply {
            playTogether(titleStory_opacity, titleStory_moveX)
        }

        val titleApp_opacity =
            ObjectAnimator.ofFloat(binding?.titleApp, View.ALPHA, 0f, 1f).apply { duration = 2000 }
        val titleApp_moveX = ObjectAnimator.ofFloat(binding?.titleApp, View.TRANSLATION_X, 300f, 0f)
            .apply { duration = 2000 }
        val animateTitleApp = AnimatorSet().apply {
            playTogether(titleApp_opacity, titleApp_moveX)
        }

        AnimatorSet().apply {
            playTogether(animateTitleStory, animateTitleApp)
            start()
        }
    }

    private fun moveNext() {
        Handler().postDelayed({
            val userPref = UserPreference(this)
            val intent: Intent

            if (userPref.getUser().token.toString().isNotEmpty()) {
                intent = Intent(this@SplashScreen, MainActivity::class.java)
            } else {
                intent = Intent(this@SplashScreen, WelcomeActivity::class.java)
            }

            startActivity(intent)
            finish()

        }, SPLASH_SCREEN_LENGTH)
    }

    override fun onDestroy() {
        super.onDestroy()
        _splashScreenBinding = null
    }
}