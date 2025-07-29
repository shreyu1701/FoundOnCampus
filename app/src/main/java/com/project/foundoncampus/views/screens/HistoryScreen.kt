package com.project.foundoncampus.views.screens

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

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
        scope.launch {
            listings = db.listingDao().getAllListings()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("History") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Blue,
                    titleContentColor = Color.White
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
                    HistoryItemCardFromEntity(
                        item = item,
                        onClick = { selectedItem = item },
                        onDelete = {
                            scope.launch {
                                db.listingDao().deleteListing(item)
                                listings = db.listingDao().getAllListings()
                            }
                        }
                    )
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
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text("Category: ${item.category}")
                            Text("Type: ${item.type}")
                            Text("Date: ${item.date}")
                            Text("Status: ${item.status ?: "-"}")
                            Text("Location: ${item.contact}")
                            Spacer(modifier = Modifier.height(8.dp))
                            HorizontalDivider(
                                Modifier,
                                DividerDefaults.Thickness,
                                DividerDefaults.color
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Description:")
                            Text(item.description, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun HistoryItemCardFromEntity(item: ListingEntity, onClick: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Image, contentDescription = "No Image", modifier = Modifier.size(56.dp))
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(item.title, style = MaterialTheme.typography.titleMedium)
                Text("Category: ${item.category}")
                Text("Type: ${item.type}")
                Text("Date: ${item.date}")
                Text("Status: ${item.status ?: "-"}")
                Text("Location: ${item.contact}")
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete")
            }
        }
    }
}

@Composable
fun FilterChip(label: String, selected: Boolean, onClick: () -> Unit) {
    AssistChip(
        onClick = onClick,
        label = { Text(label) },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = if (selected) Color.Blue else Color.LightGray,
            labelColor = if (selected) Color.White else Color.Black
        )
    )
}
