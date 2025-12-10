package com.project.foundoncampus.views.profile

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.project.foundoncampus.nav.Route

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileDetailsScreen(navController: NavController) {

    val auth = FirebaseAuth.getInstance()
    val firebaseUser = auth.currentUser

    // Local editable states
    var fullName by remember { mutableStateOf(firebaseUser?.displayName ?: "") }
    var email by remember { mutableStateOf(firebaseUser?.email ?: "") }
    var phone by remember { mutableStateOf("") } // Firebase does not store phone unless manually added.
    var avatarUri by remember { mutableStateOf(firebaseUser?.photoUrl?.toString()) }

    var isEditing by remember { mutableStateOf(false) }
    var savedStatus by remember { mutableStateOf(false) }

    // Image picker launcher
    val context = LocalContext.current
    val pickImage = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (isEditing && uri != null) {
            try {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: SecurityException) {}

            avatarUri = uri.toString()
            savedStatus = false
        }
    }

    // TOP BAR
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile Details") },
                actions = {
                    // EDIT / SAVE buttons
                    if (isEditing) {
                        TextButton(onClick = {
                            savedStatus = true
                            isEditing = false
                        }) { Text("Save") }
                    } else {
                        TextButton(onClick = { isEditing = true }) { Text("Edit") }
                    }

                    // LOGOUT
                    TextButton(
                        onClick = {
                            auth.signOut()
                            navController.navigate(Route.SignIn.routeName) {
                                popUpTo(Route.Home.routeName) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    ) { Text("Logout") }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            // ---------------------------
            // PROFILE IMAGE
            // ---------------------------
            Row(verticalAlignment = Alignment.CenterVertically) {

                val imageModifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable(enabled = isEditing) {
                        pickImage.launch(arrayOf("image/*"))
                    }

                Box(
                    modifier = imageModifier,
                    contentAlignment = Alignment.Center
                ) {
                    if (avatarUri != null) {
                        Image(
                            painter = rememberAsyncImagePainter(avatarUri),
                            contentDescription = "Profile Photo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Text("No\nPhoto", fontWeight = FontWeight.SemiBold)
                    }
                }

                Spacer(Modifier.width(20.dp))

                Column {
                    if (isEditing) {
                        OutlinedTextField(
                            value = fullName,
                            onValueChange = {
                                fullName = it
                                savedStatus = false
                            },
                            label = { Text("Full Name") },
                            singleLine = true
                        )
                    } else {
                        Text(fullName.ifBlank { "No Name" }, style = MaterialTheme.typography.titleMedium)
                        Text("Full name", style = MaterialTheme.typography.labelSmall)
                    }

                    Spacer(Modifier.height(8.dp))

                    Text(email, style = MaterialTheme.typography.bodySmall)
                    Text("Email", style = MaterialTheme.typography.labelSmall)
                }
            }

            // ---------------------------
            // PHONE NUMBER
            // ---------------------------
            if (isEditing) {
                OutlinedTextField(
                    value = phone,
                    onValueChange = {
                        phone = it
                        savedStatus = false
                    },
                    label = { Text("Contact Number") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Column {
                    Text(phone.ifBlank { "—" }, style = MaterialTheme.typography.bodyLarge)
                    Text("Contact number", style = MaterialTheme.typography.labelSmall)
                }
            }

            // ---------------------------
            // SAVED STATUS
            // ---------------------------
            if (savedStatus && !isEditing) {
                Text("Saved ✓", color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}
