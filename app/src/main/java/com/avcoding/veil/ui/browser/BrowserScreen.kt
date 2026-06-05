package com.avcoding.veil.ui.browser

import android.graphics.Bitmap
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.avcoding.veil.ui.components.PillScreenType
import com.avcoding.veil.ui.components.SettingsBottomSheet
import com.avcoding.veil.ui.components.VeilBottomPill
import com.avcoding.veil.ui.components.VeilTopBar
import com.avcoding.veil.ui.theme.VeilAccent
import com.avcoding.veil.ui.theme.VeilBackground

@Composable
fun BrowserScreen(
    initialUrl: String,
    viewModel: BrowserViewModel = hiltViewModel(),
    onNavigateToTabs: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToBookmarks: () -> Unit = {},
    onNavigateToDownloads: () -> Unit = {},
    onNavigateToHistory: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var webView: WebView? by remember { mutableStateOf(null) }
    var sheetVisible by remember { mutableStateOf(false) }

    val isSecure = uiState.currentUrl.startsWith("https://")

    BackHandler {
        if (webView?.canGoBack() == true) {
            webView?.goBack()
        } else {
            onNavigateToHome()
        }
    }

    LaunchedEffect(initialUrl) {
        if (initialUrl.isNotEmpty()) {
            viewModel.onUrlChanged(initialUrl)
            webView?.loadUrl(initialUrl)
        }
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
                isHomepage = false,
                currentUrl = uiState.currentUrl,
                onUrlSubmit = { url ->
                    viewModel.onUrlChanged(url)
                    webView?.loadUrl(url)
                },
                onSettingsClick = { sheetVisible = true },
                isSecure = isSecure,
                onRefresh = { webView?.reload() }
            )

            if (uiState.isLoading) {
                LinearProgressIndicator(
                    progress = { uiState.progress / 100f },
                    modifier = Modifier.fillMaxWidth(),
                    color = VeilAccent
                )
            }

            AndroidView(
                factory = { context ->
                    WebView(context).apply {
                        @Suppress("SetJavaScriptEnabled")
                        settings.javaScriptEnabled = true
                        webViewClient = object : WebViewClient() {
                            override fun onPageStarted(
                                view: WebView?, url: String?, favicon: Bitmap?
                            ) {
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

        VeilBottomPill(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 8.dp, vertical = 8.dp),
            screenType = PillScreenType.BROWSER,
            canGoBack = uiState.canGoBack,
            canGoForward = uiState.canGoForward,
            tabCount = uiState.tabs.size,
            onBack = { webView?.goBack() },
            onForward = { webView?.goForward() },
            onTabs = onNavigateToTabs,
            onHome = onNavigateToHome
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
