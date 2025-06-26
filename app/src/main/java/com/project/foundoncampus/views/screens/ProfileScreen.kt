package com.project.foundoncampus.views.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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



@Composable
fun ProfileScreen(navController: NavHostController){
    ProfileContent(
        profilePictureUrl = "https://www.kindpng.com/picc/m/252-2524695_dummy-profile-image-jpg-hd-png-download.png",
        fullName = "John Doe",
        email = "john.doe@example.com",
        contactNumber = "+1 234 567 890",
        claimedCount = 5,
        foundedCount = 3,
        reportedCount = 2,
        onChangeEmailClick = { /* navController.navigate(Route.ChangeEmail.routeName) */ },
        onChangePasswordClick = { /* navController.navigate(Route.ChangePassword.routeName) */ },
        onLogoutClick = { /* handle logout & navController.navigate(Route.Login.routeName) */ }
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
                  onChangeEmailClick: () -> Unit,
                  onChangePasswordClick: () -> Unit,
                  onLogoutClick: () -> Unit) {
    var context = LocalContext.current
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Details
            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally)
            {
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

            // My Listing
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ListingItem("Claimed", claimedCount)
                ListingItem("Founded", foundedCount)
                ListingItem("Reported", reportedCount)
            }

            // Account
            Column {
                    AccountItem(Icons.Filled.Person, label = "Profile Details", onClick = {
                        Toast.makeText(context, "Click on Profile Details", Toast.LENGTH_SHORT).show()
                    })
                    Spacer(modifier = Modifier.height(16.dp))

                    AccountItem(Icons.Filled.Menu, label = "My Listing", onClick = {
                        Toast.makeText(context, "Click on My Listing", Toast.LENGTH_SHORT).show()
                    })
                    Spacer(modifier = Modifier.height(16.dp))

                    AccountItem(Icons.Filled.AccountBox, label = "Account Details", onClick = {
                        Toast.makeText(context, "Click on Account Details", Toast.LENGTH_SHORT).show()
                    })
                    Spacer(modifier = Modifier.height(16.dp))

                    AccountItem(Icons.AutoMirrored.Filled.ExitToApp, label = "Logout", onClick = {
                        Toast.makeText(context, "Click on Logout", Toast.LENGTH_SHORT).show()
                    })
            }

            Spacer(modifier = Modifier.weight(1f))

            // Logout
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
        horizontalArrangement = Arrangement.SpaceEvenly
    ){
        Icon(icon, contentDescription = label)
        Text(
            text = label,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(vertical = 12.dp),
            style = MaterialTheme.typography.bodyLarge
        )
    }


}

@Preview(showBackground = true)
@Composable
fun ProfileContentPreview() {
    ProfileContent(
        profilePictureUrl = "https://www.kindpng.com/picc/m/252-2524695_dummy-profile-image-jpg-hd-png-download.png",
        fullName = "John Doe",
        email = "john.doe@example.com",
        contactNumber = "+1 234 567 890",
        claimedCount = 5,
        foundedCount = 3,
        reportedCount = 2,
        onChangeEmailClick = {},
        onChangePasswordClick = {},
        onLogoutClick = {}
    )
}
