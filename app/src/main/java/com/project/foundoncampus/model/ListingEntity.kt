package com.project.foundoncampus.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "listings")
data class ListingEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val category: String,
    val type: String,
    val date: String,
    val status: String?,
    val contact: String,
    val userEmail: String
)
