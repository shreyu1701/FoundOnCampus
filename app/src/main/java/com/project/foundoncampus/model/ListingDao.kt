package com.project.foundoncampus.model

import androidx.room.*

@Dao
interface ListingDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertListing(listing: ListingEntity)

    @Query("SELECT * FROM listings ORDER BY id DESC")
    suspend fun getAllListings(): List<ListingEntity>

    @Query("DELETE FROM listings")
    suspend fun clearAll()

    @Delete
    suspend fun deleteListing(listing: ListingEntity)
}

