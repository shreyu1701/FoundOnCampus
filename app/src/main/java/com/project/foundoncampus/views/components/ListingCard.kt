package com.project.foundoncampus.views.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.project.foundoncampus.model.ListingEntity

@Composable
fun ListingCard(
    item: ListingEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val cs = MaterialTheme.colorScheme
    val ty = MaterialTheme.typography

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = cs.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        border = CardDefaults.outlinedCardBorder(true)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberAsyncImagePainter(item.imageUrl),
                contentDescription = item.title,
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(12.dp)),
            )

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    item.title,
                    style = ty.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = cs.primary
                )
                Spacer(Modifier.height(2.dp))
                Text(item.category, style = ty.bodySmall, color = cs.onSurfaceVariant)
                Text("Date: ${item.date}", style = ty.bodySmall, color = cs.onSurfaceVariant)
                Text(
                    "Status: ${item.status ?: "-"}",
                    style = ty.bodySmall,
                    color = statusColor(item.status)
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                IconButton(onClick = onEdit) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = cs.secondary
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = cs.error
                    )
                }
            }
        }
    }
}

@Composable
private fun statusColor(status: String?): androidx.compose.ui.graphics.Color {
    val cs = MaterialTheme.colorScheme
    return when ((status ?: "").lowercase()) {
        "claimed", "resolved" -> cs.tertiary.takeIf { it != cs.primary } ?: cs.primary
        "pending" -> cs.secondary
        else -> cs.onSurfaceVariant
    }
}