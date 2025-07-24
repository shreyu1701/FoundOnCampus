package com.project.foundoncampus.nav

sealed class Route(val routeName: String) {

    object SignUp : Route("sign_up")
    object SignIn : Route("sign_in")
    object Home : Route("home")
    object Search : Route("search")
    object Create : Route("create")
    object History : Route("history")
    object Profile : Route("profile")
<<<<<<< Updated upstream
=======
    object MyListing : Route("my_listing")
    object RecentLost : Route("recentlost")
    object RecentClaimed : Route("recentclaimed")
    object RecentFound : Route("recentfound")
>>>>>>> Stashed changes
}