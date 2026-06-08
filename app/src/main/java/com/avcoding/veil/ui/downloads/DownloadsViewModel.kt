package com.avcoding.veil.ui.downloads

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avcoding.veil.data.local.entity.DownloadEntity
import com.avcoding.veil.data.local.entity.DownloadStatus
import com.avcoding.veil.data.repository.DownloadRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DownloadsViewModel @Inject constructor(
    private val downloadRepository: DownloadRepository
) : ViewModel() {

    val downloads: StateFlow<List<DownloadEntity>> = downloadRepository.getAllDownloads()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        startPolling()
    }

    private fun startPolling() {
        viewModelScope.launch {
            while (true) {
                delay(2_000L)
                try {
                    updateActiveDownloadStatuses()
                } catch (e: Exception) {
                    // Ignore transient errors during polling
                }
            }
        }
    }

    private suspend fun updateActiveDownloadStatuses() {
        val allDownloads = downloadRepository.getAllDownloads().first()
        val active = allDownloads.filter {
            (it.status == DownloadStatus.DOWNLOADING || it.status == DownloadStatus.PENDING) &&
                it.downloadManagerId != -1L
        }
        for (download in active) {
            val newStatus = downloadRepository.queryDownloadStatus(download.downloadManagerId)
            if (newStatus != download.status) {
                downloadRepository.updateStatus(download.id, newStatus)
            }
        }
    }

    fun deleteDownload(id: Int) {
        viewModelScope.launch { downloadRepository.deleteDownload(id) }
    }

    fun clearAll() {
        viewModelScope.launch { downloadRepository.clearAllDownloads() }
    }

    fun cancelDownload(entity: DownloadEntity) {
        viewModelScope.launch {
            if (entity.downloadManagerId != -1L) {
                downloadRepository.cancelDownload(entity.downloadManagerId)
            }
            downloadRepository.deleteDownload(entity.id)
        }
    }

    fun retryDownload(entity: DownloadEntity) {
        viewModelScope.launch {
            downloadRepository.deleteDownload(entity.id)
            downloadRepository.startDownload(entity.url, entity.fileName, entity.mimeType)
        }
    }

    fun formatFileSize(bytes: Long): String = when {
        bytes < 0 -> "—"
        bytes < 1_024 -> "$bytes B"
        bytes < 1_024 * 1_024 -> "${bytes / 1_024} KB"
        bytes < 1_024L * 1_024 * 1_024 -> "${"%.1f".format(bytes / (1_024.0 * 1_024))} MB"
        else -> "${"%.1f".format(bytes / (1_024.0 * 1_024 * 1_024))} GB"
    }

    fun getFileIcon(mimeType: String): ImageVector = when {
        mimeType.contains("pdf", ignoreCase = true) -> Icons.Default.PictureAsPdf
        mimeType.startsWith("image/") -> Icons.Default.Image
        mimeType.startsWith("video/") -> Icons.Default.Movie
        mimeType.startsWith("audio/") -> Icons.Default.MusicNote
        mimeType.contains("zip") || mimeType.contains("rar") ||
            mimeType.contains("tar") || mimeType.contains("gzip") -> Icons.Default.Archive
        else -> Icons.Default.InsertDriveFile
    }

    fun getIconColor(mimeType: String): Long = when {
        mimeType.contains("pdf", ignoreCase = true) -> 0xFFDC2626L
        mimeType.startsWith("image/") -> 0xFF2563EBL
        mimeType.startsWith("video/") -> 0xFF7C3AEDL
        mimeType.startsWith("audio/") -> 0xFF059669L
        mimeType.contains("zip") || mimeType.contains("rar") ||
            mimeType.contains("tar") || mimeType.contains("gzip") -> 0xFFD97706L
        else -> 0xFF6B7280L
    }
}
