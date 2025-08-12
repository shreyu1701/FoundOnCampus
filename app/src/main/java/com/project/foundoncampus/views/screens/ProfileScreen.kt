package com.project.foundoncampus.views.screens

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.project.foundoncampus.model.AppDatabase
import com.project.foundoncampus.model.UserEntity
import com.project.foundoncampus.nav.Route
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun ProfileScreen(navController: NavHostController, userEmail: String) {
    val db = remember { AppDatabase.getInstance(navController.context) }

    var user by remember { mutableStateOf<UserEntity?>(null) }
    var fullName by remember { mutableStateOf("") }
    var contactNumber by remember { mutableStateOf("") }
    var avatarUrl by remember { mutableStateOf<String?>(null) }

    var lostCount by remember { mutableIntStateOf(0) }
    var foundCount by remember { mutableIntStateOf(0) }
    var statusChangedCount by remember { mutableIntStateOf(0) }

    LaunchedEffect(userEmail) {
        withContext(Dispatchers.IO) {
            val u = db.userDao().getUserByEmail(userEmail)
            val p = db.profileDao().getProfile(userEmail)

            val listings = db.listingDao().getAllListings().filter { it.userEmail.equals(userEmail, true) }

            withContext(Dispatchers.Main) {
                user = u
                fullName = p?.fullName?.takeIf { it.isNotBlank() } ?: (u?.name ?: "Your Name")
                contactNumber = p?.phone ?: (u?.phone ?: "Not Provided")
                avatarUrl = p?.avatarUri

                lostCount = listings.count { it.type.equals("lost", true) }
                foundCount = listings.count { it.type.equals("found", true) }
                statusChangedCount = listings.count {
                    it.userEmail.equals(userEmail, true) &&
                            (it.status.equals("Claimed", true) || it.status.equals("Resolved", true))
                }
            }
        }
    }

    ProfileContent(
        profilePictureUrl = avatarUrl
            ?: "https://www.kindpng.com/picc/m/252-2524695_dummy-profile-image-jpg-hd-png-download.png",
        fullName = fullName,
        email = userEmail,
        contactNumber = contactNumber,
        claimedCount = statusChangedCount,
        foundedCount = foundCount,
        reportedCount = lostCount,
        navController = navController
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileContent(
    profilePictureUrl: String,
    fullName: String,
    email: String,
    contactNumber: String,
    claimedCount: Int,
    foundedCount: Int,
    reportedCount: Int,
    navController: NavController
) {
    val cs = MaterialTheme.colorScheme
    val ty = MaterialTheme.typography
    var showDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            AsyncImage(
                model = profilePictureUrl,
                contentDescription = "Profile picture",
                modifier = Modifier
                    .size(84.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(fullName, style = ty.titleMedium, color = cs.onBackground)
            Text(email, style = ty.bodySmall, color = cs.onSurfaceVariant)
            Text(contactNumber, style = ty.bodySmall, color = cs.onSurfaceVariant)
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ListingItem("Status Changed", claimedCount)
            ListingItem("Found Items", foundedCount)
            ListingItem("Lost Items", reportedCount)
        }

        Column {
            AccountItem(Icons.Filled.Person, "Profile Details") {
                navController.navigate("${Route.ProfileDetails.routeName}?email=${Uri.encode(email)}")
            }
            AccountItem(Icons.Filled.Menu, "My Listing") {
                navController.navigate(Route.MyListing.routeName)
            }
            AccountItem(Icons.Filled.AccountBox, "Account Details") {
                navController.navigate("${Route.AccountDetails.routeName}?email=${Uri.encode(email)}")
            }
            AccountItem(Icons.AutoMirrored.Filled.ExitToApp, "Logout") { showDialog = true }
        }

        Spacer(modifier = Modifier.weight(1f))
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    navController.navigate(Route.SignIn.routeName)
                }) { Text("Yes") }
            },
            dismissButton = { TextButton(onClick = { showDialog = false }) { Text("Cancel") } },
            title = { Text("Confirm Logout") },
            text = { Text("Are you sure you want to logout?") }
        )
    }
}

@Composable
fun ListingItem(label: String, count: Int) {
    val ty = MaterialTheme.typography
    val cs = MaterialTheme.colorScheme
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("$count", style = ty.titleMedium, color = cs.onBackground, fontWeight = FontWeight.Bold)
        Text(label, style = ty.bodySmall, color = cs.onSurfaceVariant)
    }
}

@Composable
fun AccountItem(icon: ImageVector, label: String, onClick: () -> Unit) {
    val ty = MaterialTheme.typography
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp)
    ) {
        Icon(icon, contentDescription = label, modifier = Modifier.padding(end = 12.dp))
        Text(text = label, style = ty.bodyLarge)
    }
}