package com.avcoding.veil.di

import android.content.Context
import androidx.room.Room
import com.avcoding.veil.data.local.BrowserDatabase
import com.avcoding.veil.data.local.dao.BookmarkDao
import com.avcoding.veil.data.local.dao.HistoryDao
import com.avcoding.veil.data.repository.HistoryRepository
import com.avcoding.veil.util.PrivateModeManager
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
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideBookmarkDao(database: BrowserDatabase): BookmarkDao {
        return database.bookmarkDao()
    }

    @Provides
    fun provideHistoryDao(database: BrowserDatabase): HistoryDao {
        return database.historyDao()
    }

    @Provides
    @Singleton
    fun provideHistoryRepository(
        historyDao: HistoryDao,
        privateModeManager: PrivateModeManager
    ): HistoryRepository {
        return HistoryRepository(historyDao, privateModeManager)
    }
}
