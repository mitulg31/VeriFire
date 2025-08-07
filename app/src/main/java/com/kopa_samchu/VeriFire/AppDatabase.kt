package com.kopa_samchu.VeriFire

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [BlockedMessage::class, BlocklistItem::class, AllowlistItem::class], version = 2, exportSchema = false) // Increment version to 2
abstract class AppDatabase : RoomDatabase() {

    abstract fun blockedMessageDao(): BlockedMessageDao
    abstract fun blocklistDao(): BlocklistDao
    abstract fun allowlistDao(): AllowlistDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "verifire_database"
                )
                    .fallbackToDestructiveMigration() // Simple migration for this project
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}