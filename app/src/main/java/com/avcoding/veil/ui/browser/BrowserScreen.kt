package com.avcoding.veil.ui.browser

import android.graphics.Bitmap
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowserScreen(
    initialUrl: String,
    viewModel: BrowserViewModel = hiltViewModel(),
    onNavigateToTabs: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var webView: WebView? by remember { mutableStateOf(null) }

    LaunchedEffect(initialUrl) {
        if (initialUrl.isNotEmpty()) {
            viewModel.onUrlChanged(initialUrl)
            webView?.loadUrl(initialUrl)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    TextField(
                        value = uiState.currentUrl,
                        onValueChange = { viewModel.onUrlChanged(it) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        trailingIcon = {
                            IconButton(onClick = { webView?.loadUrl(uiState.currentUrl) }) {
                                Icon(Icons.Default.Refresh, contentDescription = "Reload")
                            }
                        }
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateToHome) {
                        Icon(Icons.Default.Home, contentDescription = "Home")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.addBookmark() }) {
                        Icon(Icons.Default.Favorite, contentDescription = "Add Bookmark")
                    }
                    IconButton(onClick = onNavigateToTabs) {
                        Icon(Icons.Default.Layers, contentDescription = "Tabs")
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar {
                IconButton(
                    onClick = { webView?.goBack() },
                    enabled = uiState.canGoBack
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                IconButton(
                    onClick = { webView?.goForward() },
                    enabled = uiState.canGoForward
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Forward")
                }
                Spacer(Modifier.weight(1f))
                IconButton(onClick = { viewModel.toggleAdBlock() }) {
                    Icon(
                        if (uiState.isAdBlockEnabled) Icons.Default.Shield else Icons.Default.ShieldMoon,
                        contentDescription = "Toggle AdBlock",
                        tint = if (uiState.isAdBlockEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            if (uiState.isLoading) {
                LinearProgressIndicator(
                    progress = { uiState.progress / 100f },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            AndroidView(
                factory = { context ->
                    WebView(context).apply {
                        @Suppress("SetJavaScriptEnabled")
                        settings.javaScriptEnabled = true
                        webViewClient = object : WebViewClient() {
                            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                viewModel.onLoadingStateChanged(isLoading = true)
                                url?.let { viewModel.onUrlChanged(it) }
                            }

                            override fun onPageFinished(view: WebView?, url: String?) {
                                viewModel.onLoadingStateChanged(isLoading = false)
                                viewModel.onNavigationStateChanged(
                                    canGoBack = view?.canGoBack() ?: false,
                                    canGoForward = view?.canGoForward() ?: false
                                )
                            }

                            override fun shouldInterceptRequest(
                                view: WebView?,
                                request: WebResourceRequest?
                            ): android.webkit.WebResourceResponse? {
                                if (uiState.isAdBlockEnabled && request != null) {
                                    if (viewModel.adBlocker.shouldBlock(request.url.toString())) {
                                        return viewModel.adBlocker.createEmptyResponse()
                                    }
                                }
                                return super.shouldInterceptRequest(view, request)
                            }
                        }
                        webChromeClient = object : WebChromeClient() {
                            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                                viewModel.onProgressChanged(newProgress)
                            }

                            override fun onReceivedTitle(view: WebView?, title: String?) {
                                title?.let { viewModel.onTitleChanged(it) }
                            }
                        }
                        webView = this
                        if (initialUrl.isNotEmpty()) loadUrl(initialUrl)
                        else loadUrl("https://www.google.com")
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
