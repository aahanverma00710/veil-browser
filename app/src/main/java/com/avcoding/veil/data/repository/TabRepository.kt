package com.avcoding.veil.data.repository

import com.avcoding.veil.domain.model.Tab
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TabRepository @Inject constructor() {
    private val _tabs = MutableStateFlow<List<Tab>>(emptyList())
    val tabs: StateFlow<List<Tab>> = _tabs.asStateFlow()

    private val _activeTabId = MutableStateFlow<String?>(null)
    val activeTabId: StateFlow<String?> = _activeTabId.asStateFlow()

    fun addTab(url: String, isPrivate: Boolean = false): Tab {
        val newTab = Tab(
            id = UUID.randomUUID().toString(),
            title = if (url.isEmpty()) "New Tab" else url,
            url = url,
            isPrivate = isPrivate
        )
        _tabs.value = _tabs.value + newTab
        _activeTabId.value = newTab.id
        return newTab
    }

    fun closeTab(tabId: String) {
        val wasActive = _activeTabId.value == tabId
        val remaining = _tabs.value.filter { it.id != tabId }
        if (remaining.isEmpty()) {
            val defaultTab = Tab(
                id = UUID.randomUUID().toString(),
                title = "New Tab",
                url = ""
            )
            _tabs.value = listOf(defaultTab)
            _activeTabId.value = defaultTab.id
        } else {
            _tabs.value = remaining
            if (wasActive) {
                _activeTabId.value = remaining.last().id
            }
        }
    }

    fun switchTab(tabId: String) {
        _activeTabId.value = tabId
    }

    fun updateActiveTab(title: String? = null, url: String? = null) {
        val activeId = _activeTabId.value ?: return
        _tabs.value = _tabs.value.map { tab ->
            if (tab.id == activeId) {
                tab.copy(
                    title = title ?: tab.title,
                    url = url ?: tab.url
                )
            } else tab
        }
    }

    fun getActiveTabId(): String? = _activeTabId.value
    fun getTab(tabId: String): Tab? = _tabs.value.firstOrNull { it.id == tabId }
    fun getPrivateTabs(): List<Tab> = _tabs.value.filter { it.isPrivate }
    fun getNormalTabs(): List<Tab> = _tabs.value.filter { !it.isPrivate }
}
