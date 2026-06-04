package com.avcoding.veil.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.avcoding.veil.ui.bookmarks.BookmarksScreen
import com.avcoding.veil.ui.browser.BrowserScreen
import com.avcoding.veil.ui.homepage.HomepageScreen
import com.avcoding.veil.ui.tabs.TabsScreen

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Browser : Screen("browser?url={url}") {
        fun createRoute(url: String) = "browser?url=$url"
    }
    data object Bookmarks : Screen("bookmarks")
    data object Tabs : Screen("tabs")
}

@Composable
fun BrowserNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomepageScreen(
                onNavigateToBrowser = { url ->
                    navController.navigate(Screen.Browser.createRoute(url))
                },
                onNavigateToBookmarks = {
                    navController.navigate(Screen.Bookmarks.route)
                },
                onNavigateToTabs = {
                    navController.navigate(Screen.Tabs.route)
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
                onNavigateToTabs = {
                    navController.navigate(Screen.Tabs.route)
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Bookmarks.route) {
            BookmarksScreen(
                onNavigateToBrowser = { url ->
                    navController.navigate(Screen.Browser.createRoute(url))
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }
        composable(Screen.Tabs.route) {
            TabsScreen(
                onSelectTab = { url ->
                    navController.navigate(Screen.Browser.createRoute(url)) {
                        popUpTo(Screen.Tabs.route) { inclusive = true }
                    }
                },
                onClose = {
                    navController.popBackStack()
                }
            )
        }
    }
}
