package com.dicoding.mystoryapp.ui

import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.mystoryapp.R
import com.dicoding.mystoryapp.adapter.CardStory
import com.dicoding.mystoryapp.adapter.LoadingStateAdapter
import com.dicoding.mystoryapp.adapter.StoriesAdapter
import com.dicoding.mystoryapp.api_settings.responses.ListStoryItem
import com.dicoding.mystoryapp.databinding.ActivityMainBinding
import com.dicoding.mystoryapp.db.UserAuth
import com.dicoding.mystoryapp.db.UserPreference
import com.dicoding.mystoryapp.models.MainViewModel
import com.dicoding.mystoryapp.models.MainViewModelFactory
import com.dicoding.mystoryapp.ui.add_activity.AddStoryActivity
import com.dicoding.mystoryapp.ui.maps_activity.MapsActivity
import com.dicoding.mystoryapp.ui.welcome_activity.WelcomeActivity

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    private var _acitivityMainBinding: ActivityMainBinding? = null
    private val binding get() = _acitivityMainBinding
    private lateinit var userAuth: UserAuth

    private val mainViewModel: MainViewModel by viewModels {
        MainViewModelFactory(this)
    }

    private val open: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.rotate_open_anim
        )
    }
    private val close: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.rotate_close_anim
        )
    }
    private var btnClicked: Boolean = false
    private var isAnimationRunning: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _acitivityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setupView()
        userAuth()
        TOKEN = "Bearer " + userAuth.token.toString()

        // Pembuatan RecylerView
        val layoutManager = LinearLayoutManager(this)
        binding?.rvStories?.layoutManager = layoutManager

        // View Model
        getStoriesData()

        // Logic Floating Button
        binding?.apply {
            mainFloatingButton.setOnClickListener { mainFloatingClicked() }

            mapFloatingButton.setOnClickListener {
                mainFloatingClicked()
                val intent = Intent(this@MainActivity, MapsActivity::class.java)
                startActivity(intent)
            }

            addFloatingButton.setOnClickListener {
                mainFloatingClicked()
                val intent = Intent(this@MainActivity, AddStoryActivity::class.java)
                startActivity(intent)
            }

            logoutFloatingButton.setOnClickListener {
                val userPref = UserPreference(this@MainActivity)
                userPref.userLogout()

                val intent = Intent(this@MainActivity, WelcomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
        }

        // Disable Click Button
        if (btnClicked == false) {
            binding?.addFloatingButton?.isClickable = false
            binding?.logoutFloatingButton?.isClickable = false
            binding?.mapFloatingButton?.isClickable = false
        }
    }

    private fun getStoriesData() {
        val adapter = StoriesAdapter()
        binding?.rvStories?.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )

        mainViewModel.stories.observe(this) {
            adapter.submitData(lifecycle, it)
        }
    }

    private fun mainFloatingClicked() {
        if (isAnimationRunning == false) {
            changeClickable()
            animateFloatingBtn()
            btnClicked = !btnClicked
        }
    }

    private fun animateFloatingBtn() {
        if (!btnClicked) {
            binding?.mainFloatingButton?.startAnimation(open)
            showFloatingBtn()
            disableScreenClick()
        } else {
            binding?.mainFloatingButton?.startAnimation(close)
            hideFloatingBtn()
            enableScreenClick()
        }
    }

    private fun disableScreenClick() {
        binding?.darkOverlay?.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    true
                }
                MotionEvent.ACTION_UP -> {
                    mainFloatingClicked()
                    binding?.darkOverlay?.performClick()
                    true
                }
                else -> false
            }
        }
    }

    private fun enableScreenClick() {
        binding?.darkOverlay?.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    false
                }
                MotionEvent.ACTION_UP -> {
                    binding?.darkOverlay?.performClick()
                    false
                }
                else -> false
            }

        }
    }

    private fun showFloatingBtn() {

        if (isAnimationRunning == false) {

            isAnimationRunning = true

            val translateShowAdd =
                ObjectAnimator.ofFloat(binding?.addFloatingButton, View.TRANSLATION_Y, 200f, 0f)
                    .apply { duration = 300 }
            val alphaShowAdd = ObjectAnimator.ofFloat(binding?.addFloatingButton, View.ALPHA, 1f)
                .apply { duration = 300 }

            val translateShowLogout =
                ObjectAnimator.ofFloat(binding?.logoutFloatingButton, View.TRANSLATION_Y, 200f, 0f)
                    .apply { duration = 300 }
            val alphaShowLogout =
                ObjectAnimator.ofFloat(binding?.logoutFloatingButton, View.ALPHA, 1f)
                    .apply { duration = 300 }

            val translateShowMap =
                ObjectAnimator.ofFloat(binding?.mapFloatingButton, View.TRANSLATION_Y, 200f, 0f)
                    .apply { duration = 300 }
            val alphaShowMap = ObjectAnimator.ofFloat(binding?.mapFloatingButton, View.ALPHA, 1f)
                .apply { duration = 300 }

            val showAddBtn = AnimatorSet().apply {
                playTogether(alphaShowAdd, translateShowAdd)
            }

            val showLogoutBtn = AnimatorSet().apply {
                playTogether(alphaShowLogout, translateShowLogout)
            }

            val showMapBtn = AnimatorSet().apply {
                playTogether(alphaShowMap, translateShowMap)
            }

            ObjectAnimator.ofFloat(binding?.darkOverlay, View.ALPHA, 1f).apply {
                duration = 150
                start()
            }

            AnimatorSet().apply {
                playSequentially(showAddBtn, showMapBtn, showLogoutBtn)
                startDelay = 200

                addListener(object : AnimatorListener {
                    override fun onAnimationStart(p0: Animator) {
                        // None
                    }

                    override fun onAnimationEnd(p0: Animator) {
                        isAnimationRunning = false
                    }

                    override fun onAnimationCancel(p0: Animator) {
                        // None
                    }

                    override fun onAnimationRepeat(p0: Animator) {
                        // None
                    }

                })

                start()
            }
        }
    }

    private fun hideFloatingBtn() {
        if (isAnimationRunning == false) {

            isAnimationRunning = true

            val translateHideAdd =
                ObjectAnimator.ofFloat(binding?.addFloatingButton, View.TRANSLATION_Y, 200f)
                    .apply { duration = 150 }
            val alphaHideAdd = ObjectAnimator.ofFloat(binding?.addFloatingButton, View.ALPHA, 0f)
                .apply { duration = 150 }

            val translateHideLogout =
                ObjectAnimator.ofFloat(binding?.logoutFloatingButton, View.TRANSLATION_Y, 200f)
                    .apply { duration = 150 }
            val alphaHideLogout =
                ObjectAnimator.ofFloat(binding?.logoutFloatingButton, View.ALPHA, 0f)
                    .apply { duration = 150 }

            val translateHideMap =
                ObjectAnimator.ofFloat(binding?.mapFloatingButton, View.TRANSLATION_Y, 200f)
                    .apply { duration = 150 }
            val alphaHideMap = ObjectAnimator.ofFloat(binding?.mapFloatingButton, View.ALPHA, 0f)
                .apply { duration = 150 }

            val hideAddBtn = AnimatorSet().apply {
                playTogether(alphaHideAdd, translateHideAdd)
            }

            val hideLogoutBtn = AnimatorSet().apply {
                playTogether(alphaHideLogout, translateHideLogout)
            }

            val hideMapBtn = AnimatorSet().apply {
                playTogether(alphaHideMap, translateHideMap)
            }

            ObjectAnimator.ofFloat(binding?.darkOverlay, View.ALPHA, 0f).apply {
                duration = 150
                addListener(object : AnimatorListener {
                    override fun onAnimationStart(p0: Animator) {
                        // None
                    }

                    override fun onAnimationEnd(p0: Animator) {
                        isAnimationRunning = false
                    }

                    override fun onAnimationCancel(p0: Animator) {
                        // None
                    }

                    override fun onAnimationRepeat(p0: Animator) {
                        // None
                    }

                })
                start()
            }

            AnimatorSet().apply {
                playSequentially(hideAddBtn, hideMapBtn, hideLogoutBtn)
                start()
            }
        }
    }

    private fun changeClickable() {
        if (!btnClicked) {
            binding?.addFloatingButton?.isClickable = true
            binding?.logoutFloatingButton?.isClickable = true
            binding?.mapFloatingButton?.isClickable = true
        } else {
            binding?.addFloatingButton?.isClickable = false
            binding?.logoutFloatingButton?.isClickable = false
            binding?.mapFloatingButton?.isClickable = false
        }
    }

    private fun userAuth() {
        val userPref = UserPreference(this)
        userAuth = userPref.getUser()
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
        if (userAuth.token?.isNotEmpty() == true) {
            finishAffinity()
        }
    }

    companion object {
        var TOKEN: String? = null
        var IS_FROM_ADDSTORY: Boolean = false
    }
}
























