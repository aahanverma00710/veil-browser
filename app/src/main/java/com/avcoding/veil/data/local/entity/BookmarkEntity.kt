package com.avcoding.veil.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.avcoding.veil.domain.model.Bookmark

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val url: String
) {
    fun toDomain() = Bookmark(id, title, url)
    
    companion object {
        fun fromDomain(bookmark: Bookmark) = BookmarkEntity(
            id = bookmark.id,
            title = bookmark.title,
            url = bookmark.url
        )
    }
}
