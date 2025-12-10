package com.project.foundoncampus.views.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.project.foundoncampus.model.ChatMessage
import com.project.foundoncampus.utils.sendMessage
import java.util.*

@Composable
fun GroupChatScreen(navController: NavController) {
    val db = FirebaseFirestore.getInstance()
    val chatMessages = remember { mutableStateListOf<ChatMessage>() }
    var messageText by remember { mutableStateOf("") }

    val currentUser = FirebaseAuth.getInstance().currentUser?.email ?: "Unknown"

    // Real-time listener
    LaunchedEffect(Unit) {
        db.collection("group_chat")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null || snapshot == null) return@addSnapshotListener

                val messages = snapshot.documents.mapNotNull { doc ->
                    val message = doc.getString("message") ?: return@mapNotNull null
                    val senderEmail = doc.getString("senderEmail") ?: "Unknown"
                    val timestamp = doc.getTimestamp("timestamp")?.toDate() ?: Date()
                    ChatMessage(sender = senderEmail, message = message, timestamp = timestamp)
                }

                chatMessages.clear()
                chatMessages.addAll(messages)
            }
    }

    // UI
    MaterialTheme {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(chatMessages) { chat ->
                        Text(
                            text = "${chat.sender}: ${chat.message}",
                            color = Color.Black
                        )
                    }
                }

                OutlinedTextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Enter your message") },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Send
                    ),
                    keyboardActions = KeyboardActions(
                        onSend = {
                            if (messageText.isNotBlank()) {
                                sendMessage(db, currentUser, messageText)
                                messageText = ""
                            }
                        }
                    ),
                    trailingIcon = {
                        IconButton(onClick = {
                            if (messageText.isNotBlank()) {
                                sendMessage(db, currentUser, messageText)
                                messageText = ""
                            }
                        }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Send"
                            )
                        }
                    }
                )
            }
        }
    }
}
