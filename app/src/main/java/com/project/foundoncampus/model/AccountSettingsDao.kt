package com.project.foundoncampus.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface AccountSettingsDao {
    @Query("SELECT * FROM account_settings WHERE userEmail = :email LIMIT 1")
    suspend fun get(email: String): AccountSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(settings: AccountSettingsEntity)

    @Update
    suspend fun update(settings: AccountSettingsEntity)
}
