package com.avcoding.veil.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.avcoding.veil.data.local.entity.DownloadEntity
import com.avcoding.veil.data.local.entity.DownloadStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface DownloadDao {

    @Query("SELECT * FROM downloads ORDER BY downloadedAt DESC")
    fun getAllDownloads(): Flow<List<DownloadEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDownload(entity: DownloadEntity): Long

    @Query("UPDATE downloads SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Int, status: DownloadStatus)

    @Query("UPDATE downloads SET downloadManagerId = :downloadManagerId WHERE id = :id")
    suspend fun updateDownloadManagerId(id: Int, downloadManagerId: Long)

    @Query("DELETE FROM downloads WHERE id = :id")
    suspend fun deleteDownload(id: Int)

    @Query("DELETE FROM downloads")
    suspend fun clearAllDownloads()

    @Query("SELECT * FROM downloads WHERE downloadManagerId = :downloadManagerId LIMIT 1")
    suspend fun getDownloadByManagerId(downloadManagerId: Long): DownloadEntity?
}
