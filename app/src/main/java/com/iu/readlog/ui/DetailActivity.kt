package com.iu.readlog.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.iu.readlog.R
import com.iu.readlog.data.AppDatabase
import com.iu.readlog.data.BookJournalEntry
import com.iu.readlog.repository.BookJournalRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Detail screen activity.
 * It shows all the saved information for a single book entry and allows the user to:
 * - Read full notes and quotes.
 * - Open the Open Library webpage in an external browser.
 * - Navigate to the Edit screen.
 * - User can delete the book record from the local database.
 */
class DetailActivity : AppCompatActivity() {

    // Database repository instance
    private lateinit var repository: BookJournalRepository

    // Tracks the current entry ID passed from MainActivity
    private var currentEntryId: Long = -1L
    private var currentEntry: BookJournalEntry? = null

    // UI elements
    private lateinit var toolbarDetail: MaterialToolbar
    private lateinit var tvTitle: TextView
    private lateinit var tvAuthor: TextView
    private lateinit var tvStatus: TextView
    private lateinit var tvPage: TextView
    private lateinit var tvRating: TextView
    private lateinit var tvQuote: TextView
    private lateinit var tvNotes: TextView
    private lateinit var btnOpenLibrary: MaterialButton
    private lateinit var btnEditEntry: MaterialButton
    private lateinit var btnDeleteEntry: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        // Initialize the local database and repository
        val database = AppDatabase.getDatabase(this)
        repository = BookJournalRepository(database.bookJournalDao())

        // Read the entry ID passed via Intent extra
        currentEntryId = intent.getLongExtra("ENTRY_ID", -1L)

        // Find and Attach click listeners to all buttons and toolbar
        bindViews()
        setupListeners()
    }

    override fun onResume() {
        super.onResume()
        // Refresh or load the book details on focus
        if (currentEntryId != -1L) {
            loadEntryDetails(currentEntryId)
        } else {
            Toast.makeText(this, "Error loading entry.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    /**
     * Finds and connects all XML view components to their Kotlin variables.
     */
    private fun bindViews() {
        toolbarDetail = findViewById(R.id.toolbarDetail)
        tvTitle = findViewById(R.id.tvDetailTitle)
        tvAuthor = findViewById(R.id.tvDetailAuthor)
        tvStatus = findViewById(R.id.tvDetailStatus)
        tvPage = findViewById(R.id.tvDetailPage)
        tvRating = findViewById(R.id.tvDetailRating)
        tvQuote = findViewById(R.id.tvDetailQuote)
        tvNotes = findViewById(R.id.tvDetailNotes)
        btnOpenLibrary = findViewById(R.id.btnOpenLibrary)
        btnEditEntry = findViewById(R.id.btnEditEntry)
        btnDeleteEntry = findViewById(R.id.btnDeleteEntry)
    }

    /**
     * Sets up click handling for navigation, external browser launch, edit, and delete actions.
     */
    private fun setupListeners() {
        // Top back button closes the detail view
        toolbarDetail.setNavigationOnClickListener {
            finish()
        }

        // Opens the Open Library book webpage using an implicit Android Intent
        btnOpenLibrary.setOnClickListener {
            currentEntry?.let { entry ->
                if (entry.openLibraryUrl.isNotBlank()) {
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(entry.openLibraryUrl))
                    startActivity(browserIntent)
                }
            }
        }

        // Opens EditEntryActivity and passes along the current book's ID
        btnEditEntry.setOnClickListener {
            val intent = Intent(this, EditEntryActivity::class.java).apply {
                putExtra("ENTRY_ID", currentEntryId)
            }
            startActivity(intent)
        }

        // Triggers the delete confirmation popup
        btnDeleteEntry.setOnClickListener {
            showDeleteConfirmationDialog()
        }
    }

    /**
     * Loads the book entry from the Room database in the background (IO thread)
     * and updates the UI on the main thread.
     */
    private fun loadEntryDetails(id: Long) {
        lifecycleScope.launch(Dispatchers.IO) {
            val entry = repository.getEntryById(id)
            withContext(Dispatchers.Main) {
                if (entry != null) {
                    currentEntry = entry
                    populateUI(entry)
                } else {
                    finish()
                }
            }
        }
    }

    /**
     * Load all text fields and controls with the loaded book data.
     */
    private fun populateUI(entry: BookJournalEntry) {
        tvTitle.text = entry.title
        tvAuthor.text = "by ${entry.author}"
        tvStatus.text = entry.status
        tvPage.text = "Page: ${entry.currentPage}"
        tvRating.text = "★ ${entry.rating} / 5"

        // Display default placeholder text if optional fields are empty
        tvQuote.text = if (entry.favoriteQuote.isNotBlank()) entry.favoriteQuote else getString(R.string.default_no_quote)
        tvNotes.text = if (entry.notes.isNotBlank()) entry.notes else getString(R.string.default_no_notes)

        // Display default placeholder text if optional fields are empty
        if (entry.openLibraryUrl.isNotBlank()) {
            btnOpenLibrary.visibility = View.VISIBLE
        } else {
            btnOpenLibrary.visibility = View.GONE
        }
    }

    /**
     * Displays an alert dialog to ask the user for confirmation before deleting.
     * Prevents accidental deletion of records.
     */
    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Delete Entry")
            .setMessage("Are you sure you want to delete this journal entry?")
            .setPositiveButton("Delete") { _, _ ->
                currentEntry?.let { entry ->
                    // Run database delete operation on background thread
                    lifecycleScope.launch(Dispatchers.IO) {
                        repository.deleteEntry(entry)
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@DetailActivity, "Entry deleted", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}