package com.project.foundoncampus.views.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.project.foundoncampus.model.User
import com.project.foundoncampus.nav.Route
import com.project.foundoncampus.utils.FileUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignInScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isSubmitted by remember { mutableStateOf(false) } // Track if form is submitted
    var emailError by remember { mutableStateOf(false) } // Track email error
    var passwordError by remember { mutableStateOf(false) } // Track password error

    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hey, User") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Blue,
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Card wrapper
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .padding(vertical = 32.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    Text("Sign In", fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.CenterHorizontally))

                    Spacer(modifier = Modifier.height(24.dp))

                    // Email Field
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Humber Email") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = isSubmitted && email.isBlank() // Show error only if form is submitted
                    )

                    // Email Error Message
                    if (isSubmitted && email.isBlank()) {
                        Text("Please fill in the email", color = Color.Red, style = MaterialTheme.typography.bodySmall)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Password Field
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            val image = if (passwordVisible)
                                Icons.Default.Visibility
                            else Icons.Default.VisibilityOff

                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(imageVector = image, contentDescription = "Toggle Password Visibility")
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier.fillMaxWidth(),
                        isError = isSubmitted && password.isBlank() // Show error only if form is submitted
                    )

                    // Password Error Message
                    if (isSubmitted && password.isBlank()) {
                        Text("Please fill in the password", color = Color.Red, style = MaterialTheme.typography.bodySmall)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            isSubmitted = true // Mark form as submitted

                            // Check if email and password fields are filled
                            if (email.isBlank() || password.isBlank()) {
                                Toast.makeText(context, "Please fill the Email id & Password", Toast.LENGTH_SHORT).show()
                            } else {
                                val users = FileUtils.loadUsers(context)

                                // Check if email exists
                                val user = users.find { it.email.equals(email, ignoreCase = true) }

                                if (user == null) {
                                    // Email not found
                                    Toast.makeText(context, "This Humber Email id is not registered", Toast.LENGTH_SHORT).show()
                                } else {
                                    // Check if the password matches
                                    if (user.password == password) {
                                        // Password matched, sign in
                                        navController.navigate(Route.Home.routeName) {
                                            popUpTo(Route.SignIn.routeName) { inclusive = true }
                                        }
                                    } else {
                                        // Password mismatch
                                        Toast.makeText(context, "Wrong Password", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Submit")
                    }
                }
            }
        }
    }
}
