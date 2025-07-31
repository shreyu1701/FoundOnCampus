package com.project.foundoncampus.views.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.project.foundoncampus.R
import com.project.foundoncampus.model.AppDatabase
import com.project.foundoncampus.model.ListingEntity
import com.project.foundoncampus.nav.Route
import com.project.foundoncampus.util.SessionManager
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val context = LocalContext.current
    val db = AppDatabase.getInstance(context)
    val sessionManager = remember { SessionManager(context) }
    val scope = rememberCoroutineScope()

    var recentLost by remember { mutableStateOf<List<ListingEntity>>(emptyList()) }
    var recentFound by remember { mutableStateOf<List<ListingEntity>>(emptyList()) }
    var recentClaimed by remember { mutableStateOf<List<ListingEntity>>(emptyList()) }
    var userName by remember { mutableStateOf("User") }

    var selectedItem by remember { mutableStateOf<ListingEntity?>(null) }


    LaunchedEffect(true) {
        scope.launch {
            val allItems = db.listingDao().getAllListings()

            recentLost = allItems.filter {
                it.type.equals("Lost", ignoreCase = true) && !it.status.equals("Claimed", ignoreCase = true)
            }.take(3)

            recentFound = allItems.filter {
                it.type.equals("Found", ignoreCase = true) && !it.status.equals("Claimed", ignoreCase = true)
            }.take(3)

            recentClaimed = allItems.filter {
                it.status.equals("Claimed", ignoreCase = true)
            }.take(3)

            // Get user email and fetch user name
            val email = sessionManager.getUserEmail().firstOrNull() ?: ""
            val user = db.userDao().getUserByEmail(email)
            userName = user?.name ?: "User"
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hey, $userName", fontSize = 16.sp) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Blue,
                    titleContentColor = Color.White
                ),
                navigationIcon = {
                    IconButton(onClick = { /* profile nav */ }) {
                        Icon(Icons.Default.Person, contentDescription = "Profile")
                    }
                },
                actions = {
                    IconButton(onClick = { /* settings nav */ }) {
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
                HorizontalListSection(recentFound){ selectedItem = it }
                Spacer(Modifier.height(24.dp))
            }

            item {
                SectionHeader("Recent Claimed") {
                    navController.navigate(Route.RecentClaimed.routeName)
                }
                HorizontalListSection(recentClaimed){ selectedItem = it }
            }
        }
        selectedItem?.let { item ->
            AlertDialog(
                onDismissRequest = { selectedItem = null },
                confirmButton = {
                    TextButton(onClick = { selectedItem = null }) {
                        Text("Close")
                    }
                },
                title = { Text(item.title) },
                text = {
                    Column {
                        AsyncImage(
                            model = item.imageUrl,
                            contentDescription = item.title,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(Modifier.height(8.dp))
                        Text("Category: ${item.category}")
                        Text("Type: ${item.type}")
                        Text("Status: ${item.status ?: "-"}")
                        Text("Location: ${item.location}")
                        Text("Contact: ${item.contact}")
                        Spacer(Modifier.height(8.dp))
                        Text("Description:")
                        Text(item.description, style = MaterialTheme.typography.bodySmall)
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
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items.forEach { item ->
            ItemCard(item, onClick = { onItemClick(item) })
        }
    }
}


@Composable
fun ItemCard(item: ListingEntity, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .width(140.dp)
            .clickable { onClick() }
    ) {
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
                .clip(RoundedCornerShape(16.dp)),
            contentScale = ContentScale.Crop
        )
        Text(item.title, style = MaterialTheme.typography.titleMedium, maxLines = 1)
        Text(item.description, style = MaterialTheme.typography.bodySmall, maxLines = 2)
    }
}




@Composable
fun SectionHeader(label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.titleMedium)
        IconButton(onClick = onClick) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "See all")
        }
    }
}