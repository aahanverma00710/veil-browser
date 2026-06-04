package com.avcoding.veil.adblock

import android.content.Context
import android.net.Uri
import android.webkit.WebResourceResponse
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.ByteArrayInputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdBlocker @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val blocklist = mutableSetOf<String>()

    init {
        loadBlocklist()
    }

    private fun loadBlocklist() {
        try {
            context.assets.open("blocklist.txt").bufferedReader().useLines { lines ->
                lines.forEach { line ->
                    if (line.isNotBlank() && !line.startsWith("#")) {
                        blocklist.add(line.trim())
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun shouldBlock(url: String): Boolean {
        val uri = Uri.parse(url)
        val host = uri.host ?: return false
        return blocklist.any { host.contains(it) }
    }

    fun createEmptyResponse(): WebResourceResponse {
        return WebResourceResponse("text/plain", "utf-8", ByteArrayInputStream("".toByteArray()))
    }
}
