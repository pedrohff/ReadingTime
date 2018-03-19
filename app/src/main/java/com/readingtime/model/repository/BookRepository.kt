package com.readingtime.model.repository

import com.readingtime.model.Book
import com.readingtime.model.local.LocalProvider
import com.readingtime.model.remote.RemoteBook
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

/**
 * Created by pedro on 15/01/18.
 */
object BookRepository {
    private val bookApi = RemoteBook
    private val bookDao = LocalProvider.db.bookDao()

    fun findBook(id: String): Observable<Book> {
        return Observable.concat(
                getBookFromDb(id),
                getBookFromApi(id)
        )
    }

    private fun getBookFromDb(id: String): Observable<Book> {
        return bookDao.findById(id)
                .toObservable()
                .debounce(400, TimeUnit.MILLISECONDS)
    }

    private fun getBookFromApi(id: String): Observable<Book> {
        return bookApi.findById(id)
                .doOnNext {
                    storeBook(it)
                }
    }

    private fun storeBook(book: Book) {
        Observable.fromCallable { bookDao.insert(book) }
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe()
    }

    fun save(book: Book, userId: String = "pedro", saveToUserBook: Boolean = true, provKey: String? = null, onComplete: () -> Unit = {}) {
        RemoteBook.save(book, userId, saveToUserBook, provKey, {
            storeBook(book)
            onComplete()
        })

    }
}