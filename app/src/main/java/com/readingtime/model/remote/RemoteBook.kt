package com.readingtime.model.remote

import com.readingtime.extensions.removeHMS
import com.readingtime.model.Book
import com.readingtime.model.Record
import com.readingtime.model.UserBook
import io.reactivex.Flowable
import io.reactivex.Observable
import java.util.*

/**
 * Created by pedro on 08/01/18.
 */
object RemoteBook {

    fun save(book: Book, userId: String = "pedro", saveToUserBook: Boolean = true, provKey: String? = null) {
        val key: String = if (provKey == null) FirebaseProvider.fbRef.push().key else provKey
        book.id = key
        FirebaseProvider.fbRef.child("books").child(key).setValue(book)

        var record: Record = Record.construct(book, 0, null, 0, Date().removeHMS())
        FirebaseProvider.fbRef.child("records").child(userId).child(book.id).child(record.id).setValue(record)

        if (saveToUserBook) {
            RemoteUserBook.save(UserBook(book.id, book))
        }
    }

//    fun updateBook(book: Book) {
//        TODO("not implemented")
//    }

    fun findById(id: String): Observable<Book> {
        return FirebaseProvider.service.bookFind(id)
    }

}