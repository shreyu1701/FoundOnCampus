package com.project.foundoncampus.views.screens

import android.annotation.SuppressLint
import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.project.foundoncampus.model.AppDatabase
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(navController: NavHostController, userEmail: String) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    val scope = rememberCoroutineScope()
    var user by remember { mutableStateOf<com.project.foundoncampus.model.UserEntity?>(null) }

    // Counts
    var lostCount by remember { mutableStateOf(0) }
    var foundCount by remember { mutableStateOf(0) }
    var statusChangedCount by remember { mutableStateOf(0) }

    LaunchedEffect(userEmail) {
        user = db.userDao().getUserByEmail(userEmail)
        val allListings = db.listingDao().getAllListings()

        lostCount = allListings.count {
            it.userEmail.equals(userEmail, true) && it.type.equals("lost", true)
        }

        foundCount = allListings.count {
            it.userEmail.equals(userEmail, true) && it.type.equals("found", true)
        }

        statusChangedCount = allListings.count {
            it.userEmail.equals(userEmail, true) &&
                    (it.category.equals("claimed", true) || it.category.equals("returned", true))
        }
    }

    user?.let {
        ProfileContent(
            profilePictureUrl = "https://www.kindpng.com/picc/m/252-2524695_dummy-profile-image-jpg-hd-png-download.png",
            fullName = it.name,
            email = it.email,
            contactNumber = it.phone ?: "Not Provided",
            claimedCount = statusChangedCount,
            foundedCount = foundCount,
            reportedCount = lostCount,
            onChangeEmailClick = {},
            onChangePasswordClick = {},
            onLogoutClick = {
                Toast.makeText(context, "Logging out...", Toast.LENGTH_SHORT).show()
                navController.navigate("signin")
            }
        )
    }
}

@SuppressLint("SuspiciousIndentation")
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
    onChangeEmailClick: () -> Unit,
    onChangePasswordClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = profilePictureUrl,
                contentDescription = "Profile picture",
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(fullName, style = MaterialTheme.typography.titleMedium)
            Text(email, style = MaterialTheme.typography.bodySmall)
            Text(contactNumber, style = MaterialTheme.typography.bodySmall)
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ListingItem("Status Changed Items", claimedCount)
            ListingItem("Found Item Added", foundedCount)
            ListingItem("Lost Item Added", reportedCount)
        }

        Column {
            AccountItem(Icons.Filled.Person, label = "Profile Details") {
                Toast.makeText(context, "Click on Profile Details", Toast.LENGTH_SHORT).show()
            }
            Spacer(modifier = Modifier.height(16.dp))

            AccountItem(Icons.Filled.Menu, label = "My Listing") {
                Toast.makeText(context, "Click on My Listing", Toast.LENGTH_SHORT).show()
            }
            Spacer(modifier = Modifier.height(16.dp))

            AccountItem(Icons.Filled.AccountBox, label = "Account Details") {
                Toast.makeText(context, "Click on Account Details", Toast.LENGTH_SHORT).show()
            }
            Spacer(modifier = Modifier.height(16.dp))

            AccountItem(Icons.AutoMirrored.Filled.ExitToApp, label = "Logout") {
                onLogoutClick()
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onLogoutClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Logout")
        }
    }
}

@Composable
fun ListingItem(label: String, count: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("$count", fontWeight = FontWeight.Bold)
        Text(label)
    }
}

@Composable
fun AccountItem(icon: ImageVector, label: String, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp)
    ) {
        Icon(icon, contentDescription = label, modifier = Modifier.padding(end = 8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge
        )
    }


}

