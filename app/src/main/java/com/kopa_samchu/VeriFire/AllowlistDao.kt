package com.kopa_samchu.VeriFire

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface AllowlistDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: AllowlistItem)

    @Delete
    suspend fun delete(item: AllowlistItem)

    @Query("SELECT * FROM allowlist_items ORDER BY phoneNumber ASC")
    fun getAllItems(): LiveData<List<AllowlistItem>>

    @Query("SELECT * FROM allowlist_items")
    suspend fun getAllItemsList(): List<AllowlistItem> // For non-live access
}