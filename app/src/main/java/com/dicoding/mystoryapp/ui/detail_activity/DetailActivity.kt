package com.dicoding.mystoryapp.ui.detail_activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.dicoding.mystoryapp.R
import com.dicoding.mystoryapp.api_settings.responses.StoriesDetailResponse
import com.dicoding.mystoryapp.databinding.ActivityDetailBinding
import com.dicoding.mystoryapp.db.UserAuth
import com.dicoding.mystoryapp.db.UserPreference
import com.dicoding.mystoryapp.helper.ViewModelFactory
import com.dicoding.mystoryapp.models.DetailViewModel

@Suppress("DEPRECATION")
class DetailActivity : AppCompatActivity(), View.OnClickListener {

    private var _activityDetailBinding: ActivityDetailBinding? = null
    private val binding get() = _activityDetailBinding

    private lateinit var detailStoryViewModel: DetailViewModel

    private lateinit var userAuth: UserAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _activityDetailBinding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        userAuth()
        TOKEN = "Bearer " + userAuth.token
        val idStory = intent.getStringExtra(ID)
        ID = idStory

        // Pembuatan Detail View Model
        detailStoryViewModel = obtainViewModel(this@DetailActivity)

        detailStoryViewModel.storyDetailResponse.observe(this) {
            setDetailData(it)
        }

        detailStoryViewModel.isLoading.observe(this) {
            showLoading(it)
        }

        binding?.btnBack?.setOnClickListener(this)
    }

    private fun setDetailData(detailStoryResponse: StoriesDetailResponse) {

        binding?.imageStoryUser?.let {
            Glide.with(this)
                .load(detailStoryResponse.story?.photoUrl)
                .into(it)
        }

        binding?.textNamaUser?.text = detailStoryResponse.story?.name
        binding?.textCreatedAt?.text = detailStoryResponse.story?.createdAt
        binding?.descTextUser?.text = detailStoryResponse.story?.description
    }

    private fun obtainViewModel(activity: AppCompatActivity): DetailViewModel {
        val factory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory).get(DetailViewModel::class.java)
    }

    private fun userAuth() {
        val userPref = UserPreference(this)
        userAuth = userPref.getUser()
    }

    private fun showLoading(state: Boolean) {
        binding?.progressBar?.visibility = if (state) View.VISIBLE else View.GONE
    }

    companion object {
        var TOKEN: String? = null
        var ID: String? = null
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_back -> {
                onBackPressed()
            }
        }
    }
}