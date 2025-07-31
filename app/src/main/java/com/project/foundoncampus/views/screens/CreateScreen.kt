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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.project.foundoncampus.BuildConfig
import com.project.foundoncampus.model.AppDatabase
import com.project.foundoncampus.model.ListingEntity
import com.project.foundoncampus.util.GmailSender
import com.project.foundoncampus.util.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

@Composable
fun CreateScreen(navController: NavController) {
    val context = LocalContext.current
    val db = AppDatabase.getInstance(context)
    val scope = rememberCoroutineScope()
    val sessionManager = remember { SessionManager(context) }

    var selectedType by rememberSaveable { mutableStateOf("Lost") }
    var item by rememberSaveable { mutableStateOf("") }
    var category by rememberSaveable { mutableStateOf("") }
    var campus by rememberSaveable { mutableStateOf("") }
    var location by rememberSaveable { mutableStateOf("") }
    var date by rememberSaveable { mutableStateOf("") }
    var itemDescription by rememberSaveable { mutableStateOf("") }
    var imageUrl by rememberSaveable { mutableStateOf("") }
    var isSending by remember { mutableStateOf(false) }

    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, day -> date = "$year-${month + 1}-$day" },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
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
                    .padding(16.dp),
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

                    LabeledRadioGroup("Report", selectedType, listOf("Lost", "Found")) {
                        selectedType = it
                    }

                    CustomDropdown("Category", listOf("Electronics", "Clothing", "Books", "Accessories"), category) {
                        category = it
                    }

                    CustomDropdown("Campus", listOf("IGS Campus", "North Campus", "Lakeshore Campus"), campus) {
                        campus = it
                    }

                    CustomDropdown("Location", listOf("Library", "Cafeteria", "Auditorium", "Hostel"), location) {
                        location = it
                    }

                    LabeledTextField("Item Image URL (optional)", imageUrl) { imageUrl = it }

                    LabeledTextField("Item", item) { item = it }

                    LabeledMultilineField("Description", itemDescription) { itemDescription = it }

                    LabeledDateField("Date", date, onPickDate = { datePickerDialog.show() })


                    Button(
                        onClick = {
                            if (item.isBlank()) {
                                Toast.makeText(context, "Please fill in the item name.", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            isSending = true
                            scope.launch {
                                val email = sessionManager.getUserEmail().first().orEmpty()
                                val listing = ListingEntity(
                                    title = item,
                                    description = itemDescription,
                                    category = category,
                                    type = selectedType,
                                    date = date,
                                    campus = campus,
                                    location = location,
                                    status = "Pending",
                                    contact = email,
                                    userEmail = email,
                                    imageUrl = imageUrl
                                )

                                db.listingDao().insertListing(listing)
                                sendEmailAsync(context, email, item, selectedType, date)

                                // Reset form
                                listOf(
                                    { item = "" }, { category = "" }, { campus = "" }, { location = "" },
                                    { date = "" }, { itemDescription = "" }, { selectedType = "Lost" }, { imageUrl = "" }
                                ).forEach { it() }

                                isSending = false
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

private suspend fun sendEmailAsync(context: android.content.Context, email: String, item: String, type: String, date: String) {
    withContext(Dispatchers.IO) {
        try {
            val success = GmailSender(BuildConfig.EMAIL_USER, BuildConfig.EMAIL_PASS)
                .sendEmail(
                    to = email,
                    subject = "New Item Added",
                    body = "A new item titled \"$item\" was reported as $type on $date."
                )
            withContext(Dispatchers.Main) {
                val message = if (success) "Listing saved & Email sent!" else "Listing saved, but email failed!"
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Listing saved, but email crashed!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

// ----- Reusable UI Components -----

@Composable
fun LabeledRadioGroup(label: String, selected: String, options: List<String>, onChange: (String) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("$label:", fontWeight = FontWeight.Bold, modifier = Modifier.width(100.dp))
        options.forEachIndexed { index, option ->
            RadioButton(selected = selected == option, onClick = { onChange(option) })
            Text(option, modifier = Modifier.padding(end = if (index < options.lastIndex) 16.dp else 0.dp))
        }
    }
}

@Composable
fun LabeledTextField(label: String, value: String, onChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun LabeledDateField(label: String, date: String, onPickDate: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("$label:", fontWeight = FontWeight.Bold, modifier = Modifier.width(100.dp))
        OutlinedTextField(
            value = date,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                IconButton(onClick = onPickDate) {
                    Icon(Icons.Default.CalendarToday, contentDescription = "Pick Date")
                }
            },
            placeholder = { Text("Select Date") }
        )
    }
}

@Composable
fun LabeledMultilineField(label: String, value: String, onChange: (String) -> Unit) {
    Row(verticalAlignment = Alignment.Top) {
        Text("$label:", fontWeight = FontWeight.Bold, modifier = Modifier.width(100.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
        )
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
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor() // Needed for ExposedDropdownMenuBox
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
