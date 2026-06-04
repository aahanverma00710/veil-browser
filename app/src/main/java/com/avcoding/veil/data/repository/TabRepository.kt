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

    fun createTab(url: String, title: String): Tab {
        val newTab = Tab(
            id = UUID.randomUUID().toString(),
            title = title,
            url = url
        )
        _tabs.value = _tabs.value + newTab
        return newTab
    }

    fun updateTab(updatedTab: Tab) {
        _tabs.value = _tabs.value.map {
            if (it.id == updatedTab.id) updatedTab else it
        }
    }

    fun closeTab(tabId: String) {
        _tabs.value = _tabs.value.filter { it.id != tabId }
    }
}
