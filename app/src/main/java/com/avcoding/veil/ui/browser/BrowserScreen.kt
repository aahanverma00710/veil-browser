package com.avcoding.veil.ui.browser

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.webkit.MimeTypeMap
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.avcoding.veil.ui.components.PillScreenType
import com.avcoding.veil.ui.components.SettingsBottomSheet
import com.avcoding.veil.ui.components.VeilBottomPill
import com.avcoding.veil.ui.components.VeilTopBar
import com.avcoding.veil.ui.theme.VeilAccent
import com.avcoding.veil.ui.theme.VeilBackground
import java.net.URLDecoder

@Composable
fun BrowserScreen(
    initialUrl: String,
    isPrivate: Boolean = false,
    viewModel: BrowserViewModel = hiltViewModel(),
    onNavigateToTabs: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToBookmarks: () -> Unit = {},
    onNavigateToDownloads: () -> Unit = {},
    onNavigateToHistory: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var webView: WebView? by remember { mutableStateOf(null) }
    var sheetVisible by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    // Stores a pending download when waiting for WRITE_EXTERNAL_STORAGE permission
    var pendingDownload by remember { mutableStateOf<Triple<String, String, String>?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            pendingDownload?.let { (url, name, mime) ->
                viewModel.startDownload(url, name, mime)
            }
        }
        pendingDownload = null
    }

    // Show snackbar when a download event is emitted
    LaunchedEffect(Unit) {
        viewModel.downloadEvent.collect { message ->
            snackbarHostState.showSnackbar(
                message = message,
                actionLabel = "Dismiss",
                duration = SnackbarDuration.Short
            )
        }
    }

    // Trigger permission check or start download when pendingDownload is set
    LaunchedEffect(pendingDownload) {
        pendingDownload?.let { (url, name, mime) ->
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                val granted = ContextCompat.checkSelfPermission(
                    context, Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
                if (!granted) {
                    permissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    return@let
                }
            }
            viewModel.startDownload(url, name, mime)
            pendingDownload = null
        }
    }

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
                isPrivate = isPrivate,
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
                factory = { ctx ->
                    WebView(ctx).apply {
                        @Suppress("SetJavaScriptEnabled")
                        settings.javaScriptEnabled = true

                        if (isPrivate) {
                            settings.cacheMode = WebSettings.LOAD_NO_CACHE
                            @Suppress("DEPRECATION")
                            settings.saveFormData = false
                        }

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
                                url?.let { u ->
                                    val title = view?.title ?: u
                                    viewModel.addToHistory(u, title)
                                }
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

                        setDownloadListener { url, _, contentDisposition, mimeType, _ ->
                            val fileName = extractFileName(contentDisposition, url, mimeType)
                            pendingDownload = Triple(url, fileName, mimeType)
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

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 90.dp)
        ) { data ->
            Snackbar(
                snackbarData = data,
                actionOnNewLine = false
            )
        }
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

private fun extractFileName(
    contentDisposition: String?,
    url: String,
    mimeType: String
): String {
    if (!contentDisposition.isNullOrBlank()) {
        // RFC 5987: filename*=charset''encoded-name (highest priority)
        val rfc5987 = Regex(
            "filename\\*=[^']*''([\\S]+)",
            RegexOption.IGNORE_CASE
        ).find(contentDisposition)
        if (rfc5987 != null) {
            return try {
                URLDecoder.decode(rfc5987.groupValues[1].trimEnd(';'), "UTF-8")
            } catch (e: Exception) {
                rfc5987.groupValues[1].trimEnd(';')
            }
        }
        // Basic filename=
        val basic = Regex(
            "filename=[\"']?([^\"';\\s]+)[\"']?",
            RegexOption.IGNORE_CASE
        ).find(contentDisposition)
        if (basic != null) return basic.groupValues[1].trim('"', '\'', ' ')
    }

    // Fall back to the last URL path segment
    val lastSegment = Uri.parse(url).lastPathSegment
    if (!lastSegment.isNullOrBlank() && lastSegment.contains('.')) return lastSegment

    // Last resort: timestamp + extension derived from MIME type
    val ext = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType) ?: "bin"
    return "download_${System.currentTimeMillis()}.$ext"
}
