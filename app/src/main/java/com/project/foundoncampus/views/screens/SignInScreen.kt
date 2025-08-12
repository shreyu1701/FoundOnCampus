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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.project.foundoncampus.model.AppDatabase
import com.project.foundoncampus.model.UserEntity
import com.project.foundoncampus.nav.Route
import com.project.foundoncampus.util.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignInScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isSubmitted by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val db = AppDatabase.getInstance(context)
    val sessionManager = remember { SessionManager(context) }
    val scope = rememberCoroutineScope()

    val cs = MaterialTheme.colorScheme
    val ty = MaterialTheme.typography

    Scaffold { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                colors = CardDefaults.cardColors(containerColor = cs.surface)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Welcome back",
                        style = ty.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                        color = cs.onSurface
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "Sign in to continue",
                        style = ty.bodyMedium,
                        color = cs.onSurface.copy(alpha = 0.75f)
                    )

                    Spacer(Modifier.height(24.dp))

                    // Email
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        singleLine = true,
                        label = { Text("Humber Email") },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        isError = isSubmitted && email.isBlank(),
                        modifier = Modifier.fillMaxWidth(),
                        supportingText = {
                            if (isSubmitted && email.isBlank()) {
                                Text(
                                    "Please enter your email",
                                    style = ty.bodySmall,
                                    color = cs.error
                                )
                            }
                        }
                    )

                    Spacer(Modifier.height(12.dp))

                    // Password
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        singleLine = true,
                        label = { Text("Password") },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                    contentDescription = "Toggle Password Visibility"
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        isError = isSubmitted && password.isBlank(),
                        modifier = Modifier.fillMaxWidth(),
                        supportingText = {
                            if (isSubmitted && password.isBlank()) {
                                Text(
                                    "Password is required",
                                    style = ty.bodySmall,
                                    color = cs.error
                                )
                            }
                        }
                    )

                    Spacer(Modifier.height(20.dp))

                    Button(
                        onClick = {
                            isSubmitted = true
                            if (email.isBlank() || password.isBlank()) {
                                Toast.makeText(
                                    context,
                                    "Please fill the Email & Password",
                                    Toast.LENGTH_SHORT
                                ).show()
                                return@Button
                            }

                            scope.launch {
                                val user: UserEntity? = withContext(Dispatchers.IO) {
                                    db.userDao().login(email.trim(), password)
                                }

                                if (user == null) {
                                    Toast.makeText(
                                        context,
                                        "Invalid email or password",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    sessionManager.saveUserEmail(user.email)
                                    Toast.makeText(context, "Login successful", Toast.LENGTH_SHORT).show()
                                    navController.navigate(Route.Main.routeName) {
                                        popUpTo(Route.Auth.routeName) { inclusive = true }
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = cs.primary,
                            contentColor = cs.onPrimary
                        )
                    ) {
                        Text("Sign In", style = ty.labelLarge)
                    }

                    TextButton(
                        onClick = { navController.navigate(Route.SignUp.routeName) },
                        modifier = Modifier.padding(top = 6.dp),
                        colors = ButtonDefaults.textButtonColors(contentColor = cs.secondary)
                    ) {
                        Text("Don't have an account? Create one", style = ty.labelLarge)
                    }
                }
            }
        }
    }
}
