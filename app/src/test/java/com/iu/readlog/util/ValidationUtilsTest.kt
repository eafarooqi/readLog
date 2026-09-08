package com.iu.readlog.util

import org.junit.Assert.*
import org.junit.Test

class ValidationUtilsTest {

    // Title should not be empty or contain only spaces.
    @Test
    fun `validateTitle returns false for empty or blank title`() {
        assertFalse(ValidationUtils.validateTitle("").isValid)
        assertFalse(ValidationUtils.validateTitle("   ").isValid)
        assertEquals("Book title cannot be empty.", ValidationUtils.validateTitle("").errorMessage)
    }

    // A book title with no issues should pass validation.
    @Test
    fun `validateTitle returns true for valid title`() {
        assertTrue(ValidationUtils.validateTitle("The Great Gatsby").isValid)
        assertNull(ValidationUtils.validateTitle("The Great Gatsby").errorMessage)
    }

    // Author is required, so empty values should fail.
    @Test
    fun `validateAuthor returns false for empty or blank author`() {
        assertFalse(ValidationUtils.validateAuthor("").isValid)
        assertFalse(ValidationUtils.validateAuthor("   ").isValid)
        assertEquals("Author name cannot be empty.", ValidationUtils.validateAuthor("").errorMessage)
    }

    // Author with normal name should pass validation.
    @Test
    fun `validateAuthor returns true for valid author`() {
        assertTrue(ValidationUtils.validateAuthor("F. Scott Fitzgerald").isValid)
    }

    // Negative page numbers are not allowed.
    @Test
    fun `validateCurrentPage returns false for negative page`() {
        assertFalse(ValidationUtils.validateCurrentPage(-1).isValid)
        assertEquals("Current page cannot be negative.", ValidationUtils.validateCurrentPage(-1).errorMessage)
    }

    // Zero is okay because the user may not have started the book yet.
    @Test
    fun `validateCurrentPage returns true for zero or positive page`() {
        assertTrue(ValidationUtils.validateCurrentPage(0).isValid)
        assertTrue(ValidationUtils.validateCurrentPage(100).isValid)
    }

    // Ratings are limited to the 1-5 only.
    @Test
    fun `validateRating returns false for rating out of 1-5 range`() {
        assertFalse(ValidationUtils.validateRating(0).isValid)
        assertFalse(ValidationUtils.validateRating(6).isValid)
        assertEquals("Rating must be between 1 and 5.", ValidationUtils.validateRating(0).errorMessage)
    }

    // Normal rating between 1 to 5 should pass.
    @Test
    fun `validateRating returns true for rating between 1 and 5`() {
        assertTrue(ValidationUtils.validateRating(1).isValid)
        assertTrue(ValidationUtils.validateRating(3).isValid)
        assertTrue(ValidationUtils.validateRating(5).isValid)
    }

    // Open Library URL is optional, so blank input is fine.
    @Test
    fun `validateOpenLibraryUrl returns true for empty string (optional)`() {
        assertTrue(ValidationUtils.validateOpenLibraryUrl("").isValid)
        assertTrue(ValidationUtils.validateOpenLibraryUrl("   ").isValid)
    }

    // Only regular HTTP/HTTPS links are accepted.
    @Test
    fun `validateOpenLibraryUrl returns false for invalid URL protocol`() {
        assertFalse(ValidationUtils.validateOpenLibraryUrl("ftp://openlibrary.org").isValid)
        assertFalse(ValidationUtils.validateOpenLibraryUrl("www.google.com").isValid)
        assertEquals(
            "URL must start with http:// or https://",
            ValidationUtils.validateOpenLibraryUrl("www.google.com").errorMessage
        )
    }

    // Both HTTP and HTTPS URLs should be accepted.
    @Test
    fun `validateOpenLibraryUrl returns true for http or https URL`() {
        assertTrue(ValidationUtils.validateOpenLibraryUrl("http://openlibrary.org/books/1").isValid)
        assertTrue(ValidationUtils.validateOpenLibraryUrl("https://openlibrary.org/books/1").isValid)
    }
}