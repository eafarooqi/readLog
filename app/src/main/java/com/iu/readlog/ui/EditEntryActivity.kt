package com.iu.readlog.ui

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
 * Edit screen activity.
 * It fetches an existing book record from the local Room database, populates the input fields,
 * validates any user changes, and saves the updated entry back to storage.
 */
class EditEntryActivity : AppCompatActivity() {

    // Repository for database operations
    private lateinit var repository: BookJournalRepository

    // Tracks the ID and existing data of the book being edited
    private var currentEntryId: Long = -1L
    private var existingEntry: BookJournalEntry? = null

    // UI elements
    private lateinit var toolbarEdit: MaterialToolbar
    private lateinit var tilEditTitle: TextInputLayout
    private lateinit var etEditTitle: TextInputEditText
    private lateinit var tilEditAuthor: TextInputLayout
    private lateinit var etEditAuthor: TextInputEditText
    private lateinit var rgEditStatus: RadioGroup
    private lateinit var rbEditWantToRead: RadioButton
    private lateinit var rbEditReading: RadioButton
    private lateinit var rbEditFinished: RadioButton
    private lateinit var tilEditCurrentPage: TextInputLayout
    private lateinit var etEditCurrentPage: TextInputEditText
    private lateinit var ratingBarEdit: RatingBar
    private lateinit var tvEditRatingLabel: TextView
    private lateinit var tilEditOpenLibraryUrl: TextInputLayout
    private lateinit var etEditOpenLibraryUrl: TextInputEditText
    private lateinit var tilEditQuote: TextInputLayout
    private lateinit var etEditQuote: TextInputEditText
    private lateinit var tilEditNotes: TextInputLayout
    private lateinit var etEditNotes: TextInputEditText
    private lateinit var btnUpdateEntry: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_entry)

        // Initialize the database and repository
        val database = AppDatabase.getDatabase(this)
        repository = BookJournalRepository(database.bookJournalDao())

        // Retrieve the entry ID passed from DetailActivity
        currentEntryId = intent.getLongExtra("ENTRY_ID", -1L)

        bindViews()
        setupListeners()
        loadExistingData()
    }

    /**
     * Connects all layout views to their corresponding Kotlin properties.
     */
    private fun bindViews() {
        toolbarEdit = findViewById(R.id.toolbarEdit)
        tilEditTitle = findViewById(R.id.tilEditTitle)
        etEditTitle = findViewById(R.id.etEditTitle)
        tilEditAuthor = findViewById(R.id.tilEditAuthor)
        etEditAuthor = findViewById(R.id.etEditAuthor)
        rgEditStatus = findViewById(R.id.rgEditStatus)
        rbEditWantToRead = findViewById(R.id.rbEditWantToRead)
        rbEditReading = findViewById(R.id.rbEditReading)
        rbEditFinished = findViewById(R.id.rbEditFinished)
        tilEditCurrentPage = findViewById(R.id.tilEditCurrentPage)
        etEditCurrentPage = findViewById(R.id.etEditCurrentPage)
        ratingBarEdit = findViewById(R.id.ratingBarEdit)
        tvEditRatingLabel = findViewById(R.id.tvEditRatingLabel)
        tilEditOpenLibraryUrl = findViewById(R.id.tilEditOpenLibraryUrl)
        etEditOpenLibraryUrl = findViewById(R.id.etEditOpenLibraryUrl)
        tilEditQuote = findViewById(R.id.tilEditQuote)
        etEditQuote = findViewById(R.id.etEditQuote)
        tilEditNotes = findViewById(R.id.tilEditNotes)
        etEditNotes = findViewById(R.id.etEditNotes)
        btnUpdateEntry = findViewById(R.id.btnUpdateEntry)
    }

    /**
     * Configures click listeners for toolbar navigation, rating changes, and update button.
     */
    private fun setupListeners() {
        // Navigate back when top navigation icon is pressed
        toolbarEdit.setNavigationOnClickListener {
            finish()
        }

        // Keep the rating label text updated whenever the stars change
        ratingBarEdit.setOnRatingBarChangeListener { _, rating, _ ->
            tvEditRatingLabel.text = "Rating: ${rating.toInt()} / 5"
        }

        // Validate and update the record on click
        btnUpdateEntry.setOnClickListener {
            updateBookEntry()
        }
    }

    /**
     * Queries the database to fetch the current entry's details
     * and fills the form fields.
     */
    private fun loadExistingData() {
        lifecycleScope.launch(Dispatchers.IO) {
            val entry = repository.getEntryById(currentEntryId)
            withContext(Dispatchers.Main) {
                if (entry != null) {
                    existingEntry = entry

                    // Fill input fields with current data
                    etEditTitle.setText(entry.title)
                    etEditAuthor.setText(entry.author)
                    etEditCurrentPage.setText(entry.currentPage.toString())
                    ratingBarEdit.rating = entry.rating.toFloat()
                    tvEditRatingLabel.text = "Rating: ${entry.rating} / 5"
                    etEditOpenLibraryUrl.setText(entry.openLibraryUrl)
                    etEditQuote.setText(entry.favoriteQuote)
                    etEditNotes.setText(entry.notes)

                    // Match the saved status with the correct radio button
                    when (entry.status) {
                        "Reading" -> rbEditReading.isChecked = true
                        "Finished" -> rbEditFinished.isChecked = true
                        else -> rbEditWantToRead.isChecked = true
                    }
                } else {
                    Toast.makeText(this@EditEntryActivity, "Entry not found.", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }

    /**
     * Validates modified input values and saves the updated object back to the database.
     */
    private fun updateBookEntry() {
        val title = etEditTitle.text.toString().trim()
        val author = etEditAuthor.text.toString().trim()
        val pageText = etEditCurrentPage.text.toString().trim()
        val openLibraryUrl = etEditOpenLibraryUrl.text.toString().trim()
        val quote = etEditQuote.text.toString().trim()
        val notes = etEditNotes.text.toString().trim()
        val rating = ratingBarEdit.rating.toInt()

        // Read selected status from radio group
        val selectedStatusId = rgEditStatus.checkedRadioButtonId
        val status = if (selectedStatusId != -1) {
            findViewById<RadioButton>(selectedStatusId).text.toString()
        } else {
            "Want to read"
        }

        val page = pageText.toIntOrNull() ?: 0

        // Reset previous error messages
        tilEditTitle.error = null
        tilEditAuthor.error = null
        tilEditCurrentPage.error = null
        tilEditOpenLibraryUrl.error = null

        // Title validation
        val titleValidation = ValidationUtils.validateTitle(title)
        if (!titleValidation.isValid) {
            tilEditTitle.error = titleValidation.errorMessage
            return
        }

        // Author validation
        val authorValidation = ValidationUtils.validateAuthor(author)
        if (!authorValidation.isValid) {
            tilEditAuthor.error = authorValidation.errorMessage
            return
        }

        // Page count validation
        val pageValidation = ValidationUtils.validateCurrentPage(page)
        if (!pageValidation.isValid) {
            tilEditCurrentPage.error = pageValidation.errorMessage
            return
        }

        // Open Library URL validation
        val urlValidation = ValidationUtils.validateOpenLibraryUrl(openLibraryUrl)
        if (!urlValidation.isValid) {
            tilEditOpenLibraryUrl.error = urlValidation.errorMessage
            return
        }

        // Rating validation
        val ratingValidation = ValidationUtils.validateRating(rating)
        if (!ratingValidation.isValid) {
            Toast.makeText(this, ratingValidation.errorMessage, Toast.LENGTH_SHORT).show()
            return
        }

        // Create updated copy retaining original ID and creation timestamp
        existingEntry?.let { entry ->
            val updated = entry.copy(
                title = title,
                author = author,
                status = status,
                currentPage = page,
                favoriteQuote = quote,
                notes = notes,
                rating = rating,
                openLibraryUrl = openLibraryUrl,
                updatedAt = System.currentTimeMillis()
            )

            // Asynchronously update record in database
            lifecycleScope.launch(Dispatchers.IO) {
                repository.updateEntry(updated)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@EditEntryActivity, "Entry updated successfully!", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }
}