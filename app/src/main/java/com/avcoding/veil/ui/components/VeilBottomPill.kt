package com.avcoding.veil.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.avcoding.veil.ui.theme.*

enum class PillScreenType { HOME, BROWSER }

@Composable
fun VeilBottomPill(
    modifier: Modifier = Modifier,
    screenType: PillScreenType,
    activeDestination: String = "home",
    canGoBack: Boolean = false,
    canGoForward: Boolean = false,
    tabCount: Int = 0,
    onHome: () -> Unit = {},
    onBookmarks: () -> Unit = {},
    onNewTab: () -> Unit = {},
    onHistory: () -> Unit = {},
    onTabs: () -> Unit = {},
    onBack: () -> Unit = {},
    onForward: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(VeilPillBg)
            .border(0.5.dp, VeilPillBorder, RoundedCornerShape(20.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        if (screenType == PillScreenType.HOME) {
            HomePillRow(
                activeDestination = activeDestination,
                onHome = onHome,
                onBookmarks = onBookmarks,
                onNewTab = onNewTab,
                onHistory = onHistory,
                onTabs = onTabs
            )
        } else {
            BrowserPillRow(
                canGoBack = canGoBack,
                canGoForward = canGoForward,
                tabCount = tabCount,
                onBack = onBack,
                onForward = onForward,
                onTabs = onTabs,
                onHome = onHome
            )
        }
    }
}

@Composable
private fun HomePillRow(
    activeDestination: String,
    onHome: () -> Unit,
    onBookmarks: () -> Unit,
    onNewTab: () -> Unit,
    onHistory: () -> Unit,
    onTabs: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        PillIconButton(
            icon = Icons.Default.Home,
            isActive = activeDestination == "home",
            contentDescription = "Home",
            onClick = onHome
        )
        PillIconButton(
            icon = Icons.Default.Bookmark,
            isActive = activeDestination == "bookmarks",
            contentDescription = "Bookmarks",
            onClick = onBookmarks
        )
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(VeilAccent)
                .clickable(onClick = onNewTab),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "New Tab",
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
        }
        PillIconButton(
            icon = Icons.Default.History,
            isActive = activeDestination == "history",
            contentDescription = "History",
            onClick = onHistory
        )
        PillIconButton(
            icon = Icons.Default.Tab,
            isActive = activeDestination == "tabs",
            contentDescription = "Tabs",
            onClick = onTabs
        )
    }
}

@Composable
private fun BrowserPillRow(
    canGoBack: Boolean,
    canGoForward: Boolean,
    tabCount: Int,
    onBack: () -> Unit,
    onForward: () -> Unit,
    onTabs: () -> Unit,
    onHome: () -> Unit
) {
    Row(
        modifier = Modifier
            .height(52.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        PillIconButton(
            icon = Icons.AutoMirrored.Filled.ArrowBack,
            isActive = false,
            enabled = canGoBack,
            contentDescription = "Back",
            onClick = onBack
        )
        PillIconButton(
            icon = Icons.AutoMirrored.Filled.ArrowForward,
            isActive = false,
            enabled = canGoForward,
            contentDescription = "Forward",
            onClick = onForward
        )
        // Tabs pill button
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .border(1.5.dp, VeilAccent, RoundedCornerShape(10.dp))
                .background(VeilAccent.copy(alpha = 0.2f))
                .clickable(onClick = onTabs)
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Tab,
                contentDescription = "Tabs",
                tint = VeilAccent,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = tabCount.coerceIn(0, 99).toString(),
                color = VeilAccent,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 14.sp
            )
        }
        PillIconButton(
            icon = Icons.Default.Home,
            isActive = false,
            contentDescription = "Home",
            onClick = onHome
        )
    }
}

@Composable
private fun PillIconButton(
    icon: ImageVector,
    isActive: Boolean,
    enabled: Boolean = true,
    contentDescription: String?,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(44.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = when {
                isActive -> VeilAccent
                !enabled -> Color.White.copy(alpha = 0.25f)
                else -> VeilIconInactive
            },
            modifier = Modifier.size(22.dp)
        )
    }
}
