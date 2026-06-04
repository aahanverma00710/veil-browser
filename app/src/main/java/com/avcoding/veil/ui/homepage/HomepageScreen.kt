package com.avcoding.veil.ui.homepage

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.avcoding.veil.ui.bookmarks.BookmarksViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomepageScreen(
    bookmarksViewModel: BookmarksViewModel = hiltViewModel(),
    onNavigateToBrowser: (String) -> Unit,
    onNavigateToBookmarks: () -> Unit,
    onNavigateToTabs: () -> Unit
) {
    val bookmarks by bookmarksViewModel.bookmarks.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Browser App") },
                actions = {
                    IconButton(onClick = onNavigateToBookmarks) {
                        Icon(Icons.Default.Bookmarks, contentDescription = "Bookmarks")
                    }
                    IconButton(onClick = onNavigateToTabs) {
                        Icon(Icons.Default.Layers, contentDescription = "Tabs")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(32.dp))
            
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                placeholder = { Text("Search or type URL") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        Button(onClick = { 
                            val url = if (searchQuery.startsWith("http")) searchQuery else "https://www.google.com/search?q=$searchQuery"
                            onNavigateToBrowser(url) 
                        }) {
                            Text("Go")
                        }
                    }
                }
            )

            Spacer(Modifier.height(32.dp))

            Text(
                text = "Quick Access",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                contentPadding = PaddingValues(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(bookmarks.take(8)) { bookmark ->
                    SpeedDialItem(
                        title = bookmark.title,
                        onClick = { onNavigateToBrowser(bookmark.url) }
                    )
                }
            }
        }
    }
}

@Composable
fun SpeedDialItem(title: String, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier.size(56.dp),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = title.take(1).uppercase(),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            maxLines = 1,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}
