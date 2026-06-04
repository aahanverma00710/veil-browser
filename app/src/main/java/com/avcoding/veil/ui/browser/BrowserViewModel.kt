package com.avcoding.veil.ui.browser

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avcoding.veil.adblock.AdBlocker
import com.avcoding.veil.data.repository.BookmarkRepository
import com.avcoding.veil.data.repository.TabRepository
import com.avcoding.veil.domain.model.Bookmark
import com.avcoding.veil.domain.model.Tab
import dagger.hilt.android.lifecycle.HiltViewModel
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
    val tabs: List<Tab> = emptyList()
)

@HiltViewModel
class BrowserViewModel @Inject constructor(
    private val tabRepository: TabRepository,
    private val bookmarkRepository: BookmarkRepository,
    val adBlocker: AdBlocker
) : ViewModel() {

    private val _uiState = MutableStateFlow(BrowserUiState())
    val uiState: StateFlow<BrowserUiState> = _uiState.asStateFlow()

    init {
        tabRepository.tabs
            .onEach { tabs ->
                _uiState.update { it.copy(tabs = tabs) }
            }
            .launchIn(viewModelScope)
    }

    fun onUrlChanged(url: String) {
        _uiState.update { it.copy(currentUrl = url) }
    }

    fun onLoadingStateChanged(isLoading: Boolean) {
        _uiState.update { it.copy(isLoading = isLoading) }
    }

    fun onProgressChanged(progress: Int) {
        _uiState.update { it.copy(progress = progress) }
    }

    fun onTitleChanged(title: String) {
        _uiState.update { it.copy(title = title) }
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
}
