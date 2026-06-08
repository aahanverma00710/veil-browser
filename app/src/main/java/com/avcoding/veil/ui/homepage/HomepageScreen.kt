package com.avcoding.veil.ui.homepage

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForwardIos
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
import com.avcoding.veil.domain.model.Bookmark
import com.avcoding.veil.domain.model.Tab
import com.avcoding.veil.ui.bookmarks.BookmarksViewModel
import com.avcoding.veil.ui.browser.BrowserViewModel
import com.avcoding.veil.ui.components.PillScreenType
import com.avcoding.veil.ui.components.SettingsBottomSheet
import com.avcoding.veil.ui.components.VeilBottomPill
import com.avcoding.veil.ui.components.VeilTopBar
import com.avcoding.veil.ui.theme.*

private val favColors = listOf(
    Color(0xFF7C3AED), Color(0xFF2563EB), Color(0xFF059669),
    Color(0xFFD97706), Color(0xFFDC2626), Color(0xFF0891B2)
)

@Composable
fun HomepageScreen(
    isPrivate: Boolean = false,
    bookmarksViewModel: BookmarksViewModel = hiltViewModel(),
    browserViewModel: BrowserViewModel = hiltViewModel(),
    onNavigateToBrowser: (String) -> Unit,
    onNavigateToBookmarks: () -> Unit,
    onNavigateToTabs: () -> Unit,
    onNavigateToHistory: () -> Unit = {},
    onNavigateToDownloads: () -> Unit = {}
) {
    val bookmarks by bookmarksViewModel.bookmarks.collectAsStateWithLifecycle()
    val browserUiState by browserViewModel.uiState.collectAsStateWithLifecycle()
    var sheetVisible by remember { mutableStateOf(false) }

    val recentTabs = remember(browserUiState.tabs) {
        browserUiState.tabs
            .sortedByDescending { it.lastAccessed }
            .take(5)
    }

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
                isHomepage = true,
                onUrlSubmit = onNavigateToBrowser,
                onSettingsClick = { sheetVisible = true },
                isPrivate = isPrivate
            )

            if (isPrivate) {
                Text(
                    text = "Private · No history saved",
                    color = VeilAccent,
                    fontSize = 10.sp,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 4.dp)
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)
            ) {
                item {
                    SectionLabel("FAVOURITES")
                    Spacer(Modifier.height(8.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(vertical = 4.dp)
                    ) {
                        items(bookmarks.take(8)) { bookmark ->
                            FavouriteItem(
                                bookmark = bookmark,
                                colorIndex = bookmarks.indexOf(bookmark),
                                onClick = { onNavigateToBrowser(bookmark.url) }
                            )
                        }
                        item {
                            AddFavouriteItem()
                        }
                    }
                    Spacer(Modifier.height(20.dp))
                }

                item {
                    SectionLabel("RECENT TABS")
                    Spacer(Modifier.height(8.dp))
                }

                if (recentTabs.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No recent tabs",
                                color = VeilIconInactive,
                                fontSize = 14.sp
                            )
                        }
                    }
                } else {
                    items(recentTabs) { tab ->
                        RecentTabRow(
                            tab = tab,
                            colorIndex = recentTabs.indexOf(tab),
                            onClick = { onNavigateToBrowser(tab.url) }
                        )
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
            activeDestination = "home",
            borderWidth = if (isPrivate) 1.dp else 0.5.dp,
            borderColor = if (isPrivate) VeilAccent.copy(alpha = 0.4f) else VeilPillBorder,
            onHome = {},
            onBookmarks = onNavigateToBookmarks,
            onNewTab = { onNavigateToBrowser("") },
            onHistory = onNavigateToHistory,
            onTabs = onNavigateToTabs
        )
    }

    if (sheetVisible) {
        SettingsBottomSheet(
            bookmarkCount = bookmarks.size,
            isAdBlockEnabled = browserUiState.isAdBlockEnabled,
            onAdBlockToggle = { browserViewModel.toggleAdBlock() },
            onDismiss = { sheetVisible = false },
            onBookmarks = { sheetVisible = false; onNavigateToBookmarks() },
            onHistory = { sheetVisible = false; onNavigateToHistory() },
            onDownloads = { sheetVisible = false; onNavigateToDownloads() }
        )
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        color = VeilIconInactive,
        fontSize = 9.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 1.2.sp
    )
}

@Composable
private fun FavouriteItem(
    bookmark: Bookmark,
    colorIndex: Int,
    onClick: () -> Unit
) {
    val color = favColors[colorIndex % favColors.size]
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(52.dp)
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(color),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = bookmark.title.take(1).uppercase(),
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = bookmark.title,
            color = VeilIconInactive,
            fontSize = 8.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun AddFavouriteItem() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(52.dp)
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(10.dp))
                .border(1.dp, VeilIconInactive, RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add",
                tint = VeilIconInactive,
                modifier = Modifier.size(16.dp)
            )
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Add",
            color = VeilIconInactive,
            fontSize = 8.sp
        )
    }
}

@Composable
private fun RecentTabRow(
    tab: Tab,
    colorIndex: Int,
    onClick: () -> Unit
) {
    val color = favColors[colorIndex % favColors.size]
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(color),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = tab.title.take(1).uppercase(),
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = tab.title,
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = tab.url,
                color = VeilIconInactive,
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Icon(
            imageVector = Icons.Default.ArrowForwardIos,
            contentDescription = null,
            tint = VeilIconInactive,
            modifier = Modifier.size(12.dp)
        )
    }
}
