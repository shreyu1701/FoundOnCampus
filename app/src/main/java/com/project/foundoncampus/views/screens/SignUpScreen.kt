package com.project.foundoncampus.views.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.project.foundoncampus.BuildConfig
import com.project.foundoncampus.R
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
    val focus = androidx.compose.ui.platform.LocalFocusManager.current

    val auth = FirebaseAuth.getInstance()   // ✅ Firebase Auth reference

    var name by rememberSaveable { mutableStateOf("") }
    var humberId by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirm by rememberSaveable { mutableStateOf("") }
    var pwVisible by rememberSaveable { mutableStateOf(false) }
    var cpwVisible by rememberSaveable { mutableStateOf(false) }
    var submitted by rememberSaveable { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }

    // Auto-fill Humber email from ID
    LaunchedEffect(humberId) {
        if (humberId.matches(Regex("^[nN]\\d{1,8}$"))) {
            email = "${humberId.lowercase()}@humber.ca"
        }
    }

    val cs = MaterialTheme.colorScheme
    val ty = MaterialTheme.typography

    val nameErr = submitted && name.isBlank()
    val idErr = submitted && !humberId.matches(Regex("^[nN]\\d{8}$"))
    val phoneErr = submitted && phone.isNotBlank() && !phone.matches(Regex("^\\d{10}$"))
    val emailErr = submitted && !email.equals("${humberId}@humber.ca", ignoreCase = true)
    val pwErr =
        submitted && !password.matches(Regex("^(?=.*[A-Z])(?=.*\\d)(?=.*[@#\$%^&+=!]).{8,}$"))
    val cpwErr = submitted && confirm != password

    val valid = !nameErr && !idErr && !phoneErr && !emailErr && !pwErr && !cpwErr
            && name.isNotBlank() && humberId.isNotBlank() && email.isNotBlank() && password.isNotBlank() && confirm.isNotBlank()

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
                colors = CardDefaults.cardColors(containerColor = cs.surface),
                elevation = CardDefaults.cardElevation(2.dp),
                shape = MaterialTheme.shapes.large
            ) {

                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Image(
                        painter = painterResource(id = R.drawable.icon_foundoncampus),
                        contentDescription = "App Logo",
                        modifier = Modifier.size(100.dp)
                    )

                    Text("Create Account", style = ty.titleLarge)

                    // ---------- INPUT FIELDS (Unchanged UI) ----------
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Name *") },
                        isError = nameErr,
                        supportingText = { if (nameErr) Text("Name is required", color = cs.error) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = humberId,
                        onValueChange = { humberId = it.trim() },
                        label = { Text("Humber ID (n12345678) *") },
                        isError = idErr,
                        supportingText = { if (idErr) Text("Must start with n + 8 digits", color = cs.error) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it.filter(Char::isDigit).take(10) },
                        label = { Text("Phone number (optional)") },
                        isError = phoneErr,
                        supportingText = { if (phoneErr) Text("Must be 10 digits", color = cs.error) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it.trim() },
                        label = { Text("Humber Email *") },
                        isError = emailErr,
                        supportingText = { if (emailErr) Text("Email must be ${humberId.lowercase()}@humber.ca", color = cs.error) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password *") },
                        isError = pwErr,
                        supportingText = { if (pwErr) Text("Min 8 chars, 1 capital, 1 number, 1 special", color = cs.error) },
                        visualTransformation = if (pwVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { pwVisible = !pwVisible }) {
                                Icon(if (pwVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility, "")
                            }
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = confirm,
                        onValueChange = { confirm = it },
                        label = { Text("Confirm Password *") },
                        isError = cpwErr,
                        supportingText = { if (cpwErr) Text("Passwords do not match", color = cs.error) },
                        visualTransformation = if (cpwVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { cpwVisible = !cpwVisible }) {
                                Icon(if (cpwVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility, "")
                            }
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // ---------- SUBMIT BUTTON ----------
                    Button(
                        onClick = {
                            submitted = true
                            if (!valid) return@Button

                            loading = true

                            // ---------- 🔥 FIREBASE SIGNUP ----------
                            auth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener { task ->
                                    loading = false

                                    if (task.isSuccessful) {

                                        // ---------- EMAIL SENDING (same as before) ----------
                                        scope.launch {
                                            try {
                                                val sender = GmailSender(BuildConfig.EMAIL_USER, BuildConfig.EMAIL_PASS)
                                                withContext(Dispatchers.IO) {
                                                    sender.sendEmail(
                                                        to = email,
                                                        subject = "Welcome to FoundOnCampus!",
                                                        body = "Hi $name,\n\nThank you for registering at FoundOnCampus."
                                                    )
                                                }
                                            } catch (_: MessagingException) {}

                                            Toast.makeText(context, "Account created!", Toast.LENGTH_LONG).show()
                                            navController.navigate(Route.SignIn.routeName)
                                        }

                                    } else {
                                        Toast.makeText(
                                            context,
                                            task.exception?.localizedMessage ?: "Signup failed",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }
                        },
                        enabled = valid && !loading,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (loading) "Creating…" else "Submit")
                    }

                    Row {
                        Text("Already have an account?", style = ty.bodyMedium)
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
