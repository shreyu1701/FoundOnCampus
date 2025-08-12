package com.project.foundoncampus.views.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.project.foundoncampus.model.AppDatabase
import com.project.foundoncampus.model.ListingEntity

@Composable
fun SearchScreen(navController: NavController) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    val focusManager = LocalFocusManager.current

    var searchQuery by remember { mutableStateOf("") }
    var listings by remember { mutableStateOf(listOf<ListingEntity>()) }
    var selectedItem by remember { mutableStateOf<ListingEntity?>(null) }

    LaunchedEffect(true) {
        listings = db.listingDao().getAllListings()
    }

    val filteredItems = listings.filter { item ->
        searchQuery.isBlank() ||
                item.title.contains(searchQuery, ignoreCase = true) ||
                item.description.contains(searchQuery, ignoreCase = true) ||
                item.category.contains(searchQuery, ignoreCase = true)
    }

    val cs = MaterialTheme.colorScheme
    val ty = MaterialTheme.typography

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                placeholder = { Text("Search by item, category, or description") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = {
                            searchQuery = ""
                            focusManager.clearFocus()
                        }) { Icon(Icons.Default.Close, contentDescription = "Clear Search") }
                    }
                },
            )

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(filteredItems) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedItem = item },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = cs.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
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
                                    .size(80.dp)
                                    .padding(end = 12.dp)
                                    .clip(RoundedCornerShape(12.dp)),
                                contentScale = ContentScale.Crop
                            )

                            Column {
                                Text(item.title, style = ty.titleSmall, color = cs.onSurface, maxLines = 1)
                                Text(item.category, style = ty.bodySmall, color = cs.onSurfaceVariant)
                                Text("Date: ${item.date}", style = ty.bodySmall, color = cs.onSurfaceVariant)
                                Text("Contact: ${item.contact}", style = ty.bodySmall, color = cs.onSurfaceVariant)
                                Text(
                                    "Type: ${item.type} | Status: ${item.status ?: "-"}",
                                    style = ty.bodySmall,
                                    color = cs.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    selectedItem?.let { sel ->
        AlertDialog(
            onDismissRequest = { selectedItem = null },
            confirmButton = { TextButton(onClick = { selectedItem = null }) { Text("Okay") } },
            dismissButton = { TextButton(onClick = { selectedItem = null }) { Text("Close") } },
            title = { Text("${sel.title} Details", style = ty.titleMedium) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(sel.imageUrl)
                            .crossfade(true)
                            .error(com.project.foundoncampus.R.drawable.ic_launcher_foreground)
                            .placeholder(com.project.foundoncampus.R.drawable.ic_launcher_foreground)
                            .build(),
                        contentDescription = sel.title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Category: ${sel.category}", style = ty.bodyMedium)
                    Text("Date: ${sel.date}", style = ty.bodyMedium)
                    Text("Contact: ${sel.contact}", style = ty.bodyMedium)
                    Text("Type: ${sel.type}", style = ty.bodyMedium)
                    Text("Status: ${sel.status}", style = ty.bodyMedium)
                    Text("Description:", style = ty.labelLarge)
                    Text(sel.description, style = ty.bodySmall)
                }
            }
        )
    }
}
