package com.project.foundoncampus.nav


import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.project.foundoncampus.views.screens.SignUpScreen
import com.project.foundoncampus.views.screens.HomeScreen
import com.project.foundoncampus.views.screens.SearchScreen
import com.project.foundoncampus.views.screens.CreateScreen
import com.project.foundoncampus.views.screens.HistoryScreen
import com.project.foundoncampus.views.screens.ProfileScreen
import com.project.foundoncampus.views.screens.RecentClaimedScreen
import com.project.foundoncampus.views.screens.RecentFoundScreen
import com.project.foundoncampus.views.screens.RecentLostScreen
import com.project.foundoncampus.views.screens.SignInScreen

@Composable
fun NavGraph(navController: NavHostController, startDestination: String = Route.Home.routeName) {
    NavHost(

        navController = navController,
//        startDestination = Route.SignUp.routeName
        startDestination = startDestination
    ) {
<<<<<<< Updated upstream
        composable(Route.SignUp.routeName) { SignUpScreen(navController) }
        composable(Route.SignIn.routeName) { SignInScreen(navController) }
        composable(Route.Home.routeName) { HomeScreen(navController) }
        composable(Route.Search.routeName) { SearchScreen(navController) }
        composable(Route.Create.routeName) { CreateScreen(navController) }
        composable(Route.History.routeName) { HistoryScreen(navController) }
        composable(Route.Profile.routeName) { ProfileScreen(navController) }
=======
        // Auth Navigation
        navigation(
            route = Route.Auth.routeName,
            startDestination = Route.SignIn.routeName
        ) {
            composable(Route.SignIn.routeName) { SignInScreen(navController) }
            composable(Route.SignUp.routeName) { SignUpScreen(navController) }
        }

        // Main Navigation
        navigation(
            route = Route.Main.routeName,
            startDestination = Route.Home.routeName
        ) {
            composable(Route.Home.routeName) { HomeScreen(navController) }
            composable(Route.Search.routeName) { SearchScreen(navController) }
            composable(Route.Create.routeName) { CreateScreen(navController) }
            composable(Route.History.routeName) { HistoryScreen(navController) }
            composable(Route.Profile.routeName) { ProfileScreen(navController) }
            composable(Route.MyListing.routeName) { MyListingScreen(navController) }
            composable(Route.RecentLost.routeName) { RecentLostScreen(navController) }
            composable(Route.RecentFound.routeName) { RecentFoundScreen(navController) }
            composable(Route.RecentClaimed.routeName) { RecentClaimedScreen(navController) }
        }
>>>>>>> Stashed changes
    }
}
