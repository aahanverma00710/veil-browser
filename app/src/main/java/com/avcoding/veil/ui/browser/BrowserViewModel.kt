package com.avcoding.veil.ui.browser

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avcoding.veil.adblock.AdBlocker
import com.avcoding.veil.data.repository.BookmarkRepository
import com.avcoding.veil.data.repository.DownloadRepository
import com.avcoding.veil.data.repository.HistoryRepository
import com.avcoding.veil.data.repository.TabRepository
import com.avcoding.veil.domain.model.Bookmark
import com.avcoding.veil.domain.model.Tab
import com.avcoding.veil.util.PrivateModeManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BrowserUiState(
    val currentUrl: String = "",
    val isLoading: Boolean = false,
    val progress: Int = 0,
    val isAdBlockEnabled: Boolean = true,
    val canGoBack: Boolean = false,
    val canGoForward: Boolean = false,
    val title: String = "New Tab",
    val tabs: List<Tab> = emptyList(),
    val activeTabId: String? = null,
    val isPrivate: Boolean = false
)

@HiltViewModel
class BrowserViewModel @Inject constructor(
    private val tabRepository: TabRepository,
    private val bookmarkRepository: BookmarkRepository,
    private val historyRepository: HistoryRepository,
    private val downloadRepository: DownloadRepository,
    val adBlocker: AdBlocker,
    val privateModeManager: PrivateModeManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(BrowserUiState())
    val uiState: StateFlow<BrowserUiState> = _uiState.asStateFlow()

    private val _downloadEvent = Channel<String>(Channel.BUFFERED)
    val downloadEvent: Flow<String> = _downloadEvent.receiveAsFlow()

    init {
        tabRepository.tabs
            .onEach { tabs -> _uiState.update { it.copy(tabs = tabs) } }
            .launchIn(viewModelScope)

        tabRepository.activeTabId
            .onEach { id -> _uiState.update { it.copy(activeTabId = id) } }
            .launchIn(viewModelScope)

        privateModeManager.isPrivate
            .onEach { private -> _uiState.update { it.copy(isPrivate = private) } }
            .launchIn(viewModelScope)
    }

    fun newTab(url: String = "", isPrivate: Boolean = false): String {
        val tab = tabRepository.addTab(url, isPrivate)
        return tab.id
    }

    fun switchTab(tabId: String) {
        tabRepository.switchTab(tabId)
        val tab = tabRepository.getTab(tabId)
        _uiState.update {
            it.copy(
                currentUrl = tab?.url ?: "",
                title = tab?.title ?: "New Tab"
            )
        }
    }

    fun closeTab(tabId: String) {
        tabRepository.closeTab(tabId)
    }

    fun onUrlChanged(url: String) {
        _uiState.update { it.copy(currentUrl = url) }
        tabRepository.updateActiveTab(url = url)
    }

    fun onLoadingStateChanged(isLoading: Boolean) {
        _uiState.update { it.copy(isLoading = isLoading) }
    }

    fun onProgressChanged(progress: Int) {
        _uiState.update { it.copy(progress = progress) }
    }

    fun onTitleChanged(title: String) {
        _uiState.update { it.copy(title = title) }
        tabRepository.updateActiveTab(title = title)
    }

    fun onNavigationStateChanged(canGoBack: Boolean, canGoForward: Boolean) {
        _uiState.update { it.copy(canGoBack = canGoBack, canGoForward = canGoForward) }
    }

    fun toggleAdBlock() {
        _uiState.update { it.copy(isAdBlockEnabled = !it.isAdBlockEnabled) }
    }

    fun addBookmark() {
        viewModelScope.launch {
            bookmarkRepository.addBookmark(
                Bookmark(
                    title = _uiState.value.title,
                    url = _uiState.value.currentUrl
                )
            )
        }
    }

    fun addToHistory(url: String, title: String) {
        viewModelScope.launch {
            historyRepository.addHistory(url, title)
        }
    }

    fun startDownload(url: String, fileName: String, mimeType: String) {
        viewModelScope.launch {
            try {
                downloadRepository.startDownload(url, fileName, mimeType)
                _downloadEvent.trySend("Downloading $fileName…")
            } catch (e: Exception) {
                _downloadEvent.trySend("Download failed: ${e.message}")
            }
        }
    }

    fun setPrivate(value: Boolean) {
        privateModeManager.setPrivate(value)
    }

    fun togglePrivate() {
        privateModeManager.togglePrivate()
    }
}
