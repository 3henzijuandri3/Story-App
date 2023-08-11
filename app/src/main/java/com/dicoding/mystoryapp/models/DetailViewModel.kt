package com.dicoding.mystoryapp.models

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.mystoryapp.api_settings.ApiConfig
import com.dicoding.mystoryapp.api_settings.responses.StoriesDetailResponse
import com.dicoding.mystoryapp.ui.detail_activity.DetailActivity
import retrofit2.Call
import retrofit2.Response

class DetailViewModel(application: Application) : ViewModel() {

    private val _storyDetailResponse = MutableLiveData<StoriesDetailResponse>()
    val storyDetailResponse: LiveData<StoriesDetailResponse> = _storyDetailResponse

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        fetchStoryDetail(DetailActivity.ID.toString(), DetailActivity.TOKEN.toString())
    }

    private fun fetchStoryDetail(idStory: String, authToken: String) {
        _isLoading.value = true

        val client = ApiConfig.getApiService().getDetailStory(idStory, authToken)

        client.enqueue(object : retrofit2.Callback<StoriesDetailResponse> {
            override fun onResponse(
                call: Call<StoriesDetailResponse>,
                response: Response<StoriesDetailResponse>
            ) {

                if (response.isSuccessful) {
                    _storyDetailResponse.value = response.body()
                    _isLoading.value = false
                }

            }

            override fun onFailure(call: Call<StoriesDetailResponse>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message}")
                _isLoading.value = false
            }

        })
    }

    companion object {
        private const val TAG = "DetailViewModel"
    }

}