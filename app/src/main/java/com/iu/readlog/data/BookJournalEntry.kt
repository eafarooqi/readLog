package com.iu.readlog.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * This class represents a single book journal entry in the SQLite database.
 * This class serves as the Room Database Entity mapping directly to the "BookJournalEntry" table.
 * It saves all the data related to the book.
 */
@Entity(tableName = "BookJournalEntry")
data class BookJournalEntry(

    // Primary key for the SQLite table, automatically incremented with each new entry.
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    // Core book metadata fields.
    val title: String,
    val author: String,

    // User progress trackers.
    val status: String,         // contain the values in UI: "Want to read", "Reading", "Finished".
    val currentPage: Int,       // save current reading page number.

    // User thoughts and notes about the book.
    val favoriteQuote: String,
    val notes: String,

    // User rating about the book.
    val rating: Int,

    // Open Library book link, allowing the user to view additional metadata on the Open Library website.
    val openLibraryUrl: String,

    // Timestamps.
    val entryDate: Long,                              // User-selected or default timestamp for when the log is dated.
    val createdAt: Long = System.currentTimeMillis(), // System timestamp for creation.
    val updatedAt: Long = System.currentTimeMillis()  // System timestamp for modifications.
)