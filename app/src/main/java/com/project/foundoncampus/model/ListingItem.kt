package com.project.foundoncampus.model

data class ListingItem(
    val type: String, // "Lost" or "Found"
    val item: String,
    val category: String,
    val campus: String,
    val location: String,
    val date: String,
    val itemdescription: String,
    val uploadPhotoUri: String
)
