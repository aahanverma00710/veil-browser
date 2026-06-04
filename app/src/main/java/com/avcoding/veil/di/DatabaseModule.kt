package com.avcoding.veil.di

import android.content.Context
import androidx.room.Room
import com.avcoding.veil.data.local.BrowserDatabase
import com.avcoding.veil.data.local.dao.BookmarkDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): BrowserDatabase {
        return Room.databaseBuilder(
            context,
            BrowserDatabase::class.java,
            BrowserDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    fun provideBookmarkDao(database: BrowserDatabase): BookmarkDao {
        return database.bookmarkDao()
    }
}
