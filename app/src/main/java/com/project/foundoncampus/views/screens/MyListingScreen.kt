package com.project.foundoncampus.views.screens

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.project.foundoncampus.model.AppDatabase
import com.project.foundoncampus.model.ListingEntity
import com.project.foundoncampus.util.SessionManager
import com.project.foundoncampus.views.components.ListingCard
import com.project.foundoncampus.views.components.ListingDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@Composable
fun MyListingScreen(navController: NavHostController) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    val sessionManager = remember { SessionManager(context) }
    val scope = rememberCoroutineScope()

    var listings by remember { mutableStateOf(listOf<ListingEntity>()) }
    var userEmail by remember { mutableStateOf("") }

    var showEditor by remember { mutableStateOf(false) }
    var editItem by remember { mutableStateOf<ListingEntity?>(null) }
    var confirmDeleteFor by remember { mutableStateOf<ListingEntity?>(null) }

    LaunchedEffect(Unit) {
        val email = sessionManager.getUserEmail().first() ?: ""
        userEmail = email
        listings = db.listingDao().getListingsByUserEmail(userEmail)
    }

    fun refreshListings() {
        scope.launch {
            listings = db.listingDao().getListingsByUserEmail(userEmail)
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    editItem = null
                    showEditor = true
                },
            ) {
                Icon(
                    Icons.Rounded.Add,
                    contentDescription = "Add Listing",
                    tint = MaterialTheme.colorScheme.secondary
                )
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
                items(listings) { item ->
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Listing card (reusing yours)
                        ListingCard(
                            item = item,
                            onEdit = {
                                editItem = item
                                showEditor = true
                            },
                            onDelete = {
                                confirmDeleteFor = item
                            }
                        )

                        // 💬 Contact button
                        Button(
                            onClick = {
                                val intent = Intent(Intent.ACTION_SENDTO).apply {
                                    data = Uri.parse("mailto:${item.contact}")
                                    putExtra(Intent.EXTRA_SUBJECT, "About your Lost/Found item: ${item.title}")
                                    putExtra(
                                        Intent.EXTRA_TEXT,
                                        "Hi,\n\nI saw your listing titled \"${item.title}\" reported as ${item.type} on ${item.date}.\n\nI'd like to connect with you about it.\n\nThanks!"
                                    )
                                }

                                try {
                                    context.startActivity(intent)
                                } catch (e: ActivityNotFoundException) {
                                    Toast.makeText(context, "No email app found.", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Contact Owner")
                        }
                    }
                }
            }
        }

        if (showEditor) {
            ListingDialog(
                item = editItem,
                onDismiss = { showEditor = false },
                onSave = { updated ->
                    scope.launch {
                        withContext(Dispatchers.IO) {
                            if (updated.id == 0) {
                                db.listingDao().insertListing(updated)
                            } else {
                                db.listingDao().updateListing(updated)
                            }
                            refreshListings()
                        }
                    }
                    showEditor = false
                },
                userEmail = userEmail
            )
        }

        val toDelete = confirmDeleteFor
        if (toDelete != null) {
            AlertDialog(
                onDismissRequest = { confirmDeleteFor = null },
                title = { Text("Delete listing?") },
                text = {
                    Text("This will permanently delete “${toDelete.title}”. You can’t undo this action.")
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            scope.launch {
                                withContext(Dispatchers.IO) {
                                    db.listingDao().deleteListing(toDelete)
                                }
                                refreshListings()
                                confirmDeleteFor = null
                                Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show()
                            }
                        }
                    ) {
                        Text("Delete", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { confirmDeleteFor = null }) { Text("Cancel") }
                }
            )
        }
    }
}
