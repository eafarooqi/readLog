package com.iu.readlog.util

/**
 * Data class for validation.
 * Return boolean with a message.
 */
data class ValidationResult(
    val isValid: Boolean,
    val errorMessage: String? = null
)

/**
 * Utility object class for validation
 */
object ValidationUtils {

    /**
     * Validates that the book title cannot be empty.
     */
    fun validateTitle(title: String): ValidationResult {
        return if (title.trim().isEmpty()) {
            ValidationResult(false, "Book title cannot be empty.")
        } else {
            ValidationResult(true)
        }
    }

    /**
     * Validates that the author name cannot be empty.
     */
    fun validateAuthor(author: String): ValidationResult {
        return if (author.trim().isEmpty()) {
            ValidationResult(false, "Author name cannot be empty.")
        } else {
            ValidationResult(true)
        }
    }

    /**
     * Validates that the current page count should not be less than zero.
     */
    fun validateCurrentPage(page: Int): ValidationResult {
        return if (page < 0) {
            ValidationResult(false, "Current page cannot be negative.")
        } else {
            ValidationResult(true)
        }
    }

    /**
     * Validates that the book rating should be between 1 and 5.
     */
    fun validateRating(rating: Int): ValidationResult {
        return if (rating in 1..5) {
            ValidationResult(true)
        } else {
            ValidationResult(false, "Rating must be between 1 and 5.")
        }
    }

    /**
     * validation check for Open Library URL if provided.
     */
    fun validateOpenLibraryUrl(url: String): ValidationResult {
        val trimmed = url.trim()
        if (trimmed.isEmpty()) return ValidationResult(true) // Optional field

        return if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
            ValidationResult(true)
        } else {
            ValidationResult(false, "URL must start with http:// or https://")
        }
    }
}