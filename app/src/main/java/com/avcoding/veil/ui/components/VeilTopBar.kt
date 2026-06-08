package com.avcoding.veil.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.avcoding.veil.ui.theme.*
import java.net.URLEncoder

@Composable
fun VeilTopBar(
    isHomepage: Boolean,
    currentUrl: String = "",
    onUrlSubmit: (String) -> Unit,
    onSettingsClick: () -> Unit,
    isSecure: Boolean = true,
    isPrivate: Boolean = false,
    onRefresh: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (isHomepage) {
            HomepageSearchBar(
                modifier = Modifier.weight(1f),
                onSearch = onUrlSubmit,
                isPrivate = isPrivate
            )
        } else {
            BrowserUrlBar(
                modifier = Modifier.weight(1f),
                url = currentUrl,
                isSecure = isSecure,
                isPrivate = isPrivate,
                onUrlSubmit = onUrlSubmit,
                onRefresh = onRefresh
            )
        }

        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(VeilSurfaceItem)
                .clickable(onClick = onSettingsClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Settings",
                tint = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun HomepageSearchBar(
    modifier: Modifier = Modifier,
    onSearch: (String) -> Unit,
    isPrivate: Boolean = false
) {
    val bgColor = if (isPrivate) Color(0xFF818CF8).copy(alpha = 0.12f) else VeilSurfaceItem
    val borderColor = if (isPrivate) Color(0xFF818CF8).copy(alpha = 0.3f) else Color.Transparent

    var query by remember { mutableStateOf("") }
    var isFocused by remember { mutableStateOf(false) }

    val rowModifier = modifier
        .height(40.dp)
        .clip(RoundedCornerShape(20.dp))
        .background(bgColor)
        .then(
            if (isPrivate) Modifier.border(1.dp, borderColor, RoundedCornerShape(20.dp))
            else Modifier
        )
        .padding(horizontal = 12.dp)

    Row(
        modifier = rowModifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isPrivate) {
            Icon(
                imageVector = Icons.Default.Shield,
                contentDescription = "Private",
                tint = VeilAccent,
                modifier = Modifier.size(14.dp)
            )
            Spacer(Modifier.width(6.dp))
        }
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            tint = VeilIconInactive,
            modifier = Modifier.size(16.dp)
        )
        Spacer(Modifier.width(8.dp))
        Box(modifier = Modifier.weight(1f)) {
            if (query.isEmpty() && !isFocused) {
                Text(
                    text = "Search or enter URL",
                    color = VeilIconInactive,
                    fontSize = 14.sp
                )
            }
            BasicTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { isFocused = it.isFocused },
                singleLine = true,
                textStyle = TextStyle(color = Color.White, fontSize = 14.sp),
                cursorBrush = SolidColor(VeilAccent),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = {
                    val url = buildSearchUrl(query)
                    if (url.isNotEmpty()) {
                        onSearch(url)
                        query = ""
                    }
                })
            )
        }
        Spacer(Modifier.width(8.dp))
        Icon(
            imageVector = Icons.Default.Mic,
            contentDescription = "Voice search",
            tint = VeilIconInactive,
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
private fun BrowserUrlBar(
    modifier: Modifier = Modifier,
    url: String,
    isSecure: Boolean,
    isPrivate: Boolean = false,
    onUrlSubmit: (String) -> Unit,
    onRefresh: () -> Unit
) {
    var inputUrl by remember(url) { mutableStateOf(url) }

    Row(
        modifier = modifier
            .height(40.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(VeilSurfaceItem)
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isPrivate) {
            Icon(
                imageVector = Icons.Default.VisibilityOff,
                contentDescription = "Private mode",
                tint = VeilAccent,
                modifier = Modifier.size(12.dp)
            )
            Spacer(Modifier.width(4.dp))
        }
        Icon(
            imageVector = if (isSecure) Icons.Default.Lock else Icons.Default.LockOpen,
            contentDescription = null,
            tint = if (isSecure) VeilGreenSecure else VeilIconInactive,
            modifier = Modifier.size(12.dp)
        )
        Spacer(Modifier.width(6.dp))
        BasicTextField(
            value = inputUrl,
            onValueChange = { inputUrl = it },
            modifier = Modifier.weight(1f),
            singleLine = true,
            textStyle = TextStyle(color = Color.White, fontSize = 13.sp),
            cursorBrush = SolidColor(VeilAccent),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
            keyboardActions = KeyboardActions(onGo = {
                val finalUrl = buildSearchUrl(inputUrl)
                if (finalUrl.isNotEmpty()) onUrlSubmit(finalUrl)
            })
        )
        Spacer(Modifier.width(6.dp))
        Icon(
            imageVector = Icons.Default.Refresh,
            contentDescription = "Refresh",
            tint = VeilIconInactive,
            modifier = Modifier
                .size(16.dp)
                .clickable(onClick = onRefresh)
        )
    }
}

private fun buildSearchUrl(query: String): String {
    val trimmed = query.trim()
    if (trimmed.isEmpty()) return ""
    return when {
        trimmed.startsWith("http://") || trimmed.startsWith("https://") -> trimmed
        !trimmed.contains(" ") && trimmed.contains(".") -> "https://$trimmed"
        else -> "https://www.google.com/search?q=${URLEncoder.encode(trimmed, "UTF-8")}"
    }
}
