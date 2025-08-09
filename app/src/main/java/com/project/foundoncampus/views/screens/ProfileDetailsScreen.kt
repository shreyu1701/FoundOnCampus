package com.project.foundoncampus.views.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.project.foundoncampus.model.AppDatabase
import com.project.foundoncampus.model.ProfileEntity
import com.project.foundoncampus.model.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileDetailsScreen(
    navController: NavController,
    userEmail: String // 🔑 pass the logged-in user's email
) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    val scope = rememberCoroutineScope()

    // Data
    var user by remember { mutableStateOf<UserEntity?>(null) }
    var existing by remember { mutableStateOf<ProfileEntity?>(null) }

    // Editable fields
    var fullName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var avatarUri by remember { mutableStateOf<String?>(null) }

    // UI state
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var savedOnce by remember { mutableStateOf(false) }
    var isEditing by remember { mutableStateOf(false) }

    val pickImage = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (isEditing) {
            avatarUri = uri?.toString()
            savedOnce = false
        }
    }

    // Load current user + profile by email
    LaunchedEffect(userEmail) {
        loading = true
        withContext(Dispatchers.IO) {
            val u = db.userDao().getUserByEmail(userEmail)
            val p = db.profileDao().getProfile(userEmail) // use email as profile key
            withContext(Dispatchers.Main) {
                user = u
                existing = p
                fullName = p?.fullName ?: (u?.name ?: "")
                phone = p?.phone ?: (u?.phone ?: "")
                avatarUri = p?.avatarUri
                loading = false
            }
        }
    }

    fun save() {
        if (fullName.isBlank()) { error = "Full name is required"; return }
        val digits = phone.filter { it.isDigit() }
        if (phone.isNotBlank() && digits.length !in 7..15) { error = "Invalid phone number"; return }
        error = null

        // preserve fields we don't edit
        val preservedStudentId = existing?.studentId
        val preservedDepartment = existing?.department

        scope.launch(Dispatchers.IO) {
            db.profileDao().upsert(
                ProfileEntity(
                    userId = userEmail,             // 🔑 key by email
                    fullName = fullName.trim(),
                    phone = phone.ifBlank { null },
                    studentId = preservedStudentId,
                    department = preservedDepartment,
                    avatarUri = avatarUri,
                    updatedAt = System.currentTimeMillis()
                )
            )
            withContext(Dispatchers.Main) {
                savedOnce = true
                isEditing = false
                existing = existing?.copy(
                    fullName = fullName.trim(),
                    phone = phone.ifBlank { null },
                    avatarUri = avatarUri,
                    updatedAt = System.currentTimeMillis()
                ) ?: ProfileEntity(
                    userId = userEmail,
                    fullName = fullName.trim(),
                    phone = phone.ifBlank { null },
                    studentId = preservedStudentId,
                    department = preservedDepartment,
                    avatarUri = avatarUri,
                    updatedAt = System.currentTimeMillis()
                )
            }
        }
    }

    fun cancelEdit() {
        fullName = existing?.fullName ?: (user?.name ?: "")
        phone = existing?.phone ?: (user?.phone ?: "")
        avatarUri = existing?.avatarUri
        error = null
        isEditing = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                title = { Text("Profile Details") },
                actions = {
                    if (!loading) {
                        if (isEditing) {
                            TextButton(onClick = ::cancelEdit) { Text("Cancel") }
                            TextButton(onClick = ::save) { Text("Save") }
                        } else {
                            TextButton(onClick = { isEditing = true; savedOnce = false }) {
                                Text("Edit")
                            }
                        }
                    }
                }
            )
        }
    ) { inner ->
        if (loading) {
            Box(Modifier.fillMaxSize().padding(inner), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(inner)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Avatar + Name
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(84.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .let { if (isEditing) it.clickable { pickImage.launch("image/*") } else it },
                        contentAlignment = Alignment.Center
                    ) {
                        if (avatarUri != null) {
                            Image(
                                painter = rememberAsyncImagePainter(avatarUri),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Text(if (isEditing) "Add\nPhoto" else "No\nPhoto",
                                style = MaterialTheme.typography.labelMedium)
                        }
                    }
                    Spacer(Modifier.width(16.dp))
                    Column(Modifier.weight(1f)) {
                        if (isEditing) {
                            OutlinedTextField(
                                value = fullName,
                                onValueChange = { fullName = it; savedOnce = false },
                                label = { Text("Full name *") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                        } else {
                            Text(fullName.ifBlank { "—" }, style = MaterialTheme.typography.titleMedium)
                            Text("Full name", style = MaterialTheme.typography.labelSmall)
                        }
                        Spacer(Modifier.height(8.dp))
                        // Email is read-only and shown always
                        Text(userEmail, style = MaterialTheme.typography.bodySmall)
                        Text("Email", style = MaterialTheme.typography.labelSmall)
                    }
                }

                if (isEditing) {
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it; savedOnce = false },
                        label = { Text("Contact number") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    Column {
                        Text(phone.ifBlank { "—" }, style = MaterialTheme.typography.bodyLarge)
                        Text("Contact number", style = MaterialTheme.typography.labelSmall)
                    }
                }

                if (error != null) {
                    Text(error!!, color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.SemiBold)
                }

                if (savedOnce && !isEditing) {
                    Text("Saved ✓", color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}
