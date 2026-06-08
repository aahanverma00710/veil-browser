package com.avcoding.veil.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avcoding.veil.data.local.entity.HistoryEntity
import com.avcoding.veil.data.repository.HistoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val historyRepository: HistoryRepository
) : ViewModel() {

    val groupedHistory: StateFlow<Map<String, List<HistoryEntity>>> =
        historyRepository.getGroupedHistory()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyMap())

    fun clearAll() {
        viewModelScope.launch { historyRepository.clearAllHistory() }
    }

    fun deleteItem(id: Int) {
        viewModelScope.launch { historyRepository.deleteHistoryItem(id) }
    }
}
