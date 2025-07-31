package com.project.foundoncampus.views.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.project.foundoncampus.model.ListingEntity
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ListingDialog(
    item: ListingEntity?,
    userEmail: String,
    onDismiss: () -> Unit,
    onSave: (ListingEntity) -> Unit
) {
    val dateNow = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    var title by rememberSaveable { mutableStateOf(item?.title ?: "") }
    var description by rememberSaveable { mutableStateOf(item?.description ?: "") }
    var contact by rememberSaveable { mutableStateOf(item?.contact ?: "") }
    var type by rememberSaveable { mutableStateOf(item?.type ?: "Lost") }
    var category by rememberSaveable { mutableStateOf(item?.category ?: "") }
    var campus by rememberSaveable { mutableStateOf(item?.campus ?: "") }
    var location by rememberSaveable { mutableStateOf(item?.location ?: "") }
    var status by rememberSaveable { mutableStateOf(item?.status ?: "Pending") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (item == null) "Add Listing" else "Edit Listing") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = contact,
                    onValueChange = { contact = it },
                    label = { Text("Contact Info") },
                    modifier = Modifier.fillMaxWidth()
                )

                CustomDropdown(
                    label = "Type",
                    options = listOf("Lost", "Found"),
                    selectedOption = type,
                    onOptionSelected = { type = it }
                )

                CustomDropdown(
                    label = "Category",
                    options = listOf("Electronics", "Clothing", "Books", "Accessories"),
                    selectedOption = category,
                    onOptionSelected = { category = it }
                )

                CustomDropdown(
                    label = "Campus",
                    options = listOf("IGS Campus", "North Campus", "Lakeshore Campus"),
                    selectedOption = campus,
                    onOptionSelected = { campus = it }
                )

                CustomDropdown(
                    label = "Location",
                    options = listOf("Library", "Cafeteria", "Auditorium", "Hostel"),
                    selectedOption = location,
                    onOptionSelected = { location = it }
                )

                CustomDropdown(
                    label = "Status",
                    options = listOf("Pending", "Resolved", "Claimed"),
                    selectedOption = status,
                    onOptionSelected = { status = it }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (title.isNotBlank() && description.isNotBlank()) {
                    val updatedListing = item?.copy(
                        title = title,
                        description = description,
                        contact = contact,
                        type = type,
                        category = category,
                        campus = campus,
                        location = location,
                        status = status
                    ) ?: ListingEntity(
                        id = 0,
                        title = title,
                        description = description,
                        userEmail = userEmail,
                        type = type,
                        category = category,
                        campus = campus,
                        location = location,
                        date = dateNow,
                        status = status,
                        contact = contact
                    )
                    onSave(updatedListing)
                }
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        modifier = Modifier.fillMaxWidth()
    )
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

