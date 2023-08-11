package com.dicoding.mystoryapp.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.dicoding.mystoryapp.adapter.CardStory
import com.dicoding.mystoryapp.api_settings.ApiService
import com.dicoding.mystoryapp.api_settings.responses.ListStoryItem
import com.dicoding.mystoryapp.ui.MainActivity

class StoryPagingSource(private val apiService: ApiService) : PagingSource<Int, CardStory>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CardStory> {
        return try {
            val position = params.key ?: INITIAL_PAGE_INDEX
            val responseData = apiService.getStoriesPaging(
                MainActivity.TOKEN.toString(),
                position,
                params.loadSize
            )
            val listCardStory = getCardStory(responseData.listStory)

            LoadResult.Page(
                data = listCardStory,
                prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
                nextKey = if (listCardStory.isNullOrEmpty()) null else position + 1
            )
        } catch (exception: Exception) {
            return LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, CardStory>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    private fun getCardStory(stories: List<ListStoryItem>): List<CardStory> {
        val listCard = ArrayList<CardStory>()

        for (storyData in stories) {
            val cardStory = CardStory(
                storyData.photoUrl.toString(),
                storyData.name.toString(),
                storyData.createdAt.toString(),
                storyData.id.toString()
            )

            listCard.add(cardStory)
        }

        return listCard
    }

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }
}