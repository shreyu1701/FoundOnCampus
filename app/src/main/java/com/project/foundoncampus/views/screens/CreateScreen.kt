@file:OptIn(ExperimentalMaterial3Api::class)

package com.project.foundoncampus.views.screens

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.project.foundoncampus.model.AppDatabase
import com.project.foundoncampus.model.ListingEntity
import com.project.foundoncampus.util.GmailSender
import com.project.foundoncampus.util.SessionManager
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun CreateScreen(navController: NavController) {
    val context = LocalContext.current
    val db = AppDatabase.getInstance(context)
    val scope = rememberCoroutineScope()
    val sessionManager = remember { SessionManager(context) }

    var selectedType by remember { mutableStateOf("Lost") }
    var item by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var campus by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var itemDescription by remember { mutableStateOf("") }

    val categoryOptions = listOf("Electronics", "Clothing", "Books", "Accessories")
    val campusOptions = listOf("IGS Campus", "North Campus", "Lakeshore Campus")
    val locationOptions = listOf("Library", "Cafeteria", "Auditorium", "Hostel")

    val calendar = java.util.Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, day -> date = "$year-${month + 1}-$day" },
        calendar.get(java.util.Calendar.YEAR),
        calendar.get(java.util.Calendar.MONTH),
        calendar.get(java.util.Calendar.DAY_OF_MONTH)
    )

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Report Item", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Report:", fontWeight = FontWeight.Bold, modifier = Modifier.width(100.dp))
                        RadioButton(selected = selectedType == "Lost", onClick = { selectedType = "Lost" })
                        Text("Lost", Modifier.padding(end = 16.dp))
                        RadioButton(selected = selectedType == "Found", onClick = { selectedType = "Found" })
                        Text("Found")
                    }

                    CustomDropdown(label = "Category", options = categoryOptions, selectedOption = category) {
                        category = it
                    }

                    CustomDropdown(label = "Campus", options = campusOptions, selectedOption = campus) {
                        campus = it
                    }

                    CustomDropdown(label = "Location", options = locationOptions, selectedOption = location) {
                        location = it
                    }

                    OutlinedTextField(
                        value = item,
                        onValueChange = { item = it },
                        label = { Text("Item") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Date:", fontWeight = FontWeight.Bold, modifier = Modifier.width(100.dp))
                        OutlinedTextField(
                            value = date,
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier.fillMaxWidth(),
                            trailingIcon = {
                                IconButton(onClick = { datePickerDialog.show() }) {
                                    Icon(Icons.Default.CalendarToday, contentDescription = "Pick Date")
                                }
                            },
                            placeholder = { Text("Select Date") }
                        )
                    }

                    Row(verticalAlignment = Alignment.Top) {
                        Text("Description:", fontWeight = FontWeight.Bold, modifier = Modifier.width(100.dp))
                        OutlinedTextField(
                            value = itemDescription,
                            onValueChange = { itemDescription = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                        )
                    }

                    var isSending by remember { mutableStateOf(false) }

                    Button(
                        onClick = {
                            if (item.isNotBlank() && !isSending) {
                                isSending = true
                                scope.launch {
                                    val email = sessionManager.getUserEmail().first() ?: ""

                                    val listing = ListingEntity(
                                        title = item,
                                        description = itemDescription,
                                        category = category,
                                        type = selectedType,
                                        date = date,
                                        status = "Pending",
                                        contact = email,
                                        userEmail = email
                                    )

                                    db.listingDao().insertListing(listing)

                                    withContext(Dispatchers.IO) {
                                        val senderEmail = "lostandfoundhumber@gmail.com"
                                        val appPassword = "fzvn onds jnnf idt"
                                        val subject = "New Item Added"
                                        val body = "A new item titled \"$item\" was reported as $selectedType on $date."

                                        val success = GmailSender(senderEmail, appPassword).sendEmail(
                                            to = email,
                                            subject = subject,
                                            body = body
                                        )

                                        withContext(Dispatchers.Main) {
                                            isSending = false
                                            if (success) {
                                                Toast.makeText(context, "Listing saved & Email sent!", Toast.LENGTH_SHORT).show()
                                            } else {
                                                Toast.makeText(context, "Listing saved, but email failed!", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    }

                                    item = ""
                                    category = ""
                                    campus = ""
                                    location = ""
                                    date = ""
                                    itemDescription = ""
                                    selectedType = "Lost"
                                }
                            } else if (item.isBlank()) {
                                Toast.makeText(context, "Please fill in the item name.", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isSending
                    ) {
                        Text(if (isSending) "Submitting..." else "Submit")
                    }

                }
            }
        }
    }
}

@Composable
fun CustomDropdown(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
