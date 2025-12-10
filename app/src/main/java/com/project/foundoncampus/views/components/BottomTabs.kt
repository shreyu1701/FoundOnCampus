package com.project.foundoncampus.views.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.project.foundoncampus.nav.Route

@Composable
fun BottomTabsBar(
    selectedRoute: String,
    onNavigate: (String) -> Unit,
    currentUserEmail: String
) {
    val bottomItems = listOf(
        BottomNavItem("Home", Route.Home.routeName, Icons.Default.Home),
        BottomNavItem("Search", Route.Search.routeName, Icons.Default.Search),
        BottomNavItem("Create", Route.Create.routeName, Icons.Default.Add),
        BottomNavItem("History", Route.History.routeName, Icons.Default.History),
        BottomNavItem("Group Chat", "group_chat", Icons.Default.Chat) // ✅ UPDATED
    )

    NavigationBar {
        bottomItems.forEach { item ->
            NavigationBarItem(
                selected = selectedRoute == item.route,
                onClick = { onNavigate(item.route) },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) }
            )
        }
    }
}

data class BottomNavItem(
    val label: String,
    val route: String,
    val icon: ImageVector
)
