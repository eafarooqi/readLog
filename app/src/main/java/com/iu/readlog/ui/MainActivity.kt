package com.iu.readlog.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.iu.readlog.R
import com.iu.readlog.data.AppDatabase
import com.iu.readlog.repository.BookJournalRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Main dashboard activity displaying the scrollable list of recorded book logs.
 * Manages UI state (empty view vs. populated list) and coordinates navigation
 * to the Add and Detail screens.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var repository: BookJournalRepository
    private lateinit var bookAdapter: BookAdapter
    private lateinit var rvBookEntries: RecyclerView
    private lateinit var layoutEmptyState: LinearLayout
    private lateinit var fabAddEntry: FloatingActionButton
    private lateinit var toolbarMain: MaterialToolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val database = AppDatabase.getDatabase(this)
        repository = BookJournalRepository(database.bookJournalDao())

        toolbarMain = findViewById(R.id.toolbarMain)
        rvBookEntries = findViewById(R.id.rvBookEntries)
        layoutEmptyState = findViewById(R.id.layoutEmptyState)
        fabAddEntry = findViewById(R.id.fabAddEntry)

        // Setup RecyclerView & Adapter
        bookAdapter = BookAdapter { selectedEntry ->
            val intent = Intent(this, DetailActivity::class.java).apply {
                putExtra("ENTRY_ID", selectedEntry.id)
            }
            startActivity(intent)
        }

        rvBookEntries.layoutManager = LinearLayoutManager(this)
        rvBookEntries.adapter = bookAdapter

        fabAddEntry.setOnClickListener {
            val intent = Intent(this, AddEntryActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        loadEntries()
    }

    private fun loadEntries() {
        lifecycleScope.launch(Dispatchers.IO) {
            val entries = repository.getAllEntries()

            withContext(Dispatchers.Main) {
                if (entries.isEmpty()) {
                    rvBookEntries.visibility = View.GONE
                    layoutEmptyState.visibility = View.VISIBLE
                } else {
                    layoutEmptyState.visibility = View.GONE
                    rvBookEntries.visibility = View.VISIBLE
                    bookAdapter.updateData(entries)
                }
            }
        }
    }
}