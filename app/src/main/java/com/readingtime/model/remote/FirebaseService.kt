package com.readingtime.model.remote

import com.readingtime.model.Book
import com.readingtime.model.FirebaseUserBook
import com.readingtime.model.Record
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Created by pedro on 29/12/17.
 */
interface FirebaseService {

    companion object {
        val FIREBASE_URL = "https://pizzagent-e12b9.firebaseio.com/"
    }

    //BOOKS
    @GET("books.json")
    fun booksList(): Observable<Map<String, Book>>

    @GET("books/{id}.json")
    fun bookFind(@Path("id")id: String): Observable<Book>

    //RECORDS
    @GET("records/{bookId}.json")
    fun recordslistOld(@Path("bookId")
                       bookId: String?,
                       @Query("orderBy")
                       orderBy: String = "\"\$key\"",
                       @Query("limitToLast")
                       limit: Int = 10): Observable<Map<String, Record>>


    @GET("records/{userId}/{bookId}.json")
    fun recordslist(
            @Path("bookId")
            bookId: String?,
            @Path("userId")
            userId: String = "pedro",
            @Query("orderBy")
            orderBy: String = "\"\$key\"",
            @Query("limitToLast")
            limit: Int = 10
    ): Observable<Map<String, Record>>

    @GET("records/{userId}/{bookId}.json")
    fun findLastRecord(
            @Path("userId")
            userId: String,
            @Path("bookId")
            bookId: String,
            @Query("orderBy")
            orderBy: String = "\"\$key\"",
            @Query("limitToLast")
            limit: Int = 1
    ): Maybe<HashMap<String, Record>>

    @GET("records/{userId}/{bookId}/{recordDateString}.json")
    fun recordsFind(
            @Path("bookId")
            bookId: String,
            @Path("recordDateString")
            recordDateString: String,
            @Path("userId")
            userID: String = "pedro"
    ): Observable<Record>

    //BOOKUSERS
    @GET("userbooks/{userId}.json")
    fun listUserBooks(
            @Path("userId")
            userId: String = "pedro",
            @Query("orderBy")
            orderBy: String = "\"lastVisit\"",
            @Query("limitToLast")
            limit: Int = 10
    ): Observable<Map<String, FirebaseUserBook>>

    @GET("userbooks/{userId}/{bookId}.json")
    fun findUserBook(
            @Path("userId")
            userId: String = "pedro",
            @Path("bookId")
            bookId: String
    ): Observable<FirebaseUserBook>
}