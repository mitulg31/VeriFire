package com.kopa_samchu.VeriFire

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "blocklist_items")
data class BlocklistItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val value: String,
    val type: String
)
