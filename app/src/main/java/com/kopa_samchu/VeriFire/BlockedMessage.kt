package com.kopa_samchu.VeriFire

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "blocked_messages")
data class BlockedMessage(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val sender: String,
    val messageBody: String,
    val timestamp: Long,
    val spamType: String
) : Parcelable