package com.avcoding.veil.data.repository

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import com.avcoding.veil.data.local.dao.DownloadDao
import com.avcoding.veil.data.local.entity.DownloadEntity
import com.avcoding.veil.data.local.entity.DownloadStatus
import kotlinx.coroutines.flow.Flow
import java.io.File

class DownloadRepository(
    private val context: Context,
    private val downloadDao: DownloadDao
) {
    private val downloadManager =
        context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

    suspend fun startDownload(url: String, fileName: String, mimeType: String): Long {
        val filePath = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            fileName
        ).absolutePath

        val entity = DownloadEntity(
            url = url,
            fileName = fileName,
            filePath = filePath,
            mimeType = mimeType,
            fileSize = -1L,
            status = DownloadStatus.PENDING
        )
        val rowId = downloadDao.insertDownload(entity)
        val dbId = rowId.toInt()

        val request = DownloadManager.Request(Uri.parse(url)).apply {
            setTitle(fileName)
            setDescription("Downloading via Veil Browser")
            setNotificationVisibility(
                DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED
            )
            setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
            setAllowedOverMetered(true)
            setAllowedOverRoaming(false)
            addRequestHeader("User-Agent", "VeilBrowser/1.0")
            if (mimeType.isNotBlank()) setMimeType(mimeType)
        }

        val dmId = downloadManager.enqueue(request)
        downloadDao.updateDownloadManagerId(dbId, dmId)
        downloadDao.updateStatus(dbId, DownloadStatus.DOWNLOADING)

        return dbId.toLong()
    }

    fun getAllDownloads(): Flow<List<DownloadEntity>> = downloadDao.getAllDownloads()

    suspend fun deleteDownload(id: Int) = downloadDao.deleteDownload(id)

    suspend fun clearAllDownloads() = downloadDao.clearAllDownloads()

    suspend fun updateStatus(id: Int, status: DownloadStatus) =
        downloadDao.updateStatus(id, status)

    fun queryDownloadStatus(downloadManagerId: Long): DownloadStatus {
        val query = DownloadManager.Query().setFilterById(downloadManagerId)
        val cursor = downloadManager.query(query)
        return try {
            if (cursor != null && cursor.moveToFirst()) {
                val col = cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS)
                when (cursor.getInt(col)) {
                    DownloadManager.STATUS_SUCCESSFUL -> DownloadStatus.COMPLETED
                    DownloadManager.STATUS_FAILED -> DownloadStatus.FAILED
                    DownloadManager.STATUS_RUNNING -> DownloadStatus.DOWNLOADING
                    DownloadManager.STATUS_PAUSED -> DownloadStatus.DOWNLOADING
                    DownloadManager.STATUS_PENDING -> DownloadStatus.PENDING
                    else -> DownloadStatus.FAILED
                }
            } else {
                DownloadStatus.FAILED
            }
        } finally {
            cursor?.close()
        }
    }

    fun cancelDownload(downloadManagerId: Long) {
        downloadManager.remove(downloadManagerId)
    }

    fun getUriForDownload(downloadManagerId: Long): Uri? =
        downloadManager.getUriForDownloadedFile(downloadManagerId)
}
