package com.project.foundoncampus.views.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactScreen(navController: NavController) {
    val users = listOf("john@example.com", "jane@example.com", "admin@example.com") // mock users

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Contact List") })
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            users.forEach { user ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable {
                            navController.navigate("chat/${user}")
                        },
                    elevation = CardDefaults.cardElevation()
                ) {
                    Text(
                        text = user,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}
