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

    @Query("SELECT * FROM listings WHERE userEmail = :email ORDER BY id DESC")
    suspend fun getListingsByUserEmail(email: String): List<ListingEntity>

    @Query("SELECT * FROM listings WHERE id = :id")
    suspend fun getListingById(id: Int): ListingEntity?
    
    @Update
    fun updateListing(updated: ListingEntity)

    @Query("SELECT * FROM listings WHERE type = :type ORDER BY date DESC LIMIT :limit")
    suspend fun getRecentListingsByType(type: String, limit: Int): List<ListingEntity>

    @Query("SELECT * FROM listings WHERE status = 'Claimed' ORDER BY date DESC LIMIT :limit")
    suspend fun getRecentClaimedListings(limit: Int): List<ListingEntity>

    @Query("SELECT * FROM listings WHERE type = :type ORDER BY date DESC")
    suspend fun getAllByType(type: String): List<ListingEntity>

    @Query("SELECT * FROM listings WHERE status = 'Claimed' ORDER BY date DESC")
    suspend fun getAllClaimed(): List<ListingEntity>

}

