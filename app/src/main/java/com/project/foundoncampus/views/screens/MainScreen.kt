package com.project.foundoncampus.views.screens

import android.annotation.SuppressLint
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.project.foundoncampus.nav.NavGraph
import com.project.foundoncampus.nav.Route
import com.project.foundoncampus.util.SessionManager
import com.project.foundoncampus.views.components.BottomTabsBar

private val bottomNavRoutes = listOf(
    Route.Home.routeName,
    Route.Search.routeName,
    Route.Create.routeName,
    Route.History.routeName,
    "profile" // base profile path for match
)

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Route.Home.routeName

    val showBottomBar = bottomNavRoutes.any { currentRoute.startsWith(it) }

    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    var currentUserEmail by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    // Load user email from DataStore
    LaunchedEffect(Unit) {
        sessionManager.getUserEmail().collect { email ->
            if (!email.isNullOrEmpty() && email != currentUserEmail) {
                currentUserEmail = email
            }
        }
    }
    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomTabsBar(
                    selectedRoute = currentRoute,
                    onNavigate = { routeName ->
                        if (currentRoute != routeName) {
                            navController.navigate(routeName) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    currentUserEmail = currentUserEmail
                )
            }
        }
    ) {
        NavGraph(
            navController = navController,
            startDestination = Route.Auth.routeName
            //startDestination = Route.Main.routeName

        )
    }
}
