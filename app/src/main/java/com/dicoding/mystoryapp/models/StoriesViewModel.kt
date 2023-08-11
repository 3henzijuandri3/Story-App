package com.dicoding.mystoryapp.models

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.mystoryapp.api_settings.ApiConfig
import com.dicoding.mystoryapp.api_settings.responses.StoriesResponse
import com.dicoding.mystoryapp.ui.maps_activity.MapsActivity
import retrofit2.Call
import retrofit2.Response

class StoriesViewModel(application: Application) : ViewModel() {

    private val _storiesMapResponse = MutableLiveData<StoriesResponse>()
    val storiesMapResponse: LiveData<StoriesResponse> = _storiesMapResponse

    init {
        MapsActivity.TOKEN?.let { fetchMapStories(it) }
    }

    fun fetchMapStories(authToken: String) {

        val client = ApiConfig.getApiService().getMapStories(authToken)
        client.enqueue(object : retrofit2.Callback<StoriesResponse> {
            override fun onResponse(
                call: Call<StoriesResponse>,
                response: Response<StoriesResponse>
            ) {
                if (response.isSuccessful) {
                    _storiesMapResponse.value = response.body()

                } else {
                    Log.e(TAG, "Gagal Fetching")
                }
            }

            override fun onFailure(call: Call<StoriesResponse>, t: Throwable) {
                Log.e(TAG, "onFailure : ${t.message}")
            }

        })
    }

    companion object {
        private const val TAG = "StoriesViewModel"
    }
}