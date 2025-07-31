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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
                    LabeledTextField("Humber ID:", humberId, { humberId = it }, humberIdError, "Humber ID must start with n or N and have 8 digits")
                    LabeledTextField("Phone Number:", phoneNumber, { phoneNumber = it }, phoneNumberError, "Phone number must be exactly 10 digits")
                    LabeledTextField("Humber Email ID:", email, { email = it }, emailError, "Email must be your Humber ID + @humber.ca")

                    PasswordField(
                        label = "Password:",
                        value = password,
                        visible = passwordVisible,
                        onValueChange = { password = it },
                        onVisibilityToggle = { passwordVisible = !passwordVisible },
                        isError = passwordError,
                        errorMessage = "Password must have 1 capital, 1 special, 1 number, min 8 chars"
                    )

                    PasswordField(
                        label = "Confirm Password:",
                        value = confirmPassword,
                        visible = confirmPasswordVisible,
                        onValueChange = { confirmPassword = it },
                        onVisibilityToggle = { confirmPasswordVisible = !confirmPasswordVisible },
                        isError = confirmPasswordError,
                        errorMessage = "Passwords do not match"
                    )

                    Button(
                        onClick = {
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
                                                val sent = sender.sendEmail(
                                                    to = email,
                                                    subject = "Welcome to FoundOnCampus!",
                                                    body = """
                                    Hi $name,

                                    Thank you for registering at FoundOnCampus.

                                    You can now sign in using your Humber ID.

                                    — FoundOnCampus Team
                                """.trimIndent()
                                                )

                                                withContext(Dispatchers.Main) {
                                                    if (sent) {
                                                        Toast.makeText(context, "Registered", Toast.LENGTH_SHORT).show()
                                                    }
                                                }
                                            }
                                        } catch (e: MessagingException) {
                                            println("Email sending failed: ${e.message}")
                                            e.printStackTrace()
                                        }

                                        navController.navigate(Route.SignIn.routeName)
                                    }
                                }
                            } else {
                                Toast.makeText(context, "Please fill the data correctly", Toast.LENGTH_LONG).show()
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
