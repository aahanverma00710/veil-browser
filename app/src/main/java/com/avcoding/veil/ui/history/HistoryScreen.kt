package com.avcoding.veil.ui.history

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.avcoding.veil.data.local.entity.HistoryEntity
import com.avcoding.veil.ui.components.PillScreenType
import com.avcoding.veil.ui.components.SettingsBottomSheet
import com.avcoding.veil.ui.components.VeilBottomPill
import com.avcoding.veil.ui.theme.VeilAccent
import com.avcoding.veil.ui.theme.VeilBackground
import com.avcoding.veil.ui.theme.VeilIconInactive
import com.avcoding.veil.ui.theme.VeilSurfaceItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs

private val historyColors = listOf(
    Color(0xFF7C3AED), Color(0xFF2563EB), Color(0xFF059669),
    Color(0xFFD97706), Color(0xFFDC2626), Color(0xFF0891B2)
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel = hiltViewModel(),
    onNavigateToBrowser: (String) -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToBookmarks: () -> Unit,
    onNavigateToTabs: () -> Unit,
    onNavigateToDownloads: () -> Unit = {}
) {
    val groupedHistory by viewModel.groupedHistory.collectAsStateWithLifecycle()
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "History",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                if (groupedHistory.isNotEmpty()) {
                    IconButton(onClick = { viewModel.clearAll() }) {
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            contentDescription = "Clear all history",
                            tint = VeilIconInactive
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(VeilSurfaceItem)
                        .clickable { sheetVisible = true },
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

            if (groupedHistory.isEmpty()) {
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
                            text = "No browsing history yet",
                            color = Color.White.copy(alpha = 0.6f),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    groupedHistory.forEach { (date, historyItems) ->
                        stickyHeader(key = "header_$date") {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(VeilBackground)
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = date,
                                    color = VeilIconInactive,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    letterSpacing = 1.2.sp
                                )
                            }
                        }
                        items(historyItems, key = { it.id }) { item ->
                            SwipeableHistoryItem(
                                item = item,
                                onDelete = { viewModel.deleteItem(item.id) },
                                onClick = { onNavigateToBrowser(item.url) }
                            )
                        }
                    }
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
            onBookmarks = { sheetVisible = false; onNavigateToBookmarks() },
            onHistory = { sheetVisible = false },
            onDownloads = { sheetVisible = false; onNavigateToDownloads() }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeableHistoryItem(
    item: HistoryEntity,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                onDelete()
                true
            } else {
                false
            }
        }
    )

    val bgColor by animateColorAsState(
        targetValue = if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart)
            Color(0xFFDC2626).copy(alpha = 0.85f)
        else
            Color.Transparent,
        label = "swipe_bg"
    )

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        enableDismissFromEndToStart = true,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(bgColor)
                    .padding(end = 20.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color.White
                )
            }
        }
    ) {
        HistoryItemRow(item = item, onClick = onClick)
    }
}

@Composable
private fun HistoryItemRow(item: HistoryEntity, onClick: () -> Unit) {
    val letter = item.favicon ?: item.url.take(1).uppercase(Locale.getDefault())
    val colorIndex = abs(item.url.hashCode()) % historyColors.size
    val color = historyColors[colorIndex]

    val timeText = remember(item.visitedAt) {
        SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(item.visitedAt))
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(VeilBackground)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(color),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = letter,
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.title,
                color = Color.White.copy(alpha = 0.75f),
                fontSize = 13.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = item.url,
                color = VeilIconInactive,
                fontSize = 10.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Spacer(Modifier.width(8.dp))
        Text(
            text = timeText,
            color = VeilIconInactive,
            fontSize = 10.sp
        )
    }
}
