package com.project.foundoncampus.model

data class UserProfile(
    val profilePictureUrl: String,
    val fullName: String,
    val email: String,
    val contactNumber: String,
    val claimedCount: Int,
    val foundedCount: Int,
    val reportedCount: Int
)