package com.project.foundoncampus.views.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.project.foundoncampus.model.User
import com.project.foundoncampus.nav.Route
import com.project.foundoncampus.utils.FileUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(navController: NavController) {
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var humberId by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    var nameError by remember { mutableStateOf(false) }
    var humberIdError by remember { mutableStateOf(false) }
    var phoneNumberError by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }
    var confirmPasswordError by remember { mutableStateOf(false) }

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
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Sign Up", fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.CenterHorizontally))

                    LabeledTextField("Name:", name, { name = it }, nameError, "Name is required")
                    LabeledTextField("Humber ID:", humberId, { humberId = it }, humberIdError,"Humber ID must start with n or N and have 8 digits")
                    LabeledTextField("Phone Number:", phoneNumber, { phoneNumber = it }, phoneNumberError,"Phone number must be exactly 10 digits" )
                    LabeledTextField("Humber Email ID:", email, { email = it }, emailError, "Email must be your Humber ID + @humber.ca")

                    PasswordField(
                        label = "Password:",
                        value = password,
                        visible = passwordVisible,
                        onValueChange = { password = it },
                        onVisibilityToggle = { passwordVisible = !passwordVisible },
                        isError = passwordError,
                        "Password must have 1 capital, 1 special, 1 number, min 8 chars"
                    )

                    PasswordField(
                        label = "Confirm Password:",
                        value = confirmPassword,
                        visible = confirmPasswordVisible,
                        onValueChange = { confirmPassword = it },
                        onVisibilityToggle = { confirmPasswordVisible = !confirmPasswordVisible },
                        isError = confirmPasswordError,
                        "Passwords do not match"
                    )

                    Button(
                        onClick = {
                            // Reset error states
                            nameError = name.isBlank()
                            humberIdError = !humberId.matches(Regex("^[nN]\\d{8}$"))
                            phoneNumberError = !phoneNumber.matches(Regex("^\\d{10}$"))
                            emailError = !email.equals("${humberId}@humber.ca", ignoreCase = true)
                            passwordError = !password.matches(Regex("^(?=.*[A-Z])(?=.*\\d)(?=.*[@#\$%^&+=!]).{8,}$"))
                            confirmPasswordError = password != confirmPassword

                            if (!nameError && !humberIdError && !phoneNumberError &&
                                !emailError && !passwordError && !confirmPasswordError
                            ) {
                                val existingUsers = FileUtils.loadUsers(context)
                                val alreadyExists = existingUsers.any {
                                    it.email.equals(email, ignoreCase = true)
                                }

                                if (alreadyExists) {
                                    Toast.makeText(context, "This email is already registered", Toast.LENGTH_LONG).show()
                                } else {
                                    val newUser = User(name, humberId, phoneNumber, email, password)
                                    FileUtils.saveUser(context, newUser)
                                    Toast.makeText(context, "Registered successfully!", Toast.LENGTH_SHORT).show()
                                    navController.navigate(Route.SignIn.routeName)
                                }
                            } else {
                                Toast.makeText(context, "Please fill the data", Toast.LENGTH_LONG).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Submit")
                    }

                    Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                        Text("Already have an account?")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "Sign in",
                            color = Color.Blue,
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

@Composable
fun LabeledTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean,
    errorMessage: String
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                label,
                modifier = Modifier.width(130.dp),
                fontWeight = FontWeight.Bold
            )
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                isError = isError,
                singleLine = true
            )
        }
        if (isError) {
            Text(
                text = errorMessage,
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 130.dp)
            )
        }
    }
}

@Composable
fun PasswordField(
    label: String,
    value: String,
    visible: Boolean,
    onValueChange: (String) -> Unit,
    onVisibilityToggle: () -> Unit,
    isError: Boolean,
    errorMessage: String
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                label,
                modifier = Modifier.width(130.dp),
                fontWeight = FontWeight.Bold
            )
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = onVisibilityToggle) {
                        Icon(
                            imageVector = if (visible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = "Toggle Password Visibility"
                        )
                    }
                },
                isError = isError,
                singleLine = true
            )
        }
        if (isError) {
            Text(
                text = errorMessage,
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 130.dp)
            )
        }
    }
}
