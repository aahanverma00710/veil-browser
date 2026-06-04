package com.avcoding.veil.domain.model

data class Tab(
    val id: String,
    val title: String,
    val url: String,
    val lastAccessed: Long = System.currentTimeMillis()
)
