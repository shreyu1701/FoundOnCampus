package com.project.foundoncampus.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "account_settings")
data class AccountSettingsEntity(
    @PrimaryKey val userEmail: String,   // same key you already use
    val displayName: String?,            // optional label used in account area (not profile name)
    val emailNotifications: Boolean = true,
    val pushNotifications: Boolean = true,
    val darkModeEnabled: Boolean = false,
    val updatedAt: Long = System.currentTimeMillis()
)
