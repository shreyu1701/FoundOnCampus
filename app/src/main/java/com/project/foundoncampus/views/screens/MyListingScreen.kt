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
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
fun MyListingScreen(
    navController: NavHostController
) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    val sessionManager = remember { SessionManager(context) }
    val scope = rememberCoroutineScope()

    var listings by remember { mutableStateOf(listOf<ListingEntity>()) }

    var userEmail by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var editItem by remember { mutableStateOf<ListingEntity?>(null) }

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
            FloatingActionButton(onClick = {
                editItem = null
                showDialog = true
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
                items(listings) { item ->
                    ListingCard(
                        item = item,
                        onEdit = {
                            editItem = item
                            showDialog = true
                        },
                        onDelete = {
                            scope.launch {
                                db.listingDao().deleteListing(item)
                                refreshListings()
                                Toast.makeText(context, "Deleted!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }
            }
        }

        if (showDialog) {
            ListingDialog(
                item = editItem,
                onDismiss = { showDialog = false },
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
                    showDialog = false
                },
                userEmail = userEmail
            )
        }
    }
}


