package com.avcoding.veil.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.avcoding.veil.ui.bookmarks.BookmarksScreen
import com.avcoding.veil.ui.browser.BrowserScreen
import com.avcoding.veil.ui.browser.BrowserViewModel
import com.avcoding.veil.ui.downloads.DownloadsScreen
import com.avcoding.veil.ui.history.HistoryScreen
import com.avcoding.veil.ui.homepage.HomepageScreen
import com.avcoding.veil.ui.tabs.TabsScreen

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Browser : Screen("browser?url={url}") {
        fun createRoute(url: String) = "browser?url=$url"
    }
    data object Bookmarks : Screen("bookmarks")
    data object Tabs : Screen("tabs")
    data object Downloads : Screen("downloads")
    data object History : Screen("history")
}

@Composable
fun BrowserNavGraph(navController: NavHostController) {
    // Activity-scoped ViewModel: observes PrivateModeManager singleton
    // and propagates isPrivate to screens that need it as a parameter.
    val rootViewModel: BrowserViewModel = hiltViewModel()
    val rootUiState by rootViewModel.uiState.collectAsStateWithLifecycle()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomepageScreen(
                isPrivate = rootUiState.isPrivate,
                onNavigateToBrowser = { url ->
                    navController.navigate(Screen.Browser.createRoute(url))
                },
                onNavigateToBookmarks = {
                    navController.navigate(Screen.Bookmarks.route)
                },
                onNavigateToTabs = {
                    navController.navigate(Screen.Tabs.route)
                },
                onNavigateToHistory = {
                    navController.navigate(Screen.History.route)
                },
                onNavigateToDownloads = {
                    navController.navigate(Screen.Downloads.route)
                }
            )
        }

        composable(
            route = Screen.Browser.route,
            arguments = listOf(
                navArgument("url") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) { backStackEntry ->
            val url = backStackEntry.arguments?.getString("url") ?: ""
            BrowserScreen(
                initialUrl = url,
                isPrivate = rootUiState.isPrivate,
                onNavigateToTabs = {
                    navController.navigate(Screen.Tabs.route)
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onNavigateToBookmarks = {
                    navController.navigate(Screen.Bookmarks.route)
                },
                onNavigateToHistory = {
                    navController.navigate(Screen.History.route)
                },
                onNavigateToDownloads = {
                    navController.navigate(Screen.Downloads.route)
                }
            )
        }

        composable(Screen.Bookmarks.route) {
            BookmarksScreen(
                onNavigateToBrowser = { bookmarkUrl ->
                    navController.navigate(Screen.Browser.createRoute(bookmarkUrl))
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.History.route) {
            HistoryScreen(
                onNavigateToBrowser = { url ->
                    navController.navigate(Screen.Browser.createRoute(url))
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onNavigateToBookmarks = {
                    navController.navigate(Screen.Bookmarks.route)
                },
                onNavigateToTabs = {
                    navController.navigate(Screen.Tabs.route)
                },
                onNavigateToDownloads = {
                    navController.navigate(Screen.Downloads.route)
                }
            )
        }

        composable(Screen.Downloads.route) {
            DownloadsScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Tabs.route) { tabsBackStackEntry ->
            val browserBackStackEntry = remember(navController) {
                try {
                    navController.getBackStackEntry(Screen.Browser.route)
                } catch (e: IllegalArgumentException) {
                    tabsBackStackEntry
                }
            }
            TabsScreen(
                browserBackStackEntry = browserBackStackEntry,
                onSelectTab = { tabUrl ->
                    navController.navigate(Screen.Browser.createRoute(tabUrl)) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                    }
                },
                onNewTab = {
                    navController.navigate(Screen.Browser.createRoute("")) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onNavigateToBookmarks = {
                    navController.navigate(Screen.Bookmarks.route)
                },
                onNavigateToHistory = {
                    navController.navigate(Screen.History.route)
                },
                onNavigateToDownloads = {
                    navController.navigate(Screen.Downloads.route)
                }
            )
        }
    }
}
