package com.avcoding.veil.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "downloads")
data class DownloadEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val url: String,
    val fileName: String,
    val filePath: String,
    val mimeType: String,
    val fileSize: Long = -1L,
    val downloadedAt: Long = System.currentTimeMillis(),
    val status: DownloadStatus = DownloadStatus.PENDING,
    val downloadManagerId: Long = -1L
)
