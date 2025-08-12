package com.project.foundoncampus.views.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.project.foundoncampus.model.AppDatabase
import com.project.foundoncampus.model.ListingEntity
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(navController: NavController) {
    val context = LocalContext.current
    val db = AppDatabase.getInstance(context)
    val scope = rememberCoroutineScope()
    var listings by remember { mutableStateOf<List<ListingEntity>>(emptyList()) }
    var selectedFilter by remember { mutableStateOf("All") }
    var selectedSort by remember { mutableStateOf("Newest") }
    var selectedItem by remember { mutableStateOf<ListingEntity?>(null) }

    LaunchedEffect(true) {
        scope.launch { listings = db.listingDao().getAllListings() }
    }

    val cs = MaterialTheme.colorScheme
    val ty = MaterialTheme.typography

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("History", style = ty.titleMedium, color = cs.onPrimary) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = cs.primary,
                    titleContentColor = cs.onPrimary
                )
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            Row(modifier = Modifier.padding(8.dp)) {
                FilterChip(label = "All", selected = selectedFilter == "All") { selectedFilter = "All" }
                Spacer(modifier = Modifier.width(8.dp))
                FilterChip(label = "Lost", selected = selectedFilter == "Lost") { selectedFilter = "Lost" }
                Spacer(modifier = Modifier.width(8.dp))
                FilterChip(label = "Found", selected = selectedFilter == "Found") { selectedFilter = "Found" }
            }

            Row(modifier = Modifier.padding(8.dp)) {
                FilterChip(label = "Newest", selected = selectedSort == "Newest") { selectedSort = "Newest" }
                Spacer(modifier = Modifier.width(8.dp))
                FilterChip(label = "Oldest", selected = selectedSort == "Oldest") { selectedSort = "Oldest" }
                Spacer(modifier = Modifier.width(8.dp))
                FilterChip(label = "Status", selected = selectedSort == "Status") { selectedSort = "Status" }
            }

            LazyColumn {
                var filteredListings = listings.filter {
                    selectedFilter == "All" || it.type.equals(selectedFilter, ignoreCase = true)
                }

                filteredListings = when (selectedSort) {
                    "Newest" -> filteredListings.sortedByDescending { it.date }
                    "Oldest" -> filteredListings.sortedBy { it.date }
                    "Status" -> filteredListings.sortedBy { it.status ?: "" }
                    else -> filteredListings
                }

                items(filteredListings) { item ->
                    HistoryItemCardFromEntity(item = item, onClick = { selectedItem = item })
                }
            }

            selectedItem?.let { item ->
                AlertDialog(
                    onDismissRequest = { selectedItem = null },
                    confirmButton = { TextButton(onClick = { selectedItem = null }) { Text("Close") } },
                    title = { Text(item.title, style = ty.titleMedium) },
                    text = {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text("Category: ${item.category}", style = ty.bodyMedium)
                            Text("Type: ${item.type}", style = ty.bodyMedium)
                            Text("Date: ${item.date}", style = ty.bodyMedium)
                            Text("Status: ${item.status ?: "-"}", style = ty.bodyMedium)
                            Text("Location: ${item.location}", style = ty.bodyMedium)
                            Text("Contact: ${item.contact}", style = ty.bodyMedium)
                            Spacer(modifier = Modifier.height(8.dp))
                            Divider()
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Description:", style = ty.labelLarge)
                            Text(item.description, style = ty.bodySmall)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun HistoryItemCardFromEntity(item: ListingEntity, onClick: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    val ty = MaterialTheme.typography

    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = cs.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(item.imageUrl)
                    .crossfade(true)
                    .error(com.project.foundoncampus.R.drawable.ic_launcher_foreground)
                    .placeholder(com.project.foundoncampus.R.drawable.ic_launcher_foreground)
                    .build(),
                contentDescription = item.title,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(item.title, style = ty.titleSmall, color = cs.onSurface, maxLines = 1)
                Text("Category: ${item.category}", style = ty.bodySmall, color = cs.onSurfaceVariant)
                Text("Type: ${item.type}", style = ty.bodySmall, color = cs.onSurfaceVariant)
                Text("Date: ${item.date}", style = ty.bodySmall, color = cs.onSurfaceVariant)
                Text("Status: ${item.status ?: "-"}", style = ty.bodySmall, color = cs.onSurfaceVariant)
                Text("Location: ${item.location}", style = ty.bodySmall, color = cs.onSurfaceVariant)
                Text("Contact: ${item.contact}", style = ty.bodySmall, color = cs.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun FilterChip(label: String, selected: Boolean, onClick: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    val selectedContainer = cs.secondary
    val selectedLabel = cs.onSecondary
    val unselectedContainer = cs.surfaceVariant
    val unselectedLabel = cs.onSurfaceVariant

    AssistChip(
        onClick = onClick,
        label = { Text(label) },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = if (selected) selectedContainer else unselectedContainer,
            labelColor = if (selected) selectedLabel else unselectedLabel
        )
    )
}