package com.readingtime.model.local

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.readingtime.model.Book
import io.reactivex.Single

/**
 * Created by pedro on 15/01/18.
 */
@Dao
interface BookDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(book: Book)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(books: List<Book>)

//    @Update(onConflict = OnConflictStrategy.REPLACE)
//    fun update(book: Book)

    @Query("SELECT * FROM book")
    fun listAll(): Single<List<Book>>

    @Query("SELECT * FROM book WHERE book_id = :id")
    fun findById(id: String): Single<Book>

}