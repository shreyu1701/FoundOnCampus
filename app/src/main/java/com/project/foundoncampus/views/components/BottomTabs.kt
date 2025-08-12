package com.project.foundoncampus.views.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.project.foundoncampus.nav.Route

data class BottomTabItem(
    val baseRoute: String,   // Used for selection match
    val actualRoute: Route,  // Route object
    val icon: ImageVector,
    val label: String
)

private val bottomTabs = listOf(
    BottomTabItem(Route.Home.routeName, Route.Home, Icons.Filled.Home, "Home"),
    BottomTabItem(Route.Search.routeName, Route.Search, Icons.Filled.Search, "Search"),
    BottomTabItem(Route.Create.routeName, Route.Create, Icons.Filled.Add, "Create"),
    BottomTabItem(Route.History.routeName, Route.History, Icons.Filled.Menu, "History"),
    // Profile uses an email-parameterized route; match on base "profile"
    BottomTabItem("profile", Route.Profile, Icons.Filled.Person, "Profile")
)

@Composable
fun BottomTabsBar(
    selectedRoute: String,
    onNavigate: (String) -> Unit,
    currentUserEmail: String
) {
    val cs = MaterialTheme.colorScheme

    NavigationBar(
        containerColor = cs.surface,
        contentColor = cs.onSurface,
        tonalElevation = 3.dp
    ) {
        bottomTabs.forEach { tab ->
            val destinationRoute =
                if (tab.actualRoute.routeName == Route.Profile.routeName) {
                    "profile/$currentUserEmail"
                } else {
                    tab.actualRoute.routeName
                }

            NavigationBarItem(
                selected = selectedRoute.startsWith(tab.baseRoute),
                onClick = { onNavigate(destinationRoute) },
                icon = { Icon(tab.icon, contentDescription = tab.label) },
                label = { Text(tab.label) },
                alwaysShowLabel = true,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = cs.onPrimary,
                    selectedTextColor = cs.secondary,
                    indicatorColor = cs.primary,
                    unselectedIconColor = cs.onSurfaceVariant,
                    unselectedTextColor = cs.onSurfaceVariant
                )
            )
        }
    }
}
