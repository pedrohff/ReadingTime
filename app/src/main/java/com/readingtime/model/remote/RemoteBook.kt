package com.readingtime.model.remote

import com.readingtime.model.Book
import com.readingtime.model.UserBook
import io.reactivex.Observable

/**
 * Created by pedro on 08/01/18.
 */
object RemoteBook {

    /**
     * @param book the book to save
     * @param userId the user ID, will be used in case saveToUserBook is true
     * @param saveToUserBook helper to save a book to the Book instance and also to the UserBook one
     * @param provKey helper to generate a key, so the book's cover has the book's ID as it name
     * @param onComplete function to execute when the book is saved
     */
    fun save(book: Book, userId: String = "pedro", saveToUserBook: Boolean = true, provKey: String? = null, onComplete: () -> Unit = {}) {
        validateBook(book)

        val key: String = if (provKey == null) FirebaseProvider.fbRef.push().key else provKey
        book.id = key
        FirebaseProvider
                .fbRef
                .child("books")
                .child(key)
                .setValue(book) { dbError, dbRef ->
                    if (!saveToUserBook) onComplete()
                }

//        var record: Record = Record.construct(book, 0, null, 0, Date().removeHMS())
//        FirebaseProvider.fbRef.child("records").child(userId).child(book.id).child(record.id).setValue(record)

        if (saveToUserBook) {
            //TODO Userbookprovider.save
            RemoteUserBook.save(UserBook(book.id, book), onComplete = onComplete)
        }
    }

//    fun updateBook(book: Book) {
//        TODO("not implemented")
//    }

    fun findById(id: String): Observable<Book> {
        return FirebaseProvider.service.bookFind(id)
    }

    private fun validateBook(book: Book) {
        with(book) {
            if (name == null || name == "")
                throw IllegalArgumentException("Book's Name can't be empty") //TODO strings

            if (author == null || author == "")
                throw IllegalArgumentException("Book's Author can't be empty") //TODO strings

            if (publisher == null || publisher == "")
                throw IllegalArgumentException("Book's Publisher can't be empty") //TODO strings

            if (type == null || type == "")
                throw IllegalArgumentException("Book's Type can't be empty") //TODO strings

            if (pages == null || pages == 0)
                throw IllegalArgumentException("Book's Page Count can't be empty") //TODO strings

            if (category == null || category == "")
                throw IllegalArgumentException("Book's Category can't be empty") //TODO strings

            if (coverURL == null || coverURL == "")
                throw IllegalArgumentException("Book's Cover can't be empty") //TODO strings
        }
    }


}