package com.readingtime.model.remote

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.GsonBuilder
import com.readingtime.model.Book
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import rx.Observable

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

    fun getCache(bookId: String): Observable<Book> {
        return Observable.from(booksCache.keys)
                .filter { key ->
                    key == bookId
                }
                .map { key ->
                    booksCache[key]
                }
    }
}