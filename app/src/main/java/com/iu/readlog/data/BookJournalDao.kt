package com.iu.readlog.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

/**
 * Data Access Object (DAO) interface for the BookJournalEntry table.
 */
@Dao
interface BookJournalDao {

    /**
     * Inserts a new book journal entry into the local database.
     * * @param entry: The BookJournalEntry record.
     * @return The auto-generated row ID.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: BookJournalEntry): Long

    /**
     * Updates specific data fields of an existing book journal record.
     * * @param entry: The modified BookJournalEntry entity containing the updated fields.
     */
    @Update
    suspend fun updateEntry(entry: BookJournalEntry)

    /**
     * Deletes a targeted book journal record from the local SQLite database.
     * * @param entry: The BookJournalEntry entity to be deleted.
     */
    @Delete
    suspend fun deleteEntry(entry: BookJournalEntry)

    /**
     * Retrieves all book record entries from the database.
     * Sorted in descending order based on creation time, so that
     * newly logged books appear first in the listing.
     * * @return A list containing all saved BookJournalEntry entities.
     */
    @Query("SELECT * FROM BookJournalEntry ORDER BY createdAt DESC")
    suspend fun getAllEntries(): List<BookJournalEntry>

    /**
     * Get one book record by its unique id.
     * * @param id The unique primary key ID of the target journal entry.
     * @return The matching BookJournalEntry object if found; null otherwise.
     */
    @Query("SELECT * FROM BookJournalEntry WHERE id = :id")
    suspend fun getEntryById(id: Long): BookJournalEntry?
}