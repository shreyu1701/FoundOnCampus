package com.project.foundoncampus.views.screens

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
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
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CreateScreen(navController: NavController) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    val session = remember { SessionManager(context) }
    val scope = rememberCoroutineScope()
    val focus = androidx.compose.ui.platform.LocalFocusManager.current

    var selectedType by rememberSaveable { mutableStateOf("Lost") }
    var item by rememberSaveable { mutableStateOf("") }
    var category by rememberSaveable { mutableStateOf("") }
    var campus by rememberSaveable { mutableStateOf("") }
    var location by rememberSaveable { mutableStateOf("") }
    var date by rememberSaveable { mutableStateOf("") }
    var desc by rememberSaveable { mutableStateOf("") }
    var imageUrl by rememberSaveable { mutableStateOf("") }
    var submitted by rememberSaveable { mutableStateOf(false) }
    var sending by remember { mutableStateOf(false) }
    var showThanks by remember { mutableStateOf(false) } // <<< NEW

    val cal = Calendar.getInstance()
    val datePicker = DatePickerDialog(
        context,
        { _, y, m, d -> date = "%04d-%02d-%02d".format(y, m + 1, d) },
        cal.get(Calendar.YEAR),
        cal.get(Calendar.MONTH),
        cal.get(Calendar.DAY_OF_MONTH)
    ).apply {
        // Block future dates in the picker UI
        datePicker.maxDate = System.currentTimeMillis()
    }

    val cs = MaterialTheme.colorScheme
    val ty = MaterialTheme.typography

    val itemErr = submitted && item.isBlank()
    val catErr = submitted && category.isBlank()
    val campErr = submitted && campus.isBlank()
    val locErr  = submitted && location.isBlank()
    val dateEmptyErr = submitted && date.isBlank()
    val futureDate = remember(date) { isFutureDate(date) }
    val futureDateErr = submitted && date.isNotBlank() && futureDate

    val valid = item.isNotBlank() &&
            category.isNotBlank() &&
            campus.isNotBlank() &&
            location.isNotBlank() &&
            date.isNotBlank() &&
            !futureDate

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = cs.surface),
                elevation = CardDefaults.cardElevation(2.dp),
                shape = MaterialTheme.shapes.large
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Report Item", style = ty.titleLarge.copy(fontWeight = FontWeight.SemiBold))

                    // Type
                    LabeledRadioGroup(
                        label = "Report *",
                        selected = selectedType,
                        options = listOf("Lost", "Found"),
                        onChange = { selectedType = it }
                    )

                    // Category
                    CustomDropdown(
                        label = "Category *",
                        options = listOf("Electronics", "Clothing", "Books", "Accessories"),
                        selectedOption = category,
                        onOptionSelected = { category = it }
                    )
                    if (catErr) Text("Please choose a category", color = cs.error, style = ty.bodySmall)

                    // Campus
                    CustomDropdown(
                        label = "Campus *",
                        options = listOf("IGS Campus", "North Campus", "Lakeshore Campus"),
                        selectedOption = campus,
                        onOptionSelected = { campus = it }
                    )
                    if (campErr) Text("Please choose a campus", color = cs.error, style = ty.bodySmall)

                    // Location
                    CustomDropdown(
                        label = "Location *",
                        options = listOf("Library", "Cafeteria", "Auditorium", "Hostel"),
                        selectedOption = location,
                        onOptionSelected = { location = it }
                    )
                    if (locErr) Text("Please choose a location", color = cs.error, style = ty.bodySmall)

                    // Image (optional)
                    LabeledTextField(
                        label = "Item Image URL (optional)",
                        value = imageUrl,
                        onChange = { imageUrl = it },
                        ime = ImeAction.Next,
                        onNext = { focus.moveFocus(FocusDirection.Down) }
                    )

                    // Title
                    LabeledTextField(
                        label = "Item *",
                        value = item,
                        onChange = { item = it },
                        isError = itemErr,
                        errorText = "Item name is required",
                        ime = ImeAction.Next,
                        onNext = { focus.moveFocus(FocusDirection.Down) }
                    )

                    // Description
                    LabeledMultilineField(
                        label = "Description",
                        value = desc,
                        onChange = { desc = it }
                    )

                    // Date
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Date *", style = ty.labelLarge, modifier = Modifier.width(100.dp))
                        OutlinedTextField(
                            value = date,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = {
                                IconButton(onClick = { datePicker.show() }) {
                                    Icon(Icons.Filled.CalendarToday, contentDescription = null)
                                }
                            },
                            placeholder = { Text("Select Date") },
                            modifier = Modifier.fillMaxWidth(),
                            isError = dateEmptyErr || futureDateErr
                        )
                    }
                    if (dateEmptyErr) Text("Please pick a date", color = cs.error, style = ty.bodySmall)
                    if (futureDateErr) Text("Future dates are not allowed", color = cs.error, style = ty.bodySmall)

                    Button(
                        onClick = {
                            submitted = true
                            if (!valid) return@Button

                            sending = true
                            scope.launch {
                                val email = session.getUserEmail().first().orEmpty()

                                // Extra safety: validate again at submit time
                                if (isFutureDate(date)) {
                                    sending = false
                                    Toast.makeText(context, "Future dates are not allowed", Toast.LENGTH_SHORT).show()
                                    return@launch
                                }

                                val listing = ListingEntity(
                                    title = item,
                                    description = desc,
                                    category = category,
                                    type = selectedType,
                                    date = date,
                                    campus = campus,
                                    location = location,
                                    status = "Pending",
                                    contact = email,
                                    userEmail = email,
                                    imageUrl = imageUrl.ifBlank { null }
                                )
                                withContext(Dispatchers.IO) { db.listingDao().insertListing(listing) }

                                // Fire-and-forget email
                                withContext(Dispatchers.IO) {
                                    try {
                                        GmailSender(BuildConfig.EMAIL_USER, BuildConfig.EMAIL_PASS)
                                            .sendEmail(
                                                to = email,
                                                subject = "New Item Added",
                                                body = "A new item titled \"$item\" was reported as $selectedType on $date."
                                            )
                                    } catch (_: Exception) { /* ignore */ }
                                }

                                // Reset form and hide validation errors
                                item = ""
                                category = ""
                                campus = ""
                                location = ""
                                date = ""
                                desc = ""
                                selectedType = "Lost"
                                imageUrl = ""
                                submitted = false           // <<< important so errors don’t show
                                sending = false

                                // Show Thank You popup
                                showThanks = true

                                // Optional toast (keep if you still want it)
                                // Toast.makeText(context, "Listing submitted", Toast.LENGTH_SHORT).show()
                            }
                        },
                        enabled = valid && !sending,
                        modifier = Modifier.fillMaxWidth()
                    ) { Text(if (sending) "Submitting…" else "Submit") }
                }
            }
        }
    }

    // ---- Thank You Dialog ----
    if (showThanks) {
        AlertDialog(
            onDismissRequest = { showThanks = false },
            confirmButton = {
                TextButton(onClick = { showThanks = false }) {
                    Text("OK")
                }
            },
            title = { Text("Thank you!") },
            text = { Text("Your item has been submitted. We appreciate your contribution to the campus community.") }
        )
    }
}

