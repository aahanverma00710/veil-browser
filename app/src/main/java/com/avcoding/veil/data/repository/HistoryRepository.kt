package com.avcoding.veil.data.repository

import com.avcoding.veil.data.local.dao.HistoryDao
import com.avcoding.veil.data.local.entity.HistoryEntity
import com.avcoding.veil.util.PrivateModeManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Calendar
import java.util.Locale

class HistoryRepository(
    private val historyDao: HistoryDao,
    private val privateModeManager: PrivateModeManager
) {

    fun getAllHistory(): Flow<List<HistoryEntity>> = historyDao.getAllHistory()

    suspend fun addHistory(url: String, title: String) {
        if (privateModeManager.isPrivate.value) return
        historyDao.insertHistory(
            HistoryEntity(
                url = url,
                title = title.ifBlank { url },
                favicon = extractDomainFirstLetter(url)
            )
        )
    }

    suspend fun deleteHistoryItem(id: Int) = historyDao.deleteHistoryItem(id)

    suspend fun clearAllHistory() = historyDao.clearAllHistory()

    fun getHistoryByDate(start: Long, end: Long): Flow<List<HistoryEntity>> =
        historyDao.getHistoryByDate(start, end)

    fun getGroupedHistory(): Flow<Map<String, List<HistoryEntity>>> {
        return historyDao.getAllHistory().map { items ->
            val todayStart = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
            val yesterdayStart = todayStart - 86_400_000L

            val grouped = LinkedHashMap<String, MutableList<HistoryEntity>>()
            for (item in items) {
                val key = when {
                    item.visitedAt >= todayStart -> "TODAY"
                    item.visitedAt >= yesterdayStart -> "YESTERDAY"
                    else -> {
                        val cal = Calendar.getInstance().apply { timeInMillis = item.visitedAt }
                        val month = cal.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault())
                            ?.uppercase(Locale.getDefault()) ?: ""
                        val day = cal.get(Calendar.DAY_OF_MONTH)
                        "$month $day"
                    }
                }
                grouped.getOrPut(key) { mutableListOf() }.add(item)
            }
            grouped
        }
    }

    private fun extractDomainFirstLetter(url: String): String {
        return try {
            val host = java.net.URI(url).host ?: return url.take(1).uppercase(Locale.getDefault())
            host.removePrefix("www.").take(1).uppercase(Locale.getDefault())
        } catch (e: Exception) {
            url.take(1).uppercase(Locale.getDefault())
        }
    }
}
