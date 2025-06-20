package com.project.foundoncampus.nav


import androidx.compose.runtime.Composable
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable


import com.project.foundoncampus.ui.screens.HomeScreen
import com.project.foundoncampus.ui.screens.SearchScreen
import com.project.foundoncampus.ui.screens.CreateScreen
import com.project.foundoncampus.ui.screens.HistoryScreen
import com.project.foundoncampus.ui.screens.ProfileScreen

@Composable
fun NavGraph(navController: NavHostController, startDestination: String = Route.Home.routeName) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Route.Home.routeName) { HomeScreen(navController) }
        composable(Route.Search.routeName) { SearchScreen(navController) }
        composable(Route.Create.routeName) { CreateScreen(navController) }
        composable(Route.History.routeName) { HistoryScreen(navController) }
        composable(Route.Profile.routeName) { ProfileScreen(navController) }
    }
}
