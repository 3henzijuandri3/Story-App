package com.dicoding.mystoryapp.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dicoding.mystoryapp.adapter.CardStory

@Database(
    entities = [CardStory::class, RemoteKeys::class],
    version = 2,
    exportSchema = false
)
abstract class StoriesRoomDatabase : RoomDatabase() {

    abstract fun storyDao(): StoryDao
    abstract fun remoteKeysDao(): RemoteKeysDao

    companion object {
        @Volatile
        private var INSTANCE: StoriesRoomDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context): StoriesRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    StoriesRoomDatabase::class.java, "quote_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}