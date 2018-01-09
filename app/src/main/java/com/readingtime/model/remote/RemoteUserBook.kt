package com.readingtime.model.remote

import com.readingtime.extensions.removeHMS
import com.readingtime.model.Book
import com.readingtime.model.Record
import com.readingtime.model.UserBook
import rx.Observable
import java.util.*

/**
 * Created by pedro on 08/01/18.
 */

object RemoteUserBook : RemoteDatabaseHelper() {

    fun save(userBook: UserBook, userId: String = "pedro") {
        FirebaseProvider.fbRef.child("userbooks").child(userId).child(userBook.book.id).setValue(userBook)

        var record: Record = Record.construct(userBook.book, 0, null, 0, Date().removeHMS())
        FirebaseProvider.fbRef.child("records").child(userId).child(userBook.book.id).child(record.id).setValue(record)
    }

    fun update(userBook: UserBook, userId: String = "pedro") {
        FirebaseProvider.fbRef.child("userbooks").child(userId).child(userBook.book.id).setValue(userBook)
    }

    fun findById(bookId: String, userId: String = "pedro"): Observable<UserBook> {
        return FirebaseProvider.service.findUserBook(userId, bookId)
                .map { fbuBook -> fbuBook.toUserBook() }
                .flatMap { book: UserBook? ->
                    Observable.zip(
                            Observable.just(book),
                            Observable.concat(
                                    FirebaseProvider.getCache(book!!.id),
                                    RemoteBook.findById(book.id).doOnNext { tBook -> FirebaseProvider.booksCache.put(tBook.id, tBook) }
                            ).first(),
                            { uBook: UserBook?, bk: Book? -> uBook?.book = bk!!; uBook }
                    )
                }
    }

    fun listAll(userId: String = "pedro"): rx.Observable<UserBook> {
        return FirebaseProvider.service.listUserBooks(userId = userId)
                .flatMap { fbuBooksMap -> Observable.from(fbuBooksMap.values) }
                .map { fbuBook -> fbuBook.toUserBook() }
                .flatMap { book: UserBook? ->
                    Observable.zip(
                            Observable.just(book),
                            Observable.concat(
                                    FirebaseProvider.getCache(book!!.id),
                                    RemoteBook.findById(book.id).doOnNext { tBook -> FirebaseProvider.booksCache.put(tBook.id, tBook) }
                            ).first(),
                            { uBook: UserBook?, bk: Book? -> uBook?.book = bk!!; uBook }
                    )
                }
    }

    fun delete(typeId: String, userId: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}