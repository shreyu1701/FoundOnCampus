package com.project.foundoncampus.model

data class HistoryItem(
    val id: String,
    val name: String,
    val type: String, // "Lost" or "Found"
    val date: String,
    val status: String, // "Pending", "Returned", "Closed"
    val thumbnailUrl: String? // Can be null
)