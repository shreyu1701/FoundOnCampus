package com.project.foundoncampus.views.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.project.foundoncampus.nav.Route


data class BottomTabItem(
    val route: Route,
    val icon: ImageVector,
    val label: String
)

val bottomTabs = listOf(
    BottomTabItem(Route.Home, Icons.Default.Home, "Home"),
    BottomTabItem(Route.Search, Icons.Default.Search, "Search"),
    BottomTabItem(Route.Create, Icons.Default.Add, "Create"),
    BottomTabItem(Route.History, Icons.Default.Info, "History"),
    BottomTabItem(Route.Profile, Icons.Default.Person, "Profile"),
)

@Composable
fun BottomTabsBar(
    selectedRoute: String,
    onTabSelected: (Route) -> Unit
) {
    NavigationBar {
        bottomTabs.forEach { tab ->
            NavigationBarItem(
                icon = { Icon(tab.icon, contentDescription = tab.label) },
                label = { Text(tab.label) },
                selected = selectedRoute == tab.route.routeName,
                onClick = { onTabSelected(tab.route) }
            )
        }
    }
}