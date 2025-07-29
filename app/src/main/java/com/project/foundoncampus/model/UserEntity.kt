package com.project.foundoncampus.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val email: String,  // Using email as unique ID
    val name: String,
    val password: String,
    val phone: String?
)
