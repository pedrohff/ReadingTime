package com.readingtime.model.remote

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.GsonBuilder
import com.readingtime.extensions.removeHMS
import com.readingtime.model.Book
import com.readingtime.model.BookUI
import com.readingtime.model.Record
import com.readingtime.model.UserBook
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import rx.Observable
import java.util.*

/**
 * Created by pedro on 29/12/17.
 */
object FirebaseProvider {
    val service: FirebaseService
    val fbRef: DatabaseReference

    var booksCache = mutableMapOf<String, Book>()

    init {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY

        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor(logging)

        val gson = GsonBuilder()
                .setLenient()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create()

        val retrofit = Retrofit.Builder()
                .baseUrl(FirebaseService.FIREBASE_URL)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(httpClient.build())
                .build()

        service = retrofit.create(FirebaseService::class.java)
        fbRef = FirebaseDatabase.getInstance().reference
    }

    //BOOKS
    fun saveBook(book: Book, userId: String = "pedro", saveToUserBook: Boolean = true, provKey: String? = null) {
        val key: String = if (provKey == null) fbRef.push().key else provKey
        book.id = key
        fbRef.child("books").child(key).setValue(book)

        var record: Record = Record.construct(book, 0, null, 0, Date().removeHMS())
        fbRef.child("records").child(userId).child(book.id).child(record.id).setValue(record)

        if (saveToUserBook) {
            saveUserBook(UserBook(book.id, book))
        }
    }

    fun updateBook(book: Book) {
        TODO("not implemented")
    }

    fun findBook(id: String): rx.Observable<Book> {
        return service.bookFind(id)
    }

    fun findBookUI(id: String): rx.Observable<BookUI> {
        return service.bookFind(id)
                .flatMap { book ->
                    Observable.zip(
                            Observable.just(book),
                            listRecords(book.id).toList(),
                            { t1: Book?, t2: MutableList<Record>? -> BookUI.construct(t1, t2) }
                    )
                }
    }

    private fun getCache(bookId: String): Observable<Book> {
        return Observable.from(booksCache.keys)
                .filter { key ->
                    key == bookId
                }
                .map { key ->
                    booksCache[key]
                }
    }


    fun listBooks(): rx.Observable<BookUI> {
        return service.booksList()
                .flatMap { bookResult -> Observable.from(bookResult.values)}
                .flatMap { book ->
                    Observable.zip(
                            Observable.just(book),
                            listRecords(book.id).toList(),
                            { t1: Book?, t2: MutableList<Record>? -> BookUI.construct(t1, t2) }
                    )
                }
    }

    //RECORD
    fun saveRecord(record: Record, bookId: String, userId: String = "pedro") {
        fbRef.child("records").child(userId).child(bookId).child(record.id).setValue(record)
    }

    fun listRecords(bookId: String?): rx.Observable<Record> {
        return service.recordslist(bookId)
                .flatMap { record -> Observable.from(record.values) }
    }

    fun findLastRecord(userId: String = "pedro", bookId: String): Observable<Record> {
        return service.findLastRecord(userId, bookId)
                .map { map -> map.values.first() }
    }

    //USERBOOK
    fun saveUserBook(userBook: UserBook, userId: String = "pedro") {
        fbRef.child("userbooks").child(userId).child(userBook.book.id).setValue(userBook)

        var record: Record = Record.construct(userBook.book, 0, null, 0, Date().removeHMS())
        fbRef.child("records").child(userId).child(userBook.book.id).child(record.id).setValue(record)
    }

    fun updateUserBook(userBook: UserBook, userId: String = "pedro") {
        fbRef.child("userbooks").child(userId).child(userBook.book.id).setValue(userBook)
    }

    fun findUserBook(userId: String = "pedro", bookId: String): Observable<UserBook> {
        return service.findUserBook(userId, bookId)
                .map { fbuBook -> fbuBook.toUserBook() }
                .flatMap { book: UserBook? ->
                    Observable.zip(
                            Observable.just(book),
                            Observable.concat(
                                    getCache(book!!.id),
                                    findBook(book.id).doOnNext { tBook -> booksCache.put(tBook.id, tBook) }
                            ).first(),
                            { uBook: UserBook?, bk: Book? -> uBook?.book = bk!!; uBook }
                    )
                }
    }

    fun listUserBooks(): rx.Observable<UserBook> {
        return service.listUserBooks()
                .flatMap { fbuBooksMap -> Observable.from(fbuBooksMap.values) }
                .map { fbuBook -> fbuBook.toUserBook() }
                .flatMap { book: UserBook? ->
                    Observable.zip(
                            Observable.just(book),
                            Observable.concat(
                                    getCache(book!!.id),
                                    findBook(book.id).doOnNext { tBook -> booksCache.put(tBook.id, tBook) }
                            ).first(),
                            { uBook: UserBook?, bk: Book? -> uBook?.book = bk!!; uBook }
                    )
                }
    }
}