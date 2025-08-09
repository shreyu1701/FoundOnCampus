package com.project.foundoncampus.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profiles")
data class ProfileEntity(
    @PrimaryKey val userId: String,      // e.g., email or androidId
    val fullName: String,
    val phone: String?,
    val studentId: String?,
    val department: String?,
    val avatarUri: String?,
    val updatedAt: Long = System.currentTimeMillis()
)
