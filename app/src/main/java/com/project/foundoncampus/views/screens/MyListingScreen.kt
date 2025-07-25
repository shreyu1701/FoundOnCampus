package com.project.foundoncampus.views.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.project.foundoncampus.model.MyListing

@Composable
fun MyListingScreen(
    navController: NavHostController
) {
    val dummyListings = remember {
        mutableStateListOf(
            MyListing(
                id = 1,
                title = "Lost Wallet",
                description = "Black leather wallet near cafeteria",
                imageUrl = "https://eu-images.contentstack.com/v3/assets/blt7dcd2cfbc90d45de/blt6e3ce87e83b602fe/60dbb40b5c97640f9442cbe3/2_264.jpg"
            ),
            MyListing(
                id = 2,
                title = "Found Keys",
                description = "Keychain found near library",
                imageUrl = "https://images.unsplash.com/photo-1517694712202-14dd9538aa97"
            )
        )
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                dummyListings.add(
                    MyListing(
                        id = dummyListings.size + 1,
                        title = "New Item ${dummyListings.size + 1}",
                        description = "Just added dummy data",
                        imageUrl = "https://via.placeholder.com/150"
                    )
                )
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add Listing")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            Text(
                text = "My Listings",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(16.dp)
            )

            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(dummyListings) { item ->
                    ListingCard(
                        item = item,
                        onEdit = {
                            // TODO: Edit logic placeholder
                        },
                        onDelete = {
                            dummyListings.remove(item)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ListingCard(
    item: MyListing,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = rememberAsyncImagePainter(model = item.imageUrl),
                    contentDescription = "Item image",
                    modifier = Modifier
                        .size(60.dp)
                        .padding(end = 12.dp)
                )

                Column {
                    Text(text = item.title, style = MaterialTheme.typography.titleMedium)
                    Text(text = item.description, style = MaterialTheme.typography.bodySmall)
                }
            }

            Row {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                }
            }
        }
    }
}
