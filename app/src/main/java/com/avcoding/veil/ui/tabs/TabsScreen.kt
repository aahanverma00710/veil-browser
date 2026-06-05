package com.avcoding.veil.ui.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import com.avcoding.veil.domain.model.Tab
import com.avcoding.veil.ui.browser.BrowserViewModel
import com.avcoding.veil.ui.components.PillScreenType
import com.avcoding.veil.ui.components.SettingsBottomSheet
import com.avcoding.veil.ui.components.VeilBottomPill
import com.avcoding.veil.ui.theme.*

private val tabColors = listOf(
    Color(0xFF7C3AED), Color(0xFF2563EB), Color(0xFF059669),
    Color(0xFFD97706), Color(0xFFDC2626), Color(0xFF0891B2)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TabsScreen(
    browserBackStackEntry: NavBackStackEntry,
    onSelectTab: (String) -> Unit,
    onNewTab: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToBookmarks: () -> Unit,
    onNavigateToHistory: () -> Unit = {},
    onNavigateToDownloads: () -> Unit = {}
) {
    val viewModel: BrowserViewModel = hiltViewModel(browserBackStackEntry)
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var sheetVisible by remember { mutableStateOf(false) }
    var isPrivate by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(VeilTabsBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp)
        ) {
            // Top row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TabToggleButton(
                    label = "Tabs",
                    isActive = !isPrivate,
                    onClick = { isPrivate = false }
                )
                Spacer(Modifier.width(16.dp))
                TabToggleButton(
                    label = "Private",
                    isActive = isPrivate,
                    onClick = { isPrivate = true }
                )
                Spacer(Modifier.weight(1f))
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

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(uiState.tabs) { tab ->
                    TabGridCard(
                        tab = tab,
                        isActive = tab.id == uiState.activeTabId,
                        colorIndex = uiState.tabs.indexOf(tab),
                        onClick = { onSelectTab(tab.url) },
                        onClose = { viewModel.closeTab(tab.id) }
                    )
                }
                item {
                    NewTabCard(
                        onClick = {
                            viewModel.newTab()
                            onNewTab()
                        }
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
            activeDestination = "tabs",
            onHome = onNavigateToHome,
            onBookmarks = onNavigateToBookmarks,
            onNewTab = {
                viewModel.newTab()
                onNewTab()
            },
            onHistory = onNavigateToHistory,
            onTabs = {}
        )
    }

    if (sheetVisible) {
        SettingsBottomSheet(
            isAdBlockEnabled = uiState.isAdBlockEnabled,
            onAdBlockToggle = { viewModel.toggleAdBlock() },
            onDismiss = { sheetVisible = false },
            onBookmarks = { sheetVisible = false; onNavigateToBookmarks() },
            onHistory = { sheetVisible = false; onNavigateToHistory() },
            onDownloads = { sheetVisible = false; onNavigateToDownloads() }
        )
    }
}

@Composable
private fun TabToggleButton(label: String, isActive: Boolean, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Text(
            text = label,
            color = if (isActive) Color.White else VeilIconInactive,
            fontSize = 15.sp,
            fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal
        )
        Spacer(Modifier.height(3.dp))
        if (isActive) {
            Box(
                modifier = Modifier
                    .width(28.dp)
                    .height(2.dp)
                    .background(VeilAccent, RoundedCornerShape(1.dp))
            )
        }
    }
}

@Composable
private fun TabGridCard(
    tab: Tab,
    isActive: Boolean,
    colorIndex: Int,
    onClick: () -> Unit,
    onClose: () -> Unit
) {
    val color = tabColors[colorIndex % tabColors.size]
    val borderModifier = if (isActive) {
        Modifier.border(1.5.dp, VeilAccent, RoundedCornerShape(12.dp))
    } else {
        Modifier.border(0.5.dp, VeilPillBorder, RoundedCornerShape(12.dp))
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(VeilSurfaceItem)
            .then(borderModifier)
            .clickable(onClick = onClick)
    ) {
        // Preview area with skeleton lines
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            SkeletonLine(fraction = 0.9f)
            SkeletonLine(fraction = 0.7f)
            SkeletonLine(fraction = 0.85f)
            SkeletonLine(fraction = 0.5f)
        }

        // Footer
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White.copy(alpha = 0.05f))
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(14.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = tab.title.ifBlank { tab.url },
                color = Color.White,
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            IconButton(
                onClick = onClose,
                modifier = Modifier.size(20.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = VeilIconInactive,
                    modifier = Modifier.size(12.dp)
                )
            }
        }
    }
}

@Composable
private fun SkeletonLine(fraction: Float) {
    Box(
        modifier = Modifier
            .fillMaxWidth(fraction)
            .height(5.dp)
            .clip(RoundedCornerShape(3.dp))
            .background(Color.White.copy(alpha = 0.1f))
    )
}

@Composable
private fun NewTabCard(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .clip(RoundedCornerShape(12.dp))
            .dashedBorder(color = VeilIconInactive, cornerRadius = 12.dp)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .border(1.dp, VeilIconInactive, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = VeilIconInactive,
                    modifier = Modifier.size(16.dp)
                )
            }
            Text(
                text = "New tab",
                color = VeilIconInactive,
                fontSize = 12.sp
            )
        }
    }
}

private fun Modifier.dashedBorder(color: Color, cornerRadius: Dp): Modifier =
    drawBehind {
        drawRoundRect(
            color = color,
            cornerRadius = CornerRadius(cornerRadius.toPx()),
            style = Stroke(
                width = 1.5.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f))
            )
        )
    }
