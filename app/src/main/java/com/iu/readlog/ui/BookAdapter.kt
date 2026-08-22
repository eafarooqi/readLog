package com.iu.readlog.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.iu.readlog.R
import com.iu.readlog.data.BookJournalEntry
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Adapter that connects our book list data to the RecyclerView so it displays on screen.
 *
 * It handles creating book item views, filling them with book data, and handling clicks.
 *
 * @param entries The current list of book entries to display.
 * @param onItemClick The action to run when a user taps on any book card.
 */
class BookAdapter(
    private var entries: List<BookJournalEntry> = emptyList(),
    private val onItemClick: (BookJournalEntry) -> Unit
) : RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    /**
     * Holds and remembers all the views inside a single book card (item_book_entry.xml).
     * This stops the app from searching for views again every time if user scrolls.
     *
     * @param itemView The main view for one book row in the list.
     */
    class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tvBookTitle)
        val tvAuthor: TextView = itemView.findViewById(R.id.tvBookAuthor)
        val tvStatus: TextView = itemView.findViewById(R.id.tvBookStatus)
        val tvRating: TextView = itemView.findViewById(R.id.tvBookRating)
        val tvDate: TextView = itemView.findViewById(R.id.tvBookDate)
    }

    /**
     * XML layout file for a single book item and creates a new ViewHolder for it.
     *
     * @param parent The parent ViewGroup that contains this view.
     * @param viewType The view type of the new View.
     * @return A new instance of [BookViewHolder] holding the created view.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_book_entry, parent, false)
        return BookViewHolder(view)
    }

    /**
     * Takes the book data at the given position and puts it into the card's text views.
     * Also sets up the click listener for the item.
     *
     * @param holder The ViewHolder that contains the views to update.
     * @param position The index of the book item in the list.
     */
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val entry = entries[position]

        // Set the book details to their respective TextViews
        holder.tvTitle.text = entry.title
        holder.tvAuthor.text = "by ${entry.author}"
        holder.tvStatus.text = entry.status
        holder.tvRating.text = "★ ${entry.rating}/5"

        // Format the timestamp to a readable date format
        val dateFormatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        holder.tvDate.text = dateFormatter.format(Date(entry.entryDate))

        // Open Detail screen on item click
        holder.itemView.setOnClickListener {
            onItemClick(entry)
        }
    }

    /**
     * Tells the RecyclerView how many total books are in our list.
     *
     * @return The total number of items to display.
     */
    override fun getItemCount(): Int = entries.size

    /**
     * Updates the adapter with a fresh list of books and refreshes the whole screen.
     *
     * @param newEntries The new list of book entries from the database.
     */
    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newEntries: List<BookJournalEntry>) {
        this.entries = newEntries
        notifyDataSetChanged()
    }
}