package com.project.foundoncampus.views.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
fun MyListingScreen(
    navController: NavHostController
) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    val sessionManager = remember { SessionManager(context) }
    val scope = rememberCoroutineScope()

    var listings by remember { mutableStateOf(listOf<ListingEntity>()) }
    var userEmail by remember { mutableStateOf("") }

    // Add/Edit dialog state
    var showEditor by remember { mutableStateOf(false) }
    var editItem by remember { mutableStateOf<ListingEntity?>(null) }

    // Delete confirmation state
    var confirmDeleteFor by remember { mutableStateOf<ListingEntity?>(null) }

    // Load data from DB
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
                    ListingCard(
                        item = item,
                        onEdit = {
                            editItem = item
                            showEditor = true
                        },
                        onDelete = {
                            // open confirmation dialog instead of deleting immediately
                            confirmDeleteFor = item
                        }
                    )
                }
            }
        }

        // Add/Edit dialog
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

        // Delete confirmation dialog
        val toDelete = confirmDeleteFor
        if (toDelete != null) {
            AlertDialog(
                onDismissRequest = { confirmDeleteFor = null },
                title = { Text("Delete listing?") },
                text = {
                    Text(
                        "This will permanently delete “${toDelete.title}”. " +
                                "You can’t undo this action."
                    )
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
                    ) { Text("Delete", color = MaterialTheme.colorScheme.error) }
                },
                dismissButton = {
                    TextButton(onClick = { confirmDeleteFor = null }) { Text("Cancel") }
                }
            )
        }
    }
}