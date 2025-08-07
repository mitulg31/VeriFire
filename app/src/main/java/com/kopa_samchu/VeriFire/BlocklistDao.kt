package com.kopa_samchu.VeriFire

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface BlocklistDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: BlocklistItem)

    @Delete
    suspend fun delete(item: BlocklistItem)

    @Query("SELECT * FROM blocklist_items ORDER BY value ASC")
    fun getAllItems(): LiveData<List<BlocklistItem>>

    @Query("SELECT * FROM blocklist_items")
    suspend fun getAllItemsList(): List<BlocklistItem>
}