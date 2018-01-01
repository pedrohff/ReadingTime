package com.readingtime.model.remote

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.GsonBuilder
import com.readingtime.extensions.removeHMS
import com.readingtime.model.Book
import com.readingtime.model.BookUI
import com.readingtime.model.Record
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

    fun saveBook(book: Book) {
        val key = fbRef.push().key
        book.id = key
        fbRef.child("books").child(key).setValue(book)

        var record: Record = Record.construct(book, 0, null, 0, Date().removeHMS())
        fbRef.child("records").child(book.id).child(record.id).setValue(record)
    }

    fun updateBook(book: Book) {

    }

    fun findBook(id: String): rx.Observable<BookUI> {
        return service.bookFind(id)
                .flatMap { book ->
                    Observable.zip(
                            Observable.just(book),
                            listRecords(book.id).toList(),
                            { t1: Book?, t2: MutableList<Record>? -> BookUI.construct(t1, t2) }
                    )
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

    fun listRecords(bookId: String?): rx.Observable<Record> {
        return service.recordslist(bookId)
                .flatMap { record -> Observable.from(record.values) }
    }
}