package com.avcoding.veil.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.avcoding.veil.data.local.dao.BookmarkDao
import com.avcoding.veil.data.local.dao.HistoryDao
import com.avcoding.veil.data.local.entity.BookmarkEntity
import com.avcoding.veil.data.local.entity.HistoryEntity

@Database(
    entities = [BookmarkEntity::class, HistoryEntity::class],
    version = 2,
    exportSchema = false
)
abstract class BrowserDatabase : RoomDatabase() {
    abstract fun bookmarkDao(): BookmarkDao
    abstract fun historyDao(): HistoryDao

    companion object {
        const val DATABASE_NAME = "browser_db"
    }
}
