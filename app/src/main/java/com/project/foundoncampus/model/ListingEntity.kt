package com.project.foundoncampus.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "listings")
data class ListingEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val category: String,
    val type: String,
    val date: String,
    val campus: String,
    val location: String,
    val status: String?,
    val contact: String,
    val userEmail: String,
    val imageUrl: String?,
    val claimedDate: Date? = null
)
