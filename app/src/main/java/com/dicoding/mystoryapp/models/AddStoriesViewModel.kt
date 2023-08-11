package com.dicoding.mystoryapp.models

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.mystoryapp.api_settings.ApiConfig
import com.dicoding.mystoryapp.api_settings.responses.AddStoryResponse
import com.dicoding.mystoryapp.helper.Event
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response

class AddStoriesViewModel(application: Application) : ViewModel() {

    private val _addStoriesResponse = MutableLiveData<AddStoryResponse>()
    val addStoriesResponse: LiveData<AddStoryResponse> = _addStoriesResponse

    private val _responseMessage = MutableLiveData<Event<String>>()
    val responseMessage: LiveData<Event<String>> = _responseMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun uploadStory(
        authToken: String,
        image: MultipartBody.Part,
        desc: RequestBody,
        lat: RequestBody?,
        lon: RequestBody?
    ) {
        _isLoading.value = true

        val client = ApiConfig.getApiService().addStory(authToken, image, desc, lat, lon)

        client.enqueue(object : retrofit2.Callback<AddStoryResponse> {
            override fun onResponse(
                call: Call<AddStoryResponse>,
                response: Response<AddStoryResponse>
            ) {

                if (response.isSuccessful) {
                    _isLoading.value = false

                    if (response.body() != null) {
                        _addStoriesResponse.value = response.body()
                        _responseMessage.value =
                            Event(_addStoriesResponse.value?.message.toString())
                    }

                } else {
                    _isLoading.value = false
                    try {
                        val errorBody = response.errorBody()?.string()
                        val jsonObject = JSONObject(errorBody ?: "")
                        val message = jsonObject.getString("message")
                        _responseMessage.value = Event(message)

                    } catch (e: Exception) {
                        Log.e(TAG, "Exception: ${e.message}")
                    }
                }
            }

            override fun onFailure(call: Call<AddStoryResponse>, t: Throwable) {
                _isLoading.value = false
                _responseMessage.value = Event(t.message.toString())
                Log.e(TAG, "onFailure : ${t.message}")
            }

        })
    }

    companion object {
        private const val TAG = "AddStoriesViewModel"
    }
}