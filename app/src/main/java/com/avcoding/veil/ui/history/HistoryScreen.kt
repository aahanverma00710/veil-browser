package com.avcoding.veil.ui.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.avcoding.veil.ui.components.PillScreenType
import com.avcoding.veil.ui.components.SettingsBottomSheet
import com.avcoding.veil.ui.components.VeilBottomPill
import com.avcoding.veil.ui.components.VeilTopBar
import com.avcoding.veil.ui.theme.VeilBackground
import com.avcoding.veil.ui.theme.VeilIconInactive

@Composable
fun HistoryScreen(
    onNavigateToBrowser: (String) -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToBookmarks: () -> Unit,
    onNavigateToTabs: () -> Unit,
    onNavigateToDownloads: () -> Unit = {}
) {
    var sheetVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(VeilBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp)
        ) {
            VeilTopBar(
                isHomepage = false,
                currentUrl = "History",
                onUrlSubmit = onNavigateToBrowser,
                onSettingsClick = { sheetVisible = true },
                isSecure = false,
                onRefresh = {}
            )

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = VeilIconInactive
                    )
                    Text(
                        text = "No history yet",
                        color = Color.White.copy(alpha = 0.6f),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }

        VeilBottomPill(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            screenType = PillScreenType.HOME,
            activeDestination = "history",
            onHome = onNavigateToHome,
            onBookmarks = onNavigateToBookmarks,
            onNewTab = { onNavigateToBrowser("") },
            onHistory = {},
            onTabs = onNavigateToTabs
        )
    }

    if (sheetVisible) {
        SettingsBottomSheet(
            onDismiss = { sheetVisible = false },
            onBookmarks = onNavigateToBookmarks,
            onHistory = {},
            onDownloads = onNavigateToDownloads
        )
    }
}
