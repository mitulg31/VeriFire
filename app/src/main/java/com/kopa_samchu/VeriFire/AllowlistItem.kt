package com.kopa_samchu.VeriFire

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "allowlist_items")
data class AllowlistItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val phoneNumber: String
)
