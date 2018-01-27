package com.readingtime.model.remote

import com.readingtime.extensions.removeHMS
import com.readingtime.model.Book
import com.readingtime.model.FirebaseUserBook
import com.readingtime.model.Record
import com.readingtime.model.UserBook
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import java.util.*

/**
 * Created by pedro on 08/01/18.
 */

object RemoteUserBook : RemoteDatabaseHelper() {

    fun save(userBook: UserBook, userId: String = "pedro", onComplete: () -> Unit = {}) {
        FirebaseProvider
                .fbRef
                .child("userbooks")
                .child(userId)
                .child(userBook.book.id)
                .setValue(userBook) { dbError, dbRef ->
                    var record: Record = Record.construct(userBook.book, 0, null, 0, Date().removeHMS())
                    RemoteRecord.save(record, onComplete = onComplete, bookId = userBook.book.id)
                }
    }

    fun update(userBook: UserBook, userId: String = "pedro", onComplete: () -> Unit = {}) {
        FirebaseProvider
                .fbRef
                .child("userbooks")
                .child(userId)
                .child(userBook.book.id)
                .setValue(userBook) { dbError, dbRef ->
                    onComplete()
                }
    }

    fun findById(bookId: String, userId: String = "pedro"): Observable<UserBook> {
        return FirebaseProvider.service.findUserBook(userId, bookId)
                .map { fbuBook: FirebaseUserBook -> fbuBook.toUserBook() }
                .flatMap { book: UserBook? ->
                    Observable.zip(
                            Observable.just(book),
                            Observable.concat(
                                    FirebaseProvider.getCache(book!!.id),
                                    RemoteBook.findById(book.id).doOnNext { tBook -> FirebaseProvider.booksCache.put(tBook.id, tBook) }
                            ).firstElement().toObservable(), //TODO validar
                            BiFunction { uBook: UserBook?, bk: Book? -> uBook?.book = bk!!; uBook!! }
                    )
//                    Observable.zip(
//                            Observable.just(book),
//                            Observable.concat(
//                                    FirebaseProvider.getCache(book!!.id),
//                                    RemoteBook.findById(book.id).doOnNext { tBook -> FirebaseProvider.booksCache.put(tBook.id, tBook) }
//                            ).first(),
//                            { uBook: UserBook?, bk: Book? -> uBook?.book = bk!!; uBook }
//                    )
                }
    }

    fun listAll(userId: String = "pedro"): Observable<UserBook>{
        return FirebaseProvider.service.listUserBooks(userId = userId)
                .flatMap { fbuMap: Map<String, FirebaseUserBook>? -> Observable.fromIterable(fbuMap?.values) }
                .map { fbuBook -> fbuBook.toUserBook() }
                .flatMap {
                    book ->
                    Observable.zip(
                            Observable.just(book),
                            Observable.concat(
                                    FirebaseProvider.getCache(book.id),
                                    RemoteBook.findById(book.id).doOnNext { tBook:Book -> FirebaseProvider.booksCache.put(tBook.id, tBook) }
                            ),
                            BiFunction { uBook:UserBook, bk:Book -> uBook.book = bk; uBook }
                    )
                }
    }

    fun listAllNew(userId: String = "pedro"): Observable<MutableList<UserBook>> {
        return FirebaseProvider.service.listUserBooks(userId = userId)
                .flatMap { fbuMap: Map<String, FirebaseUserBook>? -> Observable.fromIterable(fbuMap?.values) }
                .map { fbuBook -> fbuBook.toUserBook() }
                .flatMap { book ->
                    Observable.zip(
                            Observable.just(book),
                            RemoteBook.findById(book.id).doOnNext { tBook: Book -> FirebaseProvider.booksCache.put(tBook.id, tBook) },
                            BiFunction { uBook: UserBook, bk: Book -> uBook.book = bk; uBook }
                    )
                }.toList().toObservable()
    }

    fun delete(typeId: String, userId: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}