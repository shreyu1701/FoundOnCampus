package com.project.foundoncampus.views.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.project.foundoncampus.model.AppDatabase
import com.project.foundoncampus.model.ListingEntity
import com.project.foundoncampus.views.components.InfoRow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecentClaimedScreen(navController: NavController) {
    val context = LocalContext.current
    val db = AppDatabase.getInstance(context)
    val scope = rememberCoroutineScope()

    var claimedItems by remember { mutableStateOf<List<ListingEntity>>(emptyList()) }
    var selectedItem by remember { mutableStateOf<ListingEntity?>(null) }
    var uploaderEmail by remember { mutableStateOf("") }

    // Load claimed items once
    LaunchedEffect(Unit) {
        scope.launch {
            claimedItems = db.listingDao().getAllClaimed()
        }
    }

    // When a card is selected, fetch uploader details
    selectedItem?.let { item ->
        LaunchedEffect(item) {
            val user = db.userDao().getUserByEmail(item.userEmail)
            uploaderEmail = user?.email ?: "Not available"
        }
    }

    // Dialog showing full item details
    if (selectedItem != null) {
        AlertDialog(
            onDismissRequest = { selectedItem = null },
            confirmButton = {
                TextButton(onClick = { selectedItem = null }) {
                    Text("Close")
                }
            },
            title = { Text("Claimed Item Details") },
            text = {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .fillMaxWidth()
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(selectedItem!!.imageUrl),
                        contentDescription = "Item Image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    InfoRow("Title", selectedItem!!.title)
                    InfoRow("Description", selectedItem!!.description)
                    InfoRow("Location", selectedItem!!.location)
                    InfoRow("Campus", selectedItem!!.campus)
                    InfoRow("Date", selectedItem!!.date)
                    InfoRow("Status", selectedItem!!.status ?: "N/A")
                    InfoRow("Contact", selectedItem!!.contact)
                    InfoRow("Uploaded By", uploaderEmail)
                }
            }
        )
    }

    // UI layout
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recent Claimed") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Blue,
                    titleContentColor = Color.White
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            claimedItems.forEach { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedItem = item },
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Image(
                            painter = rememberAsyncImagePainter(item.imageUrl),
                            contentDescription = null,
                            modifier = Modifier
                                .height(180.dp)
                                .fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(item.title, style = MaterialTheme.typography.titleMedium)
                        Text(item.description, style = MaterialTheme.typography.bodySmall)
                    }
                }
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}