// Strict check for future date based on yyyy-MM-dd
private fun isFutureDate(dateStr: String): Boolean {
    if (dateStr.isBlank()) return false
    return try {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply { isLenient = false }
        val picked = sdf.parse(dateStr) ?: return false
        // Normalize "today" to date-only (ignore time-of-day)
        val todayStr = sdf.format(Date())
        val today = sdf.parse(todayStr) ?: return false
        picked.after(today)
    } catch (_: Exception) {
        false
    }
}

/* ---------- Reusable bits ---------- */

@Composable
fun LabeledRadioGroup(label: String, selected: String, options: List<String>, onChange: (String) -> Unit) {
    val ty = MaterialTheme.typography
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(label, style = ty.labelLarge, modifier = Modifier.width(100.dp))
        options.forEachIndexed { i, option ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = selected == option, onClick = { onChange(option) })
                Text(option, style = ty.bodyMedium)
            }
            if (i < options.lastIndex) Spacer(Modifier.width(16.dp))
        }
    }
}

@Composable
fun LabeledTextField(
    label: String,
    value: String,
    onChange: (String) -> Unit,
    isError: Boolean = false,
    errorText: String? = null,
    ime: ImeAction = ImeAction.Next,
    onNext: () -> Unit = {}
) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        isError = isError,
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ime),
        keyboardActions = KeyboardActions(onNext = { onNext() }),
        supportingText = {
            if (isError && !errorText.isNullOrBlank()) Text(errorText, color = MaterialTheme.colorScheme.error)
        }
    )
}

@Composable
fun LabeledMultilineField(label: String, value: String, onChange: (String) -> Unit) {
    Row(verticalAlignment = Alignment.Top) {
        Text(label, style = MaterialTheme.typography.labelLarge, modifier = Modifier.width(100.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDropdown(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = { onOptionSelected(option); expanded = false }
                )
            }
        }
    }
}
