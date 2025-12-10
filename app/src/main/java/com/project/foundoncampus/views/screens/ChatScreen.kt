package com.project.foundoncampus.views.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(backStackEntry: NavBackStackEntry) {
    val recipient = backStackEntry.arguments?.getString("user") ?: "Unknown"
    var message by remember { mutableStateOf("") }
    var chatLog by remember { mutableStateOf(listOf<String>()) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Chat with $recipient") })
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            Text("Chat Log:", style = MaterialTheme.typography.bodyLarge)

            Column(modifier = Modifier.weight(1f)) {
                chatLog.forEach {
                    Text("You: $it", modifier = Modifier.padding(8.dp))
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextField(
                    value = message,
                    onValueChange = { message = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Type message...") }
                )
                Button(onClick = {
                    if (message.isNotBlank()) {
                        chatLog = chatLog + message
                        message = ""
                    }
                }) {
                    Text("Send")
                }
            }
        }
    }
}
