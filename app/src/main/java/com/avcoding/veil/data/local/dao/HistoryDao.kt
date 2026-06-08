package com.avcoding.veil.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.avcoding.veil.data.local.entity.HistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {

    @Query("SELECT * FROM history ORDER BY visitedAt DESC")
    fun getAllHistory(): Flow<List<HistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(entity: HistoryEntity)

    @Query("DELETE FROM history WHERE id = :id")
    suspend fun deleteHistoryItem(id: Int)

    @Query("DELETE FROM history")
    suspend fun clearAllHistory()

    @Query("SELECT * FROM history WHERE visitedAt BETWEEN :start AND :end ORDER BY visitedAt DESC")
    fun getHistoryByDate(start: Long, end: Long): Flow<List<HistoryEntity>>
}
