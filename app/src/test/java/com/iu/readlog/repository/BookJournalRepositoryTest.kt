package com.iu.readlog.repository

import com.iu.readlog.data.BookJournalDao
import com.iu.readlog.data.BookJournalEntry
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Testing the Repository by mocking the DAO.
 * This tests if the repository passes the data correctly between the app and the DAO.
 */
class BookJournalRepositoryTest {

    // Mock the DAO to avoid setting up a real database.
    private val dao: BookJournalDao = mockk()
    private val repository = BookJournalRepository(dao)

    @Test
    fun `getAllEntries calls dao getAllEntries and returns list`() = runTest {
        // Fake data.
        val mockEntries = listOf(
            BookJournalEntry(
                id = 1,
                title = "Test Book",
                author = "Author",
                status = "Reading",
                currentPage = 10,
                favoriteQuote = "Quote",
                notes = "Notes",
                rating = 5,
                openLibraryUrl = "",
                entryDate = 123456789L
            )
        )
        
        // Tell the mock DAO what to return.
        coEvery { dao.getAllEntries() } returns mockEntries

        val result = repository.getAllEntries()

        // Verify result matches and check if DAO was actually called.
        assertEquals(mockEntries, result)
        coVerify { dao.getAllEntries() }
    }

    @Test
    fun `getEntryById calls dao getEntryById`() = runTest {
        val entryId = 1L
        val mockEntry = BookJournalEntry(
            id = entryId,
            title = "Test Book",
            author = "Author",
            status = "Reading",
            currentPage = 10,
            favoriteQuote = "Quote",
            notes = "Notes",
            rating = 5,
            openLibraryUrl = "",
            entryDate = 123456789L
        )
        
        coEvery { dao.getEntryById(entryId) } returns mockEntry

        val result = repository.getEntryById(entryId)

        assertEquals(mockEntry, result)
        coVerify { dao.getEntryById(entryId) }
    }

    @Test
    fun `insertEntry calls dao insertEntry`() = runTest {
        val entry = BookJournalEntry(
            title = "New Book",
            author = "Author",
            status = "Want to read",
            currentPage = 0,
            favoriteQuote = "",
            notes = "",
            rating = 0,
            openLibraryUrl = "",
            entryDate = 123456789L
        )
        
        // Return a fake ID after "insertion"
        coEvery { dao.insertEntry(entry) } returns 1L

        val result = repository.insertEntry(entry)

        assertEquals(1L, result)
        coVerify { dao.insertEntry(entry) }
    }

    @Test
    fun `updateEntry calls dao updateEntry`() = runTest {

        // Example of an existing entry with updated values.
        val entry = BookJournalEntry(
            id = 1,
            title = "Updated Book",
            author = "Author",
            status = "Finished",
            currentPage = 100,
            favoriteQuote = "Nice",
            notes = "Done",
            rating = 5,
            openLibraryUrl = "",
            entryDate = 123456789L
        )
        
        coEvery { dao.updateEntry(entry) } returns Unit

        repository.updateEntry(entry)

        // Just checking if the DAO was triggered.
        coVerify { dao.updateEntry(entry) }
    }

    @Test
    fun `deleteEntry calls dao deleteEntry`() = runTest {

        // Entry that will be passed to the delete function.
        val entry = BookJournalEntry(
            id = 1,
            title = "Book to Delete",
            author = "Author",
            status = "Reading",
            currentPage = 10,
            favoriteQuote = "",
            notes = "",
            rating = 1,
            openLibraryUrl = "",
            entryDate = 123456789L
        )
        
        coEvery { dao.deleteEntry(entry) } returns Unit

        repository.deleteEntry(entry)

        // Make sure the correct entry was sent to the DAO for deletion.
        coVerify { dao.deleteEntry(entry) }
    }
}
