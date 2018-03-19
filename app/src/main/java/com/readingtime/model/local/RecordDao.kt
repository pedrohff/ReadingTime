package com.readingtime.model.local

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.readingtime.model.Record
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single

/**
 * Created by pedro on 15/01/18.
 */
@Dao
interface RecordDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(record: Record)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(records: List<Record>)

    @Query("SELECT * FROM Record WHERE bookId = :bookid")
    fun listAll(bookid: String): Single<List<Record>>

    @Query("SELECT * FROM Record WHERE bookId = :bookid ORDER BY date DESC LIMIT 1")
    fun last(bookid: String): Maybe<Record>
}