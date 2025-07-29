
@file:OptIn(ExperimentalMaterial3Api::class)

package com.project.foundoncampus.views.screens

import android.widget.Toast
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction



import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.project.foundoncampus.model.AppDatabase
import com.project.foundoncampus.model.ListingEntity
import kotlinx.coroutines.launch

@Composable
fun SearchScreen(navController: NavController) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    val scope = rememberCoroutineScope()
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
                        }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear Search")
                        }
                    }
                },
            )

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(filteredItems) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(4.dp, RoundedCornerShape(16.dp))
                            .clickable { selectedItem = item },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F4FF))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(item.title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Text(item.category, fontSize = 14.sp, color = Color.Gray)
                            Text("Date: ${item.date}", fontSize = 12.sp, color = Color.Gray)
                            Text("Contact: ${item.contact}", fontSize = 12.sp, color = Color.Gray)
                            Text("Type: ${item.type} | Status: ${item.status}", fontSize = 12.sp, color = Color.Gray)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(item.description, fontSize = 14.sp)
                        }
                    }
                }
            }
        }
    }

    if (selectedItem != null) {
        AlertDialog(
            onDismissRequest = { selectedItem = null },
            title = {
                Text("${selectedItem!!.title} Details", fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Category: ${selectedItem!!.category}")
                    Text("Date: ${selectedItem!!.date}")
                    Text("Contact: ${selectedItem!!.contact}")
                    Text("Type: ${selectedItem!!.type}")
                    Text("Status: ${selectedItem!!.status}")
                    Text("Description:")
                    Text(selectedItem!!.description)
                }
            },
            confirmButton = {
                if (selectedItem!!.status != "Resolved") {
                    TextButton(onClick = {
                        scope.launch {
                            val updated = selectedItem!!.copy(status = "Resolved")
                            db.listingDao().insertListing(updated)
                            listings = db.listingDao().getAllListings()
                            selectedItem = null
                            Toast.makeText(context, "Marked as resolved", Toast.LENGTH_SHORT).show()
                        }
                    }) {
                        Text("Mark as Resolved")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedItem = null }) {
                    Text("Close")
                }
            }
        )
    }
}
