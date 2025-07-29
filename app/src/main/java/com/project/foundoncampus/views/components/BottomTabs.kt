package com.project.foundoncampus.views.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.project.foundoncampus.nav.Route

data class BottomTabItem(
    val baseRoute: String,           // Used for selection match
    val actualRoute: Route,          // Route object
    val icon: ImageVector,
    val label: String
)

val bottomTabs = listOf(
    BottomTabItem(Route.Home.routeName, Route.Home, Icons.Default.Home, "Home"),
    BottomTabItem(Route.Search.routeName, Route.Search, Icons.Default.Search, "Search"),
    BottomTabItem(Route.Create.routeName, Route.Create, Icons.Default.Add, "Create"),
    BottomTabItem(Route.History.routeName, Route.History, Icons.Default.Menu, "History"),
//    BottomTabItem(Route.Profile.routeName, Route.Profile, Icons.Default.Person, "Profile")
    BottomTabItem("profile", Route.Profile, Icons.Default.Person, "Profile")

)

@Composable
fun BottomTabsBar(
    selectedRoute: String,
    onNavigate: (String) -> Unit,
    currentUserEmail: String
) {
    NavigationBar {
        bottomTabs.forEach { tab ->
            val destinationRoute = if (tab.actualRoute.routeName == Route.Profile.routeName) {
                "profile/$currentUserEmail"
            } else {
                tab.actualRoute.routeName
            }

            NavigationBarItem(
                icon = { Icon(tab.icon, contentDescription = tab.label) },
                label = { Text(tab.label) },
                selected = selectedRoute.startsWith(tab.baseRoute),
                onClick = { onNavigate(destinationRoute) }
            )
        }
    }
}
