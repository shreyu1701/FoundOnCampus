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
    object Profile : Route("profile")
    object MyListing : Route("my_listing")
}