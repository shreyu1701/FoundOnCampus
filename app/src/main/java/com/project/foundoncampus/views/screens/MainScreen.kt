package com.project.foundoncampus.views.screens


import android.annotation.SuppressLint
import androidx.compose.runtime.*
import androidx.compose.material3.Scaffold
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.project.foundoncampus.nav.NavGraph
import com.project.foundoncampus.nav.Route
import com.project.foundoncampus.views.components.BottomTabsBar

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen() {
    val navController = rememberNavController()

    // To observe and highlight the selected tab
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Route.Home.routeName

    Scaffold(
        bottomBar = {
            BottomTabsBar(
                selectedRoute = currentRoute,
                onTabSelected = { route ->
                    if (currentRoute != route.routeName) {
                        navController.navigate(route.routeName) {
                            // Avoid building up a large backstack of the same destination
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
    ) {
        NavGraph(
            navController = navController,
            startDestination = Route.Home.routeName
        )
    }
}
