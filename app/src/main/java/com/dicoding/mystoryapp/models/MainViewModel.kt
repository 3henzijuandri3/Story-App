package com.dicoding.mystoryapp.models

import android.content.Context
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dicoding.mystoryapp.adapter.CardStory
import com.dicoding.mystoryapp.di_main_activity.Injection
import com.dicoding.mystoryapp.repository.StoriesRepository

class MainViewModel(private val storiesRepository: StoriesRepository) : ViewModel() {


    var stories: LiveData<PagingData<CardStory>> =
        storiesRepository.getStories().cachedIn(viewModelScope)

}

class MainViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(Injection.provideRepository(context)) as T
        }

        throw IllegalArgumentException("Unknown ViewModel")
    }

}