package com.avcoding.veil.util

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PrivateModeManager @Inject constructor() {
    private val _isPrivate = MutableStateFlow(false)
    val isPrivate: StateFlow<Boolean> = _isPrivate.asStateFlow()

    fun togglePrivate() {
        _isPrivate.value = !_isPrivate.value
    }

    fun setPrivate(value: Boolean) {
        _isPrivate.value = value
    }
}
