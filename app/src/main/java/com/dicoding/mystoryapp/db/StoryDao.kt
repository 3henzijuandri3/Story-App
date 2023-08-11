package com.dicoding.mystoryapp.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dicoding.mystoryapp.adapter.CardStory

@Dao
interface StoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuote(quote: List<CardStory>)

    @Query("SELECT * FROM stories")
    fun getAllQuote(): PagingSource<Int, CardStory>

    @Query("DELETE FROM stories")
    suspend fun deleteAll()
}