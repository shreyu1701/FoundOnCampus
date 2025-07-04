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
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.project.foundoncampus.model.ListingItem
import com.project.foundoncampus.viewmodels.CreateViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateScreen(navController: NavController, viewModel: CreateViewModel = viewModel()) {
    val context = LocalContext.current

    // Form state
    var selectedType by remember { mutableStateOf("Lost") }
    var item by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var campus by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var itemDescription by remember { mutableStateOf("") }
    var uploadPhotoUri by remember { mutableStateOf("") }

    // Options
    val categoryOptions = listOf("Electronics", "Clothing", "Books", "Accessories")
    val campusOptions = listOf("IGS Campus", "North Campus", "Lakeshore Campus")
    val locationOptions = listOf("Library", "Cafeteria", "Auditorium", "Hostel")

    var categoryExpanded by remember { mutableStateOf(false) }
    var campusExpanded by remember { mutableStateOf(false) }
    var locationExpanded by remember { mutableStateOf(false) }

    val calendar = java.util.Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, day -> date = "$year-${month + 1}-$day" },
        calendar.get(java.util.Calendar.YEAR),
        calendar.get(java.util.Calendar.MONTH),
        calendar.get(java.util.Calendar.DAY_OF_MONTH)
    )

    val topBarTitle = if (selectedType == "Lost") {
        "Hey,User\nLost something? We're here to help!"
    } else {
        "Hey,User\nFound something? Thanks for reporting!"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(topBarTitle, lineHeight = 16.sp, fontSize = 16.sp) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Blue,
                    titleContentColor = Color.White
                ),
                navigationIcon = {
                    IconButton(onClick = { /* profile */ }) {
                        Icon(Icons.Default.Person, contentDescription = "Profile")
                    }
                },
                actions = {
                    IconButton(onClick = { /* settings */ }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        }
    ) { padding ->
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
                        .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Report Item",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        ),
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Report:", fontWeight = FontWeight.Bold, modifier = Modifier.width(100.dp))
                        RadioButton(selected = selectedType == "Lost", onClick = { selectedType = "Lost" })
                        Text("Lost", Modifier.padding(end = 16.dp))
                        RadioButton(selected = selectedType == "Found", onClick = { selectedType = "Found" })
                        Text("Found")
                    }

                    CustomField(label = "Item:", value = item) { item = it }

                    CustomDropdown(
                        label = "Category:",
                        options = categoryOptions,
                        selected = category,
                        expanded = categoryExpanded,
                        onExpand = { categoryExpanded = it },
                        onSelected = { category = it }
                    )

                    CustomDropdown(
                        label = "Campus:",
                        options = campusOptions,
                        selected = campus,
                        expanded = campusExpanded,
                        onExpand = { campusExpanded = it },
                        onSelected = { campus = it }
                    )

                    CustomDropdown(
                        label = "Location:",
                        options = locationOptions,
                        selected = location,
                        expanded = locationExpanded,
                        onExpand = { locationExpanded = it },
                        onSelected = { location = it }
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Date:", fontWeight = FontWeight.Bold, modifier = Modifier.width(100.dp))
                        OutlinedTextField(
                            value = date,
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
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

                    CustomField(label = "Photo:", value = uploadPhotoUri, trailingIcon = {
                        Icon(Icons.Default.Upload, contentDescription = "Upload")
                    }) { uploadPhotoUri = it }

                    Button(
                        onClick = {
                            if (item.isNotBlank()) {
                                val listing = ListingItem(
                                    type = selectedType,
                                    item = item,
                                    category = category,
                                    campus = campus,
                                    location = location,
                                    date = date,
                                    itemdescription = itemDescription,
                                    uploadPhotoUri = uploadPhotoUri
                                )
                                viewModel.saveListing(listing)
                                Toast.makeText(context, "Listing saved!", Toast.LENGTH_SHORT).show()
                                item = ""
                                category = ""
                                campus = ""
                                location = ""
                                date = ""
                                itemDescription = ""
                                uploadPhotoUri = ""
                                selectedType = "Lost"
                            } else {
                                Toast.makeText(context, "Please fill in the item name.", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Submit")
                    }
                }
            }
        }
    }
}

@Composable
fun CustomField(
    label: String,
    value: String,
    trailingIcon: @Composable (() -> Unit)? = null,
    onValueChange: (String) -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(label, fontWeight = FontWeight.Bold, modifier = Modifier.width(100.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            trailingIcon = trailingIcon
        )
    }
}

@Composable
fun CustomDropdown(
    label: String,
    options: List<String>,
    selected: String,
    expanded: Boolean,
    onExpand: (Boolean) -> Unit,
    onSelected: (String) -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(label, fontWeight = FontWeight.Bold, modifier = Modifier.width(100.dp))
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { onExpand(!expanded) }
        ) {
            OutlinedTextField(
                value = selected,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
                    .height(56.dp),
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                placeholder = { Text("Select") }
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { onExpand(false) }
            ) {
                options.forEach {
                    DropdownMenuItem(
                        text = { Text(it) },
                        onClick = {
                            onSelected(it)
                            onExpand(false)
                        }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CreateScreenPreview() {
    val navController = rememberNavController()
    CreateScreen(navController = navController)
}
