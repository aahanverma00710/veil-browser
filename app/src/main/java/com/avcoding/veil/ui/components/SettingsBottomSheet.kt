package com.avcoding.veil.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.avcoding.veil.ui.theme.VeilIconInactive
import com.avcoding.veil.ui.theme.VeilSurfaceItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsBottomSheet(
    bookmarkCount: Int = 0,
    isAdBlockEnabled: Boolean = true,
    onAdBlockToggle: () -> Unit = {},
    onDismiss: () -> Unit,
    onBookmarks: () -> Unit = {},
    onHistory: () -> Unit = {},
    onDownloads: () -> Unit = {}
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = "Veil",
                style = MaterialTheme.typography.labelSmall,
                color = VeilIconInactive,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            SettingsRow(
                icon = Icons.Default.Bookmark,
                title = "Bookmarks",
                subtitle = "$bookmarkCount saved sites",
                onClick = { onBookmarks(); onDismiss() }
            )
            SettingsRow(
                icon = Icons.Default.History,
                title = "History",
                subtitle = "Browsing history",
                onClick = { onHistory(); onDismiss() }
            )

            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

            SettingsRow(
                icon = Icons.Default.FileDownload,
                title = "Downloads",
                subtitle = "Downloaded files",
                onClick = { onDownloads(); onDismiss() }
            )

            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

            ListItem(
                headlineContent = { Text("AdBlock") },
                supportingContent = { Text("Block ads & trackers") },
                leadingContent = {
                    SettingsIconBox(icon = Icons.Default.Shield)
                },
                trailingContent = {
                    Switch(
                        checked = isAdBlockEnabled,
                        onCheckedChange = { onAdBlockToggle() }
                    )
                }
            )
            SettingsRow(
                icon = Icons.Default.Search,
                title = "Search Engine",
                subtitle = "Google / DuckDuckGo",
                onClick = {}
            )

            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

            SettingsRow(
                icon = Icons.Default.Palette,
                title = "Theme",
                subtitle = "Dark / Light / System",
                onClick = {}
            )
        }
    }
}

@Composable
private fun SettingsRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    ListItem(
        headlineContent = { Text(title) },
        supportingContent = {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = VeilIconInactive
            )
        },
        leadingContent = { SettingsIconBox(icon = icon) },
        modifier = Modifier.clickable(onClick = onClick)
    )
}

@Composable
private fun SettingsIconBox(icon: ImageVector) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(VeilSurfaceItem),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )
    }
}
