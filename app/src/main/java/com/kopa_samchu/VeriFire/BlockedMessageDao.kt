package com.kopa_samchu.VeriFire

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface BlockedMessageDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(message: BlockedMessage)

    @Delete
    suspend fun delete(message: BlockedMessage)

    @Query("SELECT * FROM blocked_messages ORDER BY timestamp DESC")
    fun getAllMessages(): LiveData<List<BlockedMessage>>

    @Query("SELECT COUNT(*) FROM blocked_messages")
    fun getMessageCount(): LiveData<Int>
}