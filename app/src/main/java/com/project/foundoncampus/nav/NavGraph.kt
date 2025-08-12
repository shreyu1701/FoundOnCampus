package com.project.foundoncampus.nav


import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.project.foundoncampus.views.screens.AccountDetailsScreen
import com.project.foundoncampus.views.screens.SignUpScreen
import com.project.foundoncampus.views.screens.SignInScreen
import com.project.foundoncampus.views.screens.HomeScreen
import com.project.foundoncampus.views.screens.SearchScreen
import com.project.foundoncampus.views.screens.CreateScreen
import com.project.foundoncampus.views.screens.HistoryScreen
import com.project.foundoncampus.views.screens.MyListingScreen
import com.project.foundoncampus.views.screens.ProfileDetailsScreen
import com.project.foundoncampus.views.screens.ProfileScreen
import com.project.foundoncampus.views.screens.RecentClaimedScreen
import com.project.foundoncampus.views.screens.RecentFoundScreen
import com.project.foundoncampus.views.screens.RecentLostScreen

@Composable
fun NavGraph(navController: NavHostController, startDestination: String = Route.Auth.routeName) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
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
            composable(Route.MyListing.routeName) { MyListingScreen(navController) }

            composable(Route.RecentLost.routeName) { RecentLostScreen(navController) }
            composable(Route.RecentFound.routeName) { RecentFoundScreen(navController) }
            composable(Route.RecentClaimed.routeName) { RecentClaimedScreen(navController) }

            // ✅ Dynamic route with argument passed via Route.Profile.createRoute(userEmail)
            composable(
                route = Route.Profile.routeName, // e.g., "profile/{userEmail}"
                arguments = listOf(navArgument("userEmail") { type = NavType.StringType })
            ) { backStackEntry ->
                val email = backStackEntry.arguments?.getString("userEmail") ?: ""
                ProfileScreen(navController = navController, userEmail = email)
            }
            composable(
                route = Route.ProfileDetails.routeName + "?email={email}",
                arguments = listOf(navArgument("email") { type = NavType.StringType; nullable = false })
            ) { backStackEntry ->
                val email = backStackEntry.arguments?.getString("email")!!
                ProfileDetailsScreen(navController = navController, userEmail = email)
            }
            composable(
                route = Route.AccountDetails.routeName + "?email={email}",
                arguments = listOf(navArgument("email") { type = NavType.StringType; nullable = false })
            ) { backStackEntry ->
                val email = backStackEntry.arguments?.getString("email")!!
                AccountDetailsScreen(navController = navController, userEmail = email)
            }
        }
    }
}