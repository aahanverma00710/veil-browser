package com.avcoding.veil.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val url: String,
    val title: String,
    val visitedAt: Long = System.currentTimeMillis(),
    val favicon: String? = null
)
