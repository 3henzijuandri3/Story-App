package com.dicoding.mystoryapp.helper

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.mystoryapp.models.*

class ViewModelFactory private constructor(private val mApplication: Application) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            return RegisterViewModel(mApplication) as T

        } else if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(mApplication) as T

        } else if (modelClass.isAssignableFrom(StoriesViewModel::class.java)) {
            return StoriesViewModel(mApplication) as T

        } else if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
            return DetailViewModel(mApplication) as T

        } else if (modelClass.isAssignableFrom(AddStoriesViewModel::class.java)) {
            return AddStoriesViewModel(mApplication) as T

        }

        throw IllegalArgumentException("Unknown View Model Class : ${modelClass.name}")

    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        @JvmStatic
        fun getInstance(application: Application): ViewModelFactory {
            if (INSTANCE == null) {
                synchronized(ViewModelFactory::class.java) {
                    INSTANCE = ViewModelFactory(application)
                }
            }

            return INSTANCE as ViewModelFactory
        }
    }
}