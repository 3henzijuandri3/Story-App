package com.dicoding.mystoryapp.repository

import androidx.lifecycle.LiveData
import androidx.paging.*
import com.dicoding.mystoryapp.adapter.CardStory
import com.dicoding.mystoryapp.api_settings.ApiService
import com.dicoding.mystoryapp.db.StoriesRoomDatabase

class StoriesRepository(
    private val storiesDatabase: StoriesRoomDatabase,
    private val apiService: ApiService
) {

    fun getStories(): LiveData<PagingData<CardStory>> {

        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),

            remoteMediator = StoryRemoteMediator(storiesDatabase, apiService),
            pagingSourceFactory = {
                storiesDatabase.storyDao().getAllQuote()
            }
        ).liveData

    }

}