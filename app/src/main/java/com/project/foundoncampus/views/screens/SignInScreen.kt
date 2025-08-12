package com.project.foundoncampus.views.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.project.foundoncampus.R
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
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
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    val session = remember { SessionManager(context) }
    val scope = rememberCoroutineScope()
    val focus = LocalFocusManager.current

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var submitted by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }

    val cs = MaterialTheme.colorScheme
    val ty = MaterialTheme.typography

    val emailError = submitted && email.isBlank()
    val passError = submitted && password.isBlank()
    val isValid = email.isNotBlank() && password.isNotBlank()

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
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.icon_foundoncampus),
                        contentDescription = "App Logo",
                        modifier = Modifier
                            .size(100.dp)
                            .padding(bottom = 16.dp)
                    )
                    Text(
                        "Welcome back",
                        style = ty.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                        color = cs.onSurface
                    )
                    Spacer(Modifier.height(6.dp))
                    Text("Sign in to continue", style = ty.bodyMedium, color = cs.onSurface.copy(.75f))

                    Spacer(Modifier.height(24.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Humber Email *") },
                        singleLine = true,
                        isError = emailError,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focus.moveFocus(FocusDirection.Down) }
                        ),
                        supportingText = {
                            if (emailError) Text("Please enter your email", color = cs.error)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password *") },
                        singleLine = true,
                        isError = passError,
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                    contentDescription = null
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { focus.clearFocus() }
                        ),
                        supportingText = {
                            if (passError) Text("Password is required", color = cs.error)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(20.dp))

                    Button(
                        onClick = {
                            submitted = true
                            if (!isValid) return@Button

                            loading = true
                            scope.launch {
                                val user: UserEntity? = withContext(Dispatchers.IO) {
                                    db.userDao().login(email.trim(), password)
                                }
                                loading = false
                                if (user == null) {
                                    Toast.makeText(context, "Invalid email or password", Toast.LENGTH_SHORT).show()
                                } else {
                                    session.saveUserEmail(user.email)
                                    Toast.makeText(context, "Login successful", Toast.LENGTH_SHORT).show()
                                    navController.navigate(Route.Main.routeName) {
                                        popUpTo(Route.Auth.routeName) { inclusive = true }
                                    }
                                }
                            }
                        },
                        enabled = isValid && !loading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text(if (loading) "Signing in…" else "Sign In", style = ty.labelLarge)
                    }

                    TextButton(
                        onClick = { navController.navigate(Route.SignUp.routeName) },
                        modifier = Modifier.padding(top = 6.dp)
                    ) { Text("Don't have an account? Create one", style = ty.labelLarge) }
                }
            }
        }
    }
}
