package com.avcoding.veil.data.local

import androidx.room.TypeConverter
import com.avcoding.veil.data.local.entity.DownloadStatus

class BrowserTypeConverters {

    @TypeConverter
    fun fromDownloadStatus(status: DownloadStatus): String = status.name

    @TypeConverter
    fun toDownloadStatus(name: String): DownloadStatus =
        try { DownloadStatus.valueOf(name) } catch (e: IllegalArgumentException) { DownloadStatus.FAILED }
}
