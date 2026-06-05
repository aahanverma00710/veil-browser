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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileBottomSheet(
    bookmarkCount: Int = 0,
    onDismiss: () -> Unit,
    onBookmarks: () -> Unit,
    onHistory: () -> Unit,
    onDownloads: () -> Unit,
    onTheme: () -> Unit,
    onSettings: () -> Unit
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
                text = "My Veil",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            ProfileSheetItem(
                icon = Icons.Default.Bookmark,
                title = "Bookmarks",
                subtitle = "$bookmarkCount saved sites",
                onClick = { onBookmarks(); onDismiss() }
            )
            ProfileSheetItem(
                icon = Icons.Default.History,
                title = "History",
                subtitle = "Browsing history",
                onClick = { onHistory(); onDismiss() }
            )

            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

            ProfileSheetItem(
                icon = Icons.Default.FileDownload,
                title = "Downloads",
                subtitle = "Downloaded files",
                onClick = { onDownloads(); onDismiss() }
            )

            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

            ProfileSheetItem(
                icon = Icons.Default.Palette,
                title = "Theme",
                subtitle = "Dark / Light / System",
                onClick = { onTheme(); onDismiss() }
            )
            ProfileSheetItem(
                icon = Icons.Default.Settings,
                title = "Settings",
                subtitle = "Privacy, search & more",
                onClick = { onSettings(); onDismiss() }
            )
        }
    }
}

@Composable
private fun ProfileSheetItem(
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
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        leadingContent = {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.secondaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.size(20.dp)
                )
            }
        },
        modifier = Modifier.clickable(onClick = onClick)
    )
}
