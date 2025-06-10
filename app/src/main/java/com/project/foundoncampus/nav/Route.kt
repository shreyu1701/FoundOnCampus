package com.project.foundoncampus.nav

sealed class Route(val routeName: String) {

    object Home : Route("home")
    object Search : Route("search")
    object Create : Route("create")
    object History : Route("history")
    object Profile : Route("profile")
}