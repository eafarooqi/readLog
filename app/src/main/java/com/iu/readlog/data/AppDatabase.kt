package com.iu.readlog.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * The main database class that acts as the primary access point to the SQLite database.
 * Defines the database configuration and provides access to the DAO Class.
 */
@Database(entities = [BookJournalEntry::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    /**
     * Abstract factory method.
     * Underlying implementation will be generated automatically at runtime.
     * * @return The Data Access Object (DAO) instance managing database operations.
     */
    abstract fun bookJournalDao(): BookJournalDao

    companion object {

        // Database instance variable.
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Singleton access function to retrieve or initialize the application's database instance.
         *
         * @return The thread-safe Singleton AppDatabase instance.
         */
        fun getDatabase(context: Context): AppDatabase {
            // If the instance already exists, return it immediately to save performance overhead.
            return INSTANCE ?: synchronized(this) {
                // Double-checked locking to verify another thread didn't create it while waiting.
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "readlog_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}