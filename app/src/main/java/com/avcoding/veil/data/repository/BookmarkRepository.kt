package com.avcoding.veil.data.repository

import com.avcoding.veil.data.local.dao.BookmarkDao
import com.avcoding.veil.data.local.entity.BookmarkEntity
import com.avcoding.veil.domain.model.Bookmark
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookmarkRepository @Inject constructor(
    private val bookmarkDao: BookmarkDao
) {
    val allBookmarks: Flow<List<Bookmark>> = bookmarkDao.getAllBookmarks().map { entities ->
        entities.map { it.toDomain() }
    }

    suspend fun addBookmark(bookmark: Bookmark) {
        bookmarkDao.insertBookmark(BookmarkEntity.fromDomain(bookmark))
    }

    suspend fun deleteBookmark(bookmark: Bookmark) {
        bookmarkDao.deleteBookmark(BookmarkEntity.fromDomain(bookmark))
    }
}
