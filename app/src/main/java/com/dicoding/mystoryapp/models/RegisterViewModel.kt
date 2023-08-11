package com.dicoding.mystoryapp.models

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.mystoryapp.api_settings.ApiConfig
import com.dicoding.mystoryapp.api_settings.responses.RegisterResponse
import com.dicoding.mystoryapp.helper.Event
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response

class RegisterViewModel(application: Application) : ViewModel() {

    companion object {
        private const val TAG = "RegisterViewModel"
    }

    private val _registerResponse = MutableLiveData<RegisterResponse>()
    val registerResponse: LiveData<RegisterResponse> = _registerResponse

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _responseMessage = MutableLiveData<Event<String>>()
    val responseMessage: LiveData<Event<String>> = _responseMessage

    fun postRegisterUser(name: String, email: String, password: String) {
        _isLoading.value = true

        val client = ApiConfig.getApiService().userRegister(name, email, password)

        if (client != null) {
            client.enqueue(object : retrofit2.Callback<RegisterResponse> {

                override fun onResponse(
                    call: Call<RegisterResponse>,
                    response: Response<RegisterResponse>
                ) {

                    if (response.isSuccessful) {
                        _isLoading.value = false

                        _registerResponse.value = response.body()
                        _responseMessage.value = Event(_registerResponse.value?.message.toString())

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

                override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                    _isLoading.value = false
                    _responseMessage.value = Event(t.message.toString())
                    Log.e(TAG, "onFailure : ${t.message}")
                }

            })
        }

    }
}