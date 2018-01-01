package com.readingtime.model.retrofit

import com.google.gson.GsonBuilder
import com.readingtime.model.Book
import com.readingtime.model.BookPresenter
import com.readingtime.model.Record
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
    val service: IBook

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
                .baseUrl("https://pizzagent-e12b9.firebaseio.com/")
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(httpClient.build())
                .build()

        service = retrofit.create(IBook::class.java)
    }

    fun findBook(id: String): rx.Observable<BookPresenter> {
        return service.bookFind(id)
                .flatMap { book ->
                    Observable.zip(
                            Observable.just(book),
                            listRecords(book.id).toList(),
                            { t1: Book?, t2: MutableList<Record>? -> BookPresenter.construct(t1, t2)  }
                    )
                }
    }


    fun listBooks(): rx.Observable<BookPresenter> {
        return service.booksList()
                .flatMap { bookResult -> Observable.from(bookResult.values)}
                .flatMap { book ->
                    Observable.zip(
                            Observable.just(book),
                            listRecords(book.id).toList(),
                            {t1: Book?, t2: MutableList<Record>? -> BookPresenter.construct(t1,t2) }
                    )
                }
    }

    fun listRecords(bookId: String?): rx.Observable<Record> {
        return service.recordslist(bookId)
                .flatMap { record -> Observable.from(record.values) }
    }
}