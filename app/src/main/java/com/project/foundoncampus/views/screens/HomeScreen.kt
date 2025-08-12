package com.project.foundoncampus.views.screens

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.project.foundoncampus.R
import com.project.foundoncampus.model.AppDatabase
import com.project.foundoncampus.model.ListingEntity
import com.project.foundoncampus.nav.Route
import com.project.foundoncampus.util.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.compose.foundation.lazy.LazyColumn

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val context = LocalContext.current
    val db = remember(context) { AppDatabase.getInstance(context) }
    val sessionManager = remember { SessionManager(context) }
    val scope = rememberCoroutineScope()

    var recentLost by remember { mutableStateOf<List<ListingEntity>>(emptyList()) }
    var recentFound by remember { mutableStateOf<List<ListingEntity>>(emptyList()) }
    var recentClaimed by remember { mutableStateOf<List<ListingEntity>>(emptyList()) }
    var userName by remember { mutableStateOf("User") }
    var userEmail by remember { mutableStateOf("") }
    var avatarUrl by remember { mutableStateOf<String?>(null) }

    var selectedItem by remember { mutableStateOf<ListingEntity?>(null) }

    LaunchedEffect(true) {
        scope.launch {
            // Load listings
            val allItems = withContext(Dispatchers.IO) { db.listingDao().getAllListings() }

            recentLost = allItems.filter {
                it.type.equals("Lost", ignoreCase = true) && !it.status.equals("Claimed", ignoreCase = true)
            }.take(3)

            recentFound = allItems.filter {
                it.type.equals("Found", ignoreCase = true) && !it.status.equals("Claimed", ignoreCase = true)
            }.take(3)

            recentClaimed = allItems.filter {
                it.status.equals("Claimed", ignoreCase = true)
            }.take(3)

            // Load session + user/profile
            userEmail = sessionManager.getUserEmail().firstOrNull() ?: ""
            withContext(Dispatchers.IO) {
                val user = db.userDao().getUserByEmail(userEmail)
                val profile = db.profileDao().getProfile(userEmail)
                withContext(Dispatchers.Main) {
                    userName = profile?.fullName?.takeIf { it.isNotBlank() } ?: (user?.name ?: "User")
                    avatarUrl = profile?.avatarUri
                }
            }
        }
    }

    val cs = MaterialTheme.colorScheme
    val ty = MaterialTheme.typography

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hey, $userName", style = ty.titleMedium, color = cs.onPrimary) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = cs.primary,
                    titleContentColor = cs.onPrimary,
                    navigationIconContentColor = cs.onPrimary,
                    actionIconContentColor = cs.onPrimary
                ),
                navigationIcon = {
                    IconButton(onClick = {
                        if (userEmail.isNotBlank()) {
                            navController.navigate(
                                "${Route.ProfileDetails.routeName}?email=${Uri.encode(userEmail)}"
                            )
                        }
                    }) {
                        if (!avatarUrl.isNullOrBlank()) {
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(avatarUrl)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Profile",
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(RoundedCornerShape(50)),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(Icons.Default.Person, contentDescription = "Profile")
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: navigate to settings */ }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            item {
                SectionHeader("Recent Lost") {
                    navController.navigate(Route.RecentLost.routeName)
                }
                HorizontalListSection(recentLost) { selectedItem = it }
                Spacer(Modifier.height(24.dp))
            }

            item {
                SectionHeader("Recent Found") {
                    navController.navigate(Route.RecentFound.routeName)
                }
                HorizontalListSection(recentFound) { selectedItem = it }
                Spacer(Modifier.height(24.dp))
            }

            item {
                SectionHeader("Recent Claimed") {
                    navController.navigate(Route.RecentClaimed.routeName)
                }
                HorizontalListSection(recentClaimed) { selectedItem = it }
            }
        }

        // Item quick view dialog
        selectedItem?.let { item ->
            AlertDialog(
                onDismissRequest = { selectedItem = null },
                confirmButton = {
                    TextButton(onClick = { selectedItem = null }) { Text("Close") }
                },
                title = { Text(item.title, style = ty.titleMedium) },
                text = {
                    Column {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(item.imageUrl)
                                .crossfade(true)
                                .error(R.drawable.ic_launcher_foreground)
                                .placeholder(R.drawable.ic_launcher_foreground)
                                .build(),
                            contentDescription = item.title,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(Modifier.height(8.dp))
                        Text("Category: ${item.category}", style = ty.bodyMedium, color = cs.onSurface)
                        Text("Type: ${item.type}", style = ty.bodyMedium, color = cs.onSurface)
                        Text("Status: ${item.status ?: "-"}", style = ty.bodyMedium, color = cs.onSurface)
                        Text("Location: ${item.location}", style = ty.bodyMedium, color = cs.onSurface)
                        Text("Contact: ${item.contact}", style = ty.bodyMedium, color = cs.onSurface)
                        Spacer(Modifier.height(8.dp))
                        Text("Description:", style = ty.labelLarge, color = cs.onSurface)
                        Text(item.description, style = ty.bodySmall, color = cs.onSurface)
                    }
                }
            )
        }
    }
}

@Composable
fun HorizontalListSection(
    items: List<ListingEntity>,
    onItemClick: (ListingEntity) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items.forEach { item ->
            ItemCard(item, onClick = { onItemClick(item) })
        }
    }
}

@Composable
fun ItemCard(item: ListingEntity, onClick: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    val ty = MaterialTheme.typography

    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = cs.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(Modifier.padding(8.dp)) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(item.imageUrl)
                    .crossfade(true)
                    .error(R.drawable.ic_launcher_foreground)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .build(),
                contentDescription = item.title,
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.height(6.dp))
            Text(item.title, style = ty.titleSmall, color = cs.onSurface, maxLines = 1)
            Text(item.description, style = ty.bodySmall, color = cs.onSurface, maxLines = 2)
        }
    }
}

@Composable
fun SectionHeader(label: String, onClick: () -> Unit) {
    val ty = MaterialTheme.typography
    val cs = MaterialTheme.colorScheme

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = ty.titleMedium, color = cs.onBackground)
        IconButton(onClick = onClick) {
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "See all",
                tint = cs.onBackground
            )
        }
    }
}