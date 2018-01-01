package com.readingtime.model.remote

import com.readingtime.model.Book
import com.readingtime.model.Record
import retrofit2.http.GET
import retrofit2.http.Path
import rx.Observable

/**
 * Created by pedro on 29/12/17.
 */
interface FirebaseService {

    companion object {
        val FIREBASE_URL = "https://pizzagent-e12b9.firebaseio.com/"
    }

    @GET("books.json")
    fun booksList(): Observable<Map<String, Book>>

    @GET("books/{id}.json")
    fun bookFind(@Path("id")id: String): Observable<Book>

    @GET("records/{bookId}.json")
    fun recordslist(@Path("bookId")bookId: String?): Observable<Map<String,Record>>

    @GET("records/{bookId}/{recordId}.json")
    fun recordsFind(@Path("bookId")bookId:String, recordId:String): Observable<Record>

}