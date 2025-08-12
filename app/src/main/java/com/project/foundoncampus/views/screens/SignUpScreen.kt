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
import com.project.foundoncampus.BuildConfig
import com.project.foundoncampus.R
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
    val db = remember { AppDatabase.getInstance(context) }
    val scope = rememberCoroutineScope()
    val focus = androidx.compose.ui.platform.LocalFocusManager.current

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

    // Auto-suggest email from Humber ID (still editable)
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
    val pwErr = submitted && !password.matches(Regex("^(?=.*[A-Z])(?=.*\\d)(?=.*[@#\$%^&+=!]).{8,}$"))
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
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.icon_foundoncampus),
                        contentDescription = "App Logo",
                        modifier = Modifier
                            .size(100.dp)
                            .padding(bottom = 16.dp)
                    )
                    Text("Create Account", style = ty.titleLarge, color = cs.onSurface, modifier = Modifier.align(Alignment.CenterHorizontally))

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Name *") },
                        singleLine = true,
                        isError = nameErr,
                        supportingText = { if (nameErr) Text("Name is required", color = cs.error) },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focus.moveFocus(FocusDirection.Down) }),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = humberId,
                        onValueChange = { humberId = it.trim() },
                        label = { Text("Humber ID (n12345678) *") },
                        singleLine = true,
                        isError = idErr,
                        supportingText = { if (idErr) Text("Must start with n/N + 8 digits", color = cs.error) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii, imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focus.moveFocus(FocusDirection.Down) }),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it.filter(Char::isDigit).take(10) },
                        label = { Text("Phone number (optional)") },
                        singleLine = true,
                        isError = phoneErr,
                        supportingText = { if (phoneErr) Text("Must be 10 digits", color = cs.error) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focus.moveFocus(FocusDirection.Down) }),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it.trim() },
                        label = { Text("Humber Email *") },
                        singleLine = true,
                        isError = emailErr,
                        supportingText = { if (emailErr) Text("Email must be ${humberId.lowercase()}@humber.ca", color = cs.error) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focus.moveFocus(FocusDirection.Down) }),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password *") },
                        singleLine = true,
                        isError = pwErr,
                        supportingText = {
                            if (pwErr) Text("Min 8 chars, 1 capital, 1 number, 1 special", color = cs.error)
                        },
                        visualTransformation = if (pwVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { pwVisible = !pwVisible }) {
                                Icon(if (pwVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility, contentDescription = null)
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focus.moveFocus(FocusDirection.Down) }),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = confirm,
                        onValueChange = { confirm = it },
                        label = { Text("Confirm Password *") },
                        singleLine = true,
                        isError = cpwErr,
                        supportingText = { if (cpwErr) Text("Passwords do not match", color = cs.error) },
                        visualTransformation = if (cpwVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { cpwVisible = !cpwVisible }) {
                                Icon(if (cpwVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility, contentDescription = null)
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { focus.clearFocus() }),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = {
                            submitted = true
                            if (!valid) return@Button

                            loading = true
                            scope.launch {
                                val existing = db.userDao().getUserByEmail(email)
                                if (existing != null) {
                                    loading = false
                                    Toast.makeText(context, "This email is already registered", Toast.LENGTH_LONG).show()
                                } else {
                                    val newUser = UserEntity(
                                        email = email,
                                        name = name,
                                        password = password,
                                        phone = phone.takeIf { it.isNotBlank() }
                                    )
                                    db.userDao().insertUser(newUser)

                                    try {
                                        val sender = GmailSender(BuildConfig.EMAIL_USER, BuildConfig.EMAIL_PASS)
                                        withContext(Dispatchers.IO) {
                                            sender.sendEmail(
                                                to = email,
                                                subject = "Welcome to FoundOnCampus!",
                                                body = "Hi $name,\n\nThank you for registering at FoundOnCampus."
                                            )
                                        }
                                    } catch (_: MessagingException) { /* ignore email issues */ }

                                    loading = false
                                    navController.navigate(Route.SignIn.routeName)
                                }
                            }
                        },
                        enabled = valid && !loading,
                        modifier = Modifier.fillMaxWidth()
                    ) { Text(if (loading) "Creating…" else "Submit") }

                    Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                        Text("Already have an account?", style = ty.bodyMedium)
                        Spacer(Modifier.width(6.dp))
                        Text(
                            "Sign in",
                            style = ty.labelLarge,
                            color = cs.secondary,
                            modifier = Modifier.clickable { navController.navigate(Route.SignIn.routeName) }
                        )
                    }
                }
            }
        }
    }
}
