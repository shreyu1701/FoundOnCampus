package com.project.foundoncampus.views.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.project.foundoncampus.R
import com.project.foundoncampus.model.AppDatabase
import com.project.foundoncampus.model.ListingEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun SearchScreen(navController: NavController) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    val focusManager = LocalFocusManager.current

    var searchQuery by remember { mutableStateOf("") }
    var listings by remember { mutableStateOf(listOf<ListingEntity>()) }
    var selectedItem by remember { mutableStateOf<ListingEntity?>(null) }

    LaunchedEffect(true) {
        listings = withContext(Dispatchers.IO) { db.listingDao().getAllListings() }
    }

    val filteredItems = remember(searchQuery, listings) {
        listings.filter { item ->
            searchQuery.isBlank() ||
                    item.title.contains(searchQuery, ignoreCase = true) ||
                    item.description.contains(searchQuery, ignoreCase = true) ||
                    item.category.contains(searchQuery, ignoreCase = true)
        }
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

            // Search bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                placeholder = { Text("Search by item or description") },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = {
                            searchQuery = ""
                            focusManager.clearFocus()
                        }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear Search")
                        }
                    }
                }
            )

            // Results
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(filteredItems) { item ->
                    ResultCard(
                        item = item,
                        onClick = { selectedItem = item }
                    )
                }
            }
        }
    }

    // Quick details dialog
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
                            .error(R.drawable.ic_launcher_foreground)
                            .placeholder(R.drawable.ic_launcher_foreground)
                            .build(),
                        contentDescription = sel.title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(12.dp)),
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


@Composable
private fun ResultCard(
    item: ListingEntity,
    onClick: () -> Unit
) {
    val cs = MaterialTheme.colorScheme
    val ty = MaterialTheme.typography
    val shortDesc = remember(item.description) { truncateWords(item.description, 18) }

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cs.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .height(IntrinsicSize.Min)
                .clickable { onClick() },
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Image
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(item.imageUrl)
                    .crossfade(true)
                    .error(R.drawable.ic_launcher_foreground)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .build(),
                contentDescription = item.title,
                modifier = Modifier
                    .size(92.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.width(12.dp))

            // Text block
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = item.title,
                    style = ty.titleMedium,
                    color = cs.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Meta row
                Text(
                    text = "${item.category} • ${item.date}",
                    style = ty.bodySmall,
                    color = cs.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Short description (word-truncated)
                Text(
                    text = shortDesc,
                    style = ty.bodySmall,
                    color = cs.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                // Chips row
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Capsule(text = item.type, bg = cs.secondaryContainer, fg = cs.onSecondaryContainer)
                    Capsule(text = item.status ?: "-", bg = cs.tertiaryContainer, fg = cs.onTertiaryContainer)
                }
            }

            Spacer(Modifier.width(8.dp))

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun Capsule(
    text: String,
    bg: androidx.compose.ui.graphics.Color,
    fg: androidx.compose.ui.graphics.Color
) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = fg,
        modifier = Modifier
            .background(bg, CircleShape)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

private fun truncateWords(text: String?, maxWords: Int): String {
    val safe = text?.trim().orEmpty()
    if (safe.isEmpty()) return ""
    val words = safe.split(Regex("\\s+"))
    return if (words.size <= maxWords) safe else words.take(maxWords).joinToString(" ") + "…"
}
