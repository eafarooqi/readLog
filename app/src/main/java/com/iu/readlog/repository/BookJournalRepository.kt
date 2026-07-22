package com.iu.readlog.repository

import com.iu.readlog.data.BookJournalDao
import com.iu.readlog.data.BookJournalEntry

/**
 * Repository class for managing book journal data.
 * abstract layer for database
 */
class BookJournalRepository(private val bookJournalDao: BookJournalDao) {

    /**
     * Fetches all book logs from the database, sorted on creation date.
     */
    suspend fun getAllEntries(): List<BookJournalEntry> {
        return bookJournalDao.getAllEntries()
    }

    /**
     * Get single book log with unique database primary ID.
     */
    suspend fun getEntryById(id: Long): BookJournalEntry? {
        return bookJournalDao.getEntryById(id)
    }

    /**
     * Inserts a new book log entry. Return the inserted row ID.
     */
    suspend fun insertEntry(entry: BookJournalEntry): Long {
        return bookJournalDao.insertEntry(entry)
    }

    /**
     * Updates an existing book log entry in database.
     */
    suspend fun updateEntry(entry: BookJournalEntry) {
        bookJournalDao.updateEntry(entry)
    }

    /**
     * Permanently deletes a specific book log entry from database.
     */
    suspend fun deleteEntry(entry: BookJournalEntry) {
        bookJournalDao.deleteEntry(entry)
    }
}