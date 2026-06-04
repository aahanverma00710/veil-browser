package com.avcoding.veil.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.avcoding.veil.data.local.dao.BookmarkDao
import com.avcoding.veil.data.local.entity.BookmarkEntity

@Database(entities = [BookmarkEntity::class], version = 1, exportSchema = false)
abstract class BrowserDatabase : RoomDatabase() {
    abstract fun bookmarkDao(): BookmarkDao
    
    companion object {
        const val DATABASE_NAME = "browser_db"
    }
}
