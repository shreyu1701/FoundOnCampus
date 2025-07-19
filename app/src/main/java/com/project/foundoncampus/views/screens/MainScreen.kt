package com.project.foundoncampus.views.screens


import android.annotation.SuppressLint
import androidx.compose.runtime.*
import androidx.compose.material3.Scaffold
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.project.foundoncampus.nav.NavGraph
import com.project.foundoncampus.nav.Route
import com.project.foundoncampus.views.components.BottomTabsBar


private val bottomNavRoutes = listOf(
    Route.Home.routeName,
    Route.Search.routeName,
    Route.Create.routeName,
    Route.History.routeName,
    Route.Profile.routeName
)

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in bottomNavRoutes

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomTabsBar(
                    selectedRoute = currentRoute ?: Route.Home.routeName,
                    onTabSelected = { route ->
                        if (currentRoute != route.routeName) {
                            navController.navigate(route.routeName) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                )
            }
        }
    ) {
        NavGraph(
            navController = navController,
            startDestination = Route.Auth.routeName
        )
    }
}

