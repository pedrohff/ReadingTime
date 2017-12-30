package com.readingtime.model.retrofit

import com.google.gson.GsonBuilder
import com.readingtime.model.Book
import com.readingtime.model.Record
import mu.KLogging
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import rx.Observable

/**
 * Created by pedro on 29/12/17.
 */
class BookApi {
    companion object: KLogging()
    val service: IBook

    init {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY

        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor(logging)

        val gson = GsonBuilder().setLenient().create()

        val retrofit = Retrofit.Builder()
                .baseUrl("https://pizzagent-e12b9.firebaseio.com/")
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(httpClient.build())
                .build()

        service = retrofit.create(IBook::class.java)
    }

    fun loadBooks(): rx.Observable<Book>? {
//        val r = service.list()
//        val w = r.flatMap { bookResults -> Observable.from(bookResults.result) }
//        var f = w.toString()
//        logger("----pedrooooo")
//        r.doOnNext { logger(it.toString()) }
//        val x = w.map { book -> Book(book["L1OCl5lsfY6xrEOdlrt"]!!.id,book["L1OCl5lsfY6xrEOdlrt"]!!.name, book["L1OCl5lsfY6xrEOdlrt"]!!.author, book["L1OCl5lsfY6xrEOdlrt"]!!.artist, book["L1OCl5lsfY6xrEOdlrt"]!!.publisher, book["L1OCl5lsfY6xrEOdlrt"]!!.type, book["L1OCl5lsfY6xrEOdlrt"]!!.pages, book["L1OCl5lsfY6xrEOdlrt"]!!.category) }
//        return x

        return service.booksList()
                .flatMap { bookresult-> Observable.from(bookresult.values) }
//                .map { book -> Book(book.id,book.name, book.author, book.artist, book.publisher, book.type, book.pages, book.category) }
    }

    fun listRecords(bookId: String?): rx.Observable<Record> {
        return service.recordslist(bookId)
                .flatMap { record -> Observable.from(record.values) }
//                .flatMap { book: Book -> presenter.book = book; return@flatMap service.recordslist(book.id)}
    }
}