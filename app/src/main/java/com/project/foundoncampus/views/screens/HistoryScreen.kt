package com.project.foundoncampus.views.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
                        onClick = { selectedItem = item }
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
                            Text("Location: ${item.location}")
                            Text("Contact: ${item.contact}")
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
fun HistoryItemCardFromEntity(item: ListingEntity, onClick: () -> Unit) {
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
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(item.title, style = MaterialTheme.typography.titleMedium)
                Text("Category: ${item.category}")
                Text("Type: ${item.type}")
                Text("Date: ${item.date}")
                Text("Status: ${item.status ?: "-"}")
                Text("Location: ${item.location}")
                Text("Contact: ${item.contact}")
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
