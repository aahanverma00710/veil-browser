package com.avcoding.veil.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.avcoding.veil.data.local.dao.BookmarkDao
import com.avcoding.veil.data.local.dao.DownloadDao
import com.avcoding.veil.data.local.dao.HistoryDao
import com.avcoding.veil.data.local.entity.BookmarkEntity
import com.avcoding.veil.data.local.entity.DownloadEntity
import com.avcoding.veil.data.local.entity.HistoryEntity

@Database(
    entities = [BookmarkEntity::class, HistoryEntity::class, DownloadEntity::class],
    version = 3,
    exportSchema = false
)
@TypeConverters(BrowserTypeConverters::class)
abstract class BrowserDatabase : RoomDatabase() {
    abstract fun bookmarkDao(): BookmarkDao
    abstract fun historyDao(): HistoryDao
    abstract fun downloadDao(): DownloadDao

    companion object {
        const val DATABASE_NAME = "browser_db"
    }
}
