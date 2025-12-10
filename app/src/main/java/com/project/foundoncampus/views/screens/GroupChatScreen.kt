package com.project.foundoncampus.views.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.project.foundoncampus.model.ChatMessage
import com.project.foundoncampus.utils.sendMessage
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun GroupChatScreen(navController: NavController) {
    val db = FirebaseFirestore.getInstance()
    val chatMessages = remember { mutableStateListOf<ChatMessage>() }
    var messageText by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val currentUser = FirebaseAuth.getInstance().currentUser?.email ?: "Unknown"
    val scrollState = rememberLazyListState()

    // Firebase real-time listener
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

                // Auto-scroll to bottom
                coroutineScope.launch {
                    scrollState.scrollToItem(chatMessages.size - 1)
                }
            }
    }

    val timeFormatter = remember {
        SimpleDateFormat("hh:mm a", Locale.getDefault())
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        Text(
            text = "Group Chat",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(8.dp),
            color = MaterialTheme.colorScheme.primary
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            state = scrollState,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            itemsIndexed(chatMessages) { index, chat ->
                val isCurrentUser = chat.sender == currentUser
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    horizontalAlignment = if (isCurrentUser) Alignment.End else Alignment.Start
                ) {
                    Surface(
                        color = if (isCurrentUser) MaterialTheme.colorScheme.inversePrimary else MaterialTheme.colorScheme.surfaceVariant,
                        shape = MaterialTheme.shapes.medium,
                        tonalElevation = 2.dp
                    )
                    {
                        Column(
                            modifier = Modifier
                                .padding(8.dp)
                                .widthIn(max = 300.dp)
                        ) {
                            Text(
                                text = if (isCurrentUser) "You" else chat.sender,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = chat.message,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = timeFormatter.format(chat.timestamp),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier
                                    .align(Alignment.End)
                                    .padding(top = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = messageText,
            onValueChange = { messageText = it },
            modifier = Modifier
                .fillMaxWidth(),
            placeholder = { Text("Enter your message...") },
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Send),
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
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send")
                }
            }
        )
    }
}
