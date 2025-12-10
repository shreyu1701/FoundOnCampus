package com.project.foundoncampus.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.project.foundoncampus.views.profile.ProfileDetailsScreen
import com.project.foundoncampus.views.screens.*

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

            // ✅ FIXED: Now matches 'group_chat'
            composable("group_chat") {
                GroupChatScreen(navController = navController)
            }

            // ✅ Profile Screen with userEmail argument
            composable(
                route = Route.Profile.routeName,
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
                ProfileDetailsScreen(navController = navController)
            }

            composable(
                route = Route.AccountDetails.routeName + "?email={email}",
                arguments = listOf(navArgument("email") { type = NavType.StringType; nullable = false })
            ) { backStackEntry ->
                val email = backStackEntry.arguments?.getString("email")!!
                AccountDetailsScreen(navController = navController, userEmail = email)
            }

            // ✅ Chat & Contact Screens
            composable(Route.Contact.routeName) {
                ContactScreen(navController = navController)
            }
            composable(
                route = Route.Chat.routeName,
                arguments = listOf(navArgument("user") { type = NavType.StringType })
            ) { backStackEntry ->
                ChatScreen(backStackEntry)
            }
        }
    }
}
