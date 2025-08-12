package com.project.foundoncampus.views.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.project.foundoncampus.BuildConfig
import com.project.foundoncampus.model.AppDatabase
import com.project.foundoncampus.model.UserEntity
import com.project.foundoncampus.nav.Route
import com.project.foundoncampus.util.GmailSender
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.mail.MessagingException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val db = AppDatabase.getInstance(context)

    // Inputs
    var name by rememberSaveable { mutableStateOf("") }
    var humberId by rememberSaveable { mutableStateOf("") }
    var phoneNumber by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var confirmPasswordVisible by rememberSaveable { mutableStateOf(false) }

    // Errors
    var nameError by rememberSaveable { mutableStateOf(false) }
    var humberIdError by rememberSaveable { mutableStateOf(false) }
    var phoneNumberError by rememberSaveable { mutableStateOf(false) }
    var emailError by rememberSaveable { mutableStateOf(false) }
    var passwordError by rememberSaveable { mutableStateOf(false) }
    var confirmPasswordError by rememberSaveable { mutableStateOf(false) }

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
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = cs.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Create Account",
                        style = ty.titleLarge,
                        color = cs.onSurface,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                    // Name
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it; nameError = false },
                        label = { Text("Name") },
                        isError = nameError,
                        supportingText = {
                            if (nameError) Text("Name is required", color = cs.error, style = ty.bodySmall)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    // Humber ID
                    OutlinedTextField(
                        value = humberId,
                        onValueChange = {
                            humberId = it
                            humberIdError = false
                        },
                        label = { Text("Humber ID (e.g., n12345678)") },
                        isError = humberIdError,
                        supportingText = {
                            if (humberIdError) Text("Humber ID must start with n/N + 8 digits", color = cs.error, style = ty.bodySmall)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    // Phone Number
                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = {
                            phoneNumber = it
                            phoneNumberError = false
                        },
                        label = { Text("Phone number (10 digits)") },
                        isError = phoneNumberError,
                        supportingText = {
                            if (phoneNumberError) Text("Phone number must be 10 digits", color = cs.error, style = ty.bodySmall)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    // Humber Email
                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            emailError = false
                        },
                        label = { Text("Humber email (yourID@humber.ca)") },
                        isError = emailError,
                        supportingText = {
                            if (emailError) Text("Email must be your Humber ID + @humber.ca", color = cs.error, style = ty.bodySmall)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    // Password
                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            passwordError = false
                        },
                        label = { Text("Password") },
                        isError = passwordError,
                        supportingText = {
                            if (passwordError) Text("Must have 1 capital, 1 special, 1 number, min 8 chars", color = cs.error, style = ty.bodySmall)
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                    contentDescription = "Toggle Password Visibility",
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    // Confirm Password
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = {
                            confirmPassword = it
                            confirmPasswordError = false
                        },
                        label = { Text("Confirm password") },
                        isError = confirmPasswordError,
                        supportingText = {
                            if (confirmPasswordError) Text("Passwords do not match", color = cs.error, style = ty.bodySmall)
                        },
                        visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                Icon(
                                    imageVector = if (confirmPasswordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                    contentDescription = "Toggle Password Visibility",
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Button(
                        onClick = {
                            // Validate
                            nameError = name.isBlank()
                            humberIdError = !humberId.matches(Regex("^[nN]\\d{8}$"))
                            phoneNumberError = phoneNumber.isNotBlank() && !phoneNumber.matches(Regex("^\\d{10}$"))
                            emailError = !email.equals("${humberId}@humber.ca", ignoreCase = true)
                            passwordError = !password.matches(Regex("^(?=.*[A-Z])(?=.*\\d)(?=.*[@#\$%^&+=!]).{8,}$"))
                            confirmPasswordError = password != confirmPassword

                            val hasError = nameError || humberIdError || phoneNumberError || emailError || passwordError || confirmPasswordError

                            if (!hasError) {
                                scope.launch {
                                    val existing = db.userDao().getUserByEmail(email)
                                    if (existing != null) {
                                        Toast.makeText(context, "This email is already registered", Toast.LENGTH_LONG).show()
                                    } else {
                                        val newUser = UserEntity(
                                            email = email,
                                            name = name,
                                            password = password,
                                            phone = phoneNumber.takeIf { it.isNotBlank() }
                                        )
                                        db.userDao().insertUser(newUser)

                                        try {
                                            val sender = GmailSender(
                                                user = BuildConfig.EMAIL_USER,
                                                password = BuildConfig.EMAIL_PASS
                                            )
                                            withContext(Dispatchers.IO) {
                                                sender.sendEmail(
                                                    to = email,
                                                    subject = "Welcome to FoundOnCampus!",
                                                    body = "Hi $name,\n\nThank you for registering at FoundOnCampus."
                                                )
                                            }
                                        } catch (_: MessagingException) {
                                            // ignore email issues in UI flow
                                        }

                                        navController.navigate(Route.SignIn.routeName)
                                    }
                                }
                            } else {
                                Toast.makeText(context, "Please fill the data correctly", Toast.LENGTH_LONG).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = cs.primary,
                            contentColor = cs.onPrimary
                        )
                    ) {
                        Text("Submit", style = ty.labelLarge)
                    }

                    Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                        Text("Already have an account?", style = ty.bodyMedium, color = cs.onSurface)
                        Spacer(Modifier.width(6.dp))
                        Text(
                            "Sign in",
                            style = ty.labelLarge,
                            color = cs.secondary,
                            modifier = Modifier.clickable {
                                navController.navigate(Route.SignIn.routeName)
                            }
                        )
                    }
                }
            }
        }
    }
}
