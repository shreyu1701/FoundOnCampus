package com.project.foundoncampus.views.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.project.foundoncampus.model.AppDatabase
import com.project.foundoncampus.model.ListingEntity
import com.project.foundoncampus.views.theme.FreshGreen
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(navController: NavController) {
    val context = LocalContext.current
    val db = AppDatabase.getInstance(context)
    val scope = rememberCoroutineScope()

    var listings by remember { mutableStateOf<List<ListingEntity>>(emptyList()) }
    var selectedFilter by remember { mutableStateOf("All") }
    var selectedSort by remember { mutableStateOf("Newest") }
    var selectedItem by remember { mutableStateOf<ListingEntity?>(null) }

    LaunchedEffect(Unit) {
        scope.launch { listings = db.listingDao().getAllListings() }
    }

    val cs = MaterialTheme.colorScheme
    val ty = MaterialTheme.typography

    // --- Compute the visible list (filter + sort) efficiently ---
    val visibleListings by remember(listings, selectedFilter, selectedSort) {
        derivedStateOf {
            val filtered = listings.filter { selectedFilter == "All" || it.type.equals(selectedFilter, ignoreCase = true) }
            when (selectedSort) {
                "Newest" -> filtered.sortedByDescending { sortKey(it.date, newest = true) }
                "Oldest" -> filtered.sortedBy { sortKey(it.date, newest = false) }
                "Status" -> filtered.sortedWith(
                    compareBy<ListingEntity> { (it.status ?: "").lowercase() }
                        .thenByDescending { sortKey(it.date, newest = true) }
                )
                else -> filtered
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("History", style = ty.titleMedium, color = cs.onPrimary) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = cs.primary,
                    titleContentColor = cs.onPrimary,
                    navigationIconContentColor = cs.onPrimary,
                    actionIconContentColor = cs.onPrimary
                ),
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {

            // Filter row
            Row(modifier = Modifier.padding(8.dp)) {
                AppFilterChip(label = "All",   selected = selectedFilter == "All")   { selectedFilter = "All" }
                Spacer(Modifier.width(8.dp))
                AppFilterChip(label = "Lost",  selected = selectedFilter == "Lost")  { selectedFilter = "Lost" }
                Spacer(Modifier.width(8.dp))
                AppFilterChip(label = "Found", selected = selectedFilter == "Found") { selectedFilter = "Found" }
            }

            // Sort row
            Row(modifier = Modifier.padding(8.dp)) {
                AppFilterChip(label = "Newest", selected = selectedSort == "Newest") { selectedSort = "Newest" }
                Spacer(Modifier.width(8.dp))
                AppFilterChip(label = "Oldest", selected = selectedSort == "Oldest") { selectedSort = "Oldest" }
                Spacer(Modifier.width(8.dp))
                AppFilterChip(label = "Status", selected = selectedSort == "Status") { selectedSort = "Status" }
            }

            if (visibleListings.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No items to show", style = ty.bodyMedium, color = cs.onSurfaceVariant)
                }
            } else {
                LazyColumn {
                    items(visibleListings) { item ->
                        HistoryItemCardFromEntity(item = item, onClick = { selectedItem = item })
                    }
                }
            }

            selectedItem?.let { item ->
                AlertDialog(
                    onDismissRequest = { selectedItem = null },
                    confirmButton = { TextButton(onClick = { selectedItem = null }) { Text("Close") } },
                    title = { Text(item.title, style = ty.titleMedium) },
                    text = {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text("Category: ${item.category}", style = ty.bodyMedium)
                            Text("Type: ${item.type}", style = ty.bodyMedium)
                            Text("Date: ${item.date}", style = ty.bodyMedium)
                            Text("Status: ${item.status ?: "-"}", style = ty.bodyMedium)
                            Text("Location: ${item.location}", style = ty.bodyMedium)
                            Text("Contact: ${item.contact}", style = ty.bodyMedium)
                            Spacer(modifier = Modifier.height(8.dp))
                            HorizontalDivider(
                                Modifier.fillMaxWidth(),
                                thickness = 1.dp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.12f)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Description:", style = ty.labelLarge)
                            Text(item.description, style = ty.bodySmall)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun HistoryItemCardFromEntity(
    item: ListingEntity,
    onClick: () -> Unit
) {
    val cs = MaterialTheme.colorScheme
    val ty = MaterialTheme.typography

    ElevatedCard(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = cs.surface),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 3.dp),
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(item.imageUrl)
                    .crossfade(true)
                    .error(com.project.foundoncampus.R.drawable.ic_launcher_foreground)
                    .placeholder(com.project.foundoncampus.R.drawable.ic_launcher_foreground)
                    .build(),
                contentDescription = item.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(84.dp)
                    .clip(RoundedCornerShape(12.dp))
            )

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    style = ty.titleMedium,
                    color = cs.onSurface,
                    maxLines = 1
                )

                Spacer(Modifier.height(6.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TypeChip(item.type)
                    StatusChip(item.status)
                }

                Spacer(Modifier.height(8.dp))

                MetaRow(icon = Icons.Filled.CalendarToday, text = item.date.ifBlank { "—" })

                val where = listOf(item.campus, item.location)
                    .filter { it.isNotBlank() }
                    .joinToString(" • ")
                    .ifBlank { "—" }
                MetaRow(icon = Icons.Filled.Place, text = where)

                if (!item.contact.isNullOrBlank()) {
                    MetaRow(icon = Icons.Filled.Phone, text = item.contact)
                }
            }

            Spacer(Modifier.width(8.dp))

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = cs.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun MetaRow(icon: ImageVector, text: String) {
    val cs = MaterialTheme.colorScheme
    val ty = MaterialTheme.typography
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = cs.onSurfaceVariant, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(6.dp))
        Text(text, style = ty.bodySmall, color = cs.onSurfaceVariant, maxLines = 1)
    }
}

@Composable
private fun TypeChip(type: String?) {
    val cs = MaterialTheme.colorScheme
    val t = type.orEmpty().lowercase()

    val (bg, fg, iconTint) = when (t) {
        "found" -> Triple(cs.primary, cs.onPrimary, cs.onPrimary)
        "lost" -> Triple(cs.error, Color.Black, Color.Black)
        else -> Triple(cs.surfaceVariant, cs.onSurfaceVariant, cs.onSurfaceVariant)
    }

    AssistChip(
        onClick = {},
        label = { Text(type.orEmpty().ifBlank { "—" }, style = MaterialTheme.typography.labelMedium) },
        leadingIcon = {
            Icon(
                imageVector = if (t == "found") Icons.Filled.CheckCircle else Icons.Filled.ReportProblem,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(16.dp)
            )
        },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = bg,
            labelColor = fg
        )
    )
}

@Composable
private fun StatusChip(status: String?) {
    val s = (status ?: "").lowercase()
    val cs = MaterialTheme.colorScheme

    val (bg, fg) = when (s) {
        "claimed" -> FreshGreen to cs.onSurface
        "pending" -> cs.error to Color.Black
        else -> cs.surfaceVariant to cs.onSurfaceVariant
    }

    AssistChip(
        onClick = {},
        label = { Text(status ?: "—", style = MaterialTheme.typography.labelMedium) },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = bg,
            labelColor = fg
        )
    )
}

@Composable
fun AppFilterChip(label: String, selected: Boolean, onClick: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    androidx.compose.material3.FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = cs.secondary,
            selectedLabelColor = cs.onSurface,
            containerColor = cs.surfaceVariant,
            labelColor = cs.onSurfaceVariant
        )
    )
}

/* ----------------- Sorting helpers ----------------- */

private fun sortKey(dateStr: String?, newest: Boolean): Long {
    val parsed = parseDateMillis(dateStr)
    return parsed ?: if (newest) Long.MIN_VALUE else Long.MAX_VALUE
}

private fun parseDateMillis(dateStr: String?): Long? {
    if (dateStr.isNullOrBlank()) return null
    return try {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply { isLenient = false }
        sdf.parse(dateStr)?.time
    } catch (_: Exception) {
        null
    }
}
