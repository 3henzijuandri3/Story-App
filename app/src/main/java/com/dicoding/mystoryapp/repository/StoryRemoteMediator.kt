package com.dicoding.mystoryapp.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.dicoding.mystoryapp.adapter.CardStory
import com.dicoding.mystoryapp.api_settings.ApiService
import com.dicoding.mystoryapp.api_settings.responses.ListStoryItem
import com.dicoding.mystoryapp.db.RemoteKeys
import com.dicoding.mystoryapp.db.StoriesRoomDatabase
import com.dicoding.mystoryapp.ui.MainActivity

@OptIn(ExperimentalPagingApi::class)
class StoryRemoteMediator(
    private val database: StoriesRoomDatabase,
    private val apiService: ApiService
) : RemoteMediator<Int, CardStory>() {

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, CardStory>
    ): MediatorResult {

        val page = when (loadType) {

            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: INITIAL_PAGE_INDEX
            }

            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                prevKey
            }

            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                nextKey
            }

        }

        try {
            val responseData = apiService.getStoriesPaging(
                MainActivity.TOKEN.toString(),
                page,
                state.config.pageSize
            )
            val listCardStory = getCardStory(responseData.listStory)
            val endOfPaginationReached = listCardStory.isEmpty()

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    database.remoteKeysDao().deleteRemoteKeys()
                    database.storyDao().deleteAll()
                }

                val prevKey = if (page == 1) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                val keys = listCardStory.map {
                    RemoteKeys(id = it.id, prevKey = prevKey, nextKey = nextKey)
                }
                database.remoteKeysDao().insertAll(keys)
                database.storyDao().insertQuote(listCardStory)
            }

            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)

        } catch (exception: Exception) {
            return MediatorResult.Error(exception)
        }

    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, CardStory>): RemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { data ->
            database.remoteKeysDao().getRemoteKeysId(data.id)
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, CardStory>): RemoteKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let { data ->
            database.remoteKeysDao().getRemoteKeysId(data.id)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, CardStory>): RemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                database.remoteKeysDao().getRemoteKeysId(id)
            }
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



















