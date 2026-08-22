package com.iu.readlog.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.iu.readlog.R
import com.iu.readlog.data.AppDatabase
import com.iu.readlog.data.BookJournalEntry
import com.iu.readlog.repository.BookJournalRepository
import com.iu.readlog.util.ValidationUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Activity for user to create and persist a new [BookJournalEntry].
 *
 * Responsibilities:
 * - Render and manage form fields (title, author, status, rating, etc.).
 * - Perform input validation via [ValidationUtils].
 * - Save valid entries to the local Room database via [BookJournalRepository].
 */
class AddEntryActivity : AppCompatActivity() {

    // Data layer dependency
    private lateinit var repository: BookJournalRepository

    // UI Components
    private lateinit var toolbarAdd: MaterialToolbar
    private lateinit var tilTitle: TextInputLayout
    private lateinit var etTitle: TextInputEditText
    private lateinit var tilAuthor: TextInputLayout
    private lateinit var etAuthor: TextInputEditText
    private lateinit var rgStatus: RadioGroup
    private lateinit var tilCurrentPage: TextInputLayout
    private lateinit var etCurrentPage: TextInputEditText
    private lateinit var ratingBar: RatingBar
    private lateinit var tvRatingLabel: TextView
    private lateinit var tilOpenLibraryUrl: TextInputLayout
    private lateinit var etOpenLibraryUrl: TextInputEditText
    private lateinit var tilQuote: TextInputLayout
    private lateinit var etQuote: TextInputEditText
    private lateinit var tilNotes: TextInputLayout
    private lateinit var etNotes: TextInputEditText
    private lateinit var btnSaveEntry: MaterialButton

    /**
     * Sets up the screen layout, connects the database repository, and binds views and click listeners.
     *
     * @param savedInstanceState Saved state data if the activity is being recreated.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_entry)

        // Initialize repository and database
        val database = AppDatabase.getDatabase(this)
        repository = BookJournalRepository(database.bookJournalDao())

        bindViews()
        setupListeners()
    }

    /**
     * Finds and initializes view references.
     */
    private fun bindViews() {
        toolbarAdd = findViewById(R.id.toolbarAdd)
        tilTitle = findViewById(R.id.tilTitle)
        etTitle = findViewById(R.id.etTitle)
        tilAuthor = findViewById(R.id.tilAuthor)
        etAuthor = findViewById(R.id.etAuthor)
        rgStatus = findViewById(R.id.rgStatus)
        tilCurrentPage = findViewById(R.id.tilCurrentPage)
        etCurrentPage = findViewById(R.id.etCurrentPage)
        ratingBar = findViewById(R.id.ratingBar)
        tvRatingLabel = findViewById(R.id.tvRatingLabel)
        tilOpenLibraryUrl = findViewById(R.id.tilOpenLibraryUrl)
        etOpenLibraryUrl = findViewById(R.id.etOpenLibraryUrl)
        tilQuote = findViewById(R.id.tilQuote)
        etQuote = findViewById(R.id.etQuote)
        tilNotes = findViewById(R.id.tilNotes)
        etNotes = findViewById(R.id.etNotes)
        btnSaveEntry = findViewById(R.id.btnSaveEntry)
    }

    /**
     * Registers UI event listeners for navigation, interactive widgets, and form submission.
     */
    @SuppressLint("SetTextI18n")
    private fun setupListeners() {
        // Handle back navigation on toolbar.
        toolbarAdd.setNavigationOnClickListener {
            finish()
        }

        // Updating rating label as soon as user change the rating.
        ratingBar.setOnRatingBarChangeListener { _, rating, _ ->
            tvRatingLabel.text = "Rating: ${rating.toInt()} / 5"
        }

        // Trigger saving process.
        btnSaveEntry.setOnClickListener {
            saveBookEntry()
        }
    }

    /**
     * Get the Form Inputs, perform validation and persist
     * the new [BookJournalEntry] to Database.
     */
    private fun saveBookEntry() {
        val title = etTitle.text.toString().trim()
        val author = etAuthor.text.toString().trim()
        val pageText = etCurrentPage.text.toString().trim()
        val openLibraryUrl = etOpenLibraryUrl.text.toString().trim()
        val quote = etQuote.text.toString().trim()
        val notes = etNotes.text.toString().trim()
        val rating = ratingBar.rating.toInt()

        // Determine selected status.
        val selectedStatusId = rgStatus.checkedRadioButtonId
        val status = if (selectedStatusId != -1) {
            findViewById<RadioButton>(selectedStatusId).text.toString()
        } else {
            "Want to read"
        }

        val page = pageText.toIntOrNull() ?: 0

        // Reset previous validation error messages.
        tilTitle.error = null
        tilAuthor.error = null
        tilCurrentPage.error = null
        tilOpenLibraryUrl.error = null

        // Execute validations.
        val titleValidation = ValidationUtils.validateTitle(title)
        if (!titleValidation.isValid) {
            tilTitle.error = titleValidation.errorMessage
            return
        }

        val authorValidation = ValidationUtils.validateAuthor(author)
        if (!authorValidation.isValid) {
            tilAuthor.error = authorValidation.errorMessage
            return
        }

        val pageValidation = ValidationUtils.validateCurrentPage(page)
        if (!pageValidation.isValid) {
            tilCurrentPage.error = pageValidation.errorMessage
            return
        }

        val urlValidation = ValidationUtils.validateOpenLibraryUrl(openLibraryUrl)
        if (!urlValidation.isValid) {
            tilOpenLibraryUrl.error = urlValidation.errorMessage
            return
        }

        // Book Journal entity object.
        val newEntry = BookJournalEntry(
            title = title,
            author = author,
            status = status,
            currentPage = page,
            favoriteQuote = quote,
            notes = notes,
            rating = if (rating < 1) 1 else rating,
            openLibraryUrl = openLibraryUrl,
            entryDate = System.currentTimeMillis()
        )

        // Save to database asynchronously.
        lifecycleScope.launch(Dispatchers.IO) {
            repository.insertEntry(newEntry)
            withContext(Dispatchers.Main) {
                Toast.makeText(this@AddEntryActivity, "Book saved successfully!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}