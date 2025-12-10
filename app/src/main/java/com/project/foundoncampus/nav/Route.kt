package com.project.foundoncampus.nav

sealed class Route(val routeName: String) {
    // Parent Graphs
    object Auth : Route("auth")
    object Main : Route("main")

    // Auth Screens
    object SignUp : Route("sign_up")
    object SignIn : Route("sign_in")

    // Main Screens
    object Home : Route("home")
    object Search : Route("search")
    object Create : Route("create")
    object History : Route("history")
    object MyListing : Route("my_listing")
    object RecentLost : Route("recentlost")
    object RecentClaimed : Route("recentclaimed")
    object RecentFound : Route("recentfound")
    object Chat {
        const val routeName = "chat/{user}"
        fun createRoute(userEmail: String) = "chat/$userEmail"
    }
    object Contact {
        const val routeName = "contact"
    }
    object Profile : Route("profile/{userEmail}") {
        fun createRoute(userEmail: String): String = "profile/$userEmail"
    }
    object ProfileDetails : Route("profileDetails")
    object AccountDetails : Route("account_details")
}


