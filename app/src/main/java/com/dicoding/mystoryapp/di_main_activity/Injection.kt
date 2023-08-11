package com.dicoding.mystoryapp.di_main_activity

import android.content.Context
import com.dicoding.mystoryapp.api_settings.ApiConfig
import com.dicoding.mystoryapp.db.StoriesRoomDatabase
import com.dicoding.mystoryapp.repository.StoriesRepository

object Injection {
    fun provideRepository(context: Context): StoriesRepository {
        val database = StoriesRoomDatabase.getDatabase(context)
        val apiService = ApiConfig.getApiService()
        return StoriesRepository(database, apiService)
    }
}