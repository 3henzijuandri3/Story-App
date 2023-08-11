package com.dicoding.mystoryapp.models

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.dicoding.mystoryapp.DataDummy
import com.dicoding.mystoryapp.MainDispatcherRule
import com.dicoding.mystoryapp.adapter.CardStory
import com.dicoding.mystoryapp.adapter.StoriesAdapter
import com.dicoding.mystoryapp.getOrAwaitValue
import com.dicoding.mystoryapp.repository.StoriesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest{
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    @Mock
    private lateinit var storyRepository: StoriesRepository

    @Test
    fun `when Get Story Should Not Null and Return Data`() = runTest {
        val dummyStory = DataDummy.generateDummyStory()
        val data: PagingData<CardStory> = StoryPagingSource.snapshot(dummyStory)
        val expectedStory = MutableLiveData<PagingData<CardStory>>()

        expectedStory.value = data
        Mockito.`when`(storyRepository.getStories()).thenReturn(expectedStory)

        val mainViewModel = MainViewModel(storyRepository)
        val actualStory : PagingData<CardStory> = mainViewModel.stories.getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoriesAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualStory)

        Assert.assertNotNull(differ.snapshot())
        Assert.assertEquals(dummyStory.size, differ.snapshot().size)
        Assert.assertEquals(dummyStory[0], differ.snapshot()[0]) // TANDAIN

    }

    @Test
    fun `when Get Story Empty Should Return No Data`() = runTest {
        val data: PagingData<CardStory> = PagingData.from(emptyList())
        val expectedQuote = MutableLiveData<PagingData<CardStory>>()

        expectedQuote.value = data
        Mockito.`when`(storyRepository.getStories()).thenReturn(expectedQuote)

        val mainViewModel = MainViewModel(storyRepository)
        val actualQuote: PagingData<CardStory> = mainViewModel.stories.getOrAwaitValue()
        val differ = AsyncPagingDataDiffer(
            diffCallback = StoriesAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )

        differ.submitData(actualQuote)
        Assert.assertEquals(0, differ.snapshot().size)
    }

}

class StoryPagingSource : PagingSource<Int, LiveData<List<CardStory>>>(){

    override fun getRefreshKey(state: PagingState<Int, LiveData<List<CardStory>>>): Int? {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<CardStory>>> {
        return LoadResult.Page(emptyList(), 0, 1)
    }


    companion object {
        fun snapshot(items: List<CardStory>): PagingData<CardStory> {
            return PagingData.from(items)
        }
    }

}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}